package com.template.flows.kyc;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.KYCContract;
import com.template.enums.KYCStatus;
import com.template.states.KYCState;
import net.corda.core.contracts.Command;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@InitiatingFlow
@StartableByRPC
public class SubmitKYC extends FlowLogic<SignedTransaction> {
    private String identifier;
    private String username;
    private String aadharNumber;
    private String panNumber;
    private String companyPanNumber;
    private int incorporationNumber;
    private String companyName;
    private Date incorporationDate;
    private String incorporationPlace;
    private int cibilScore;

    private Party lender;

    private final ProgressTracker.Step RETRIEVING_NOTARY = new ProgressTracker.Step("Retrieving the notary.");
    private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction.");
    private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing transaction with out private keys.");
    private final ProgressTracker.Step COUNTER_PARTY_SESSION = new ProgressTracker.Step("Sending flow to the counter-party");
    private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and recording transaction.");

    private final ProgressTracker progressTracker = new ProgressTracker(
            RETRIEVING_NOTARY,
            GENERATING_TRANSACTION,
            SIGNING_TRANSACTION,
            COUNTER_PARTY_SESSION,
            FINALISING_TRANSACTION
    );

    public SubmitKYC(String identifier , String username, String aadharNumber, String panNumber, String companyPanNumber, int incorporationNumber,
                     String companyName, Date incorporationDate, String incorporationPlace, int cibilScore,
                     Party lender) {
        this.identifier = identifier;
        this.username = username;
        this.aadharNumber = aadharNumber;
        this.panNumber = panNumber;
        this.companyPanNumber = companyPanNumber;
        this.incorporationNumber = incorporationNumber;
        this.companyName = companyName;
        this.incorporationDate = incorporationDate;
        this.incorporationPlace = incorporationPlace;
        this.cibilScore = cibilScore;
        this.lender = lender;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUsername() {
        return username;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public String getCompanyPanNumber() {
        return companyPanNumber;
    }

    public int getIncorporationNumber() {
        return incorporationNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Date getIncorporationDate() {
        return incorporationDate;
    }

    public String getIncorporationPlace() {
        return incorporationPlace;
    }

    public int getCibilScore() {
        return cibilScore;
    }

    public Party getLender() {
        return lender;
    }

    @Nullable
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {

        // Retrieving notary identity
        progressTracker.setCurrentStep(RETRIEVING_NOTARY);
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //String identifier = CommonUtils.randomAlphaNumeric(16);
        KYCState outputState = new KYCState(identifier, username,
                aadharNumber, panNumber, companyPanNumber, incorporationNumber,
                companyName, incorporationDate, incorporationPlace, cibilScore, 0, KYCStatus.Submitted.toString(),
                new Date(), getOurIdentity(), lender);

        Command command = new Command(new KYCContract.SubmitKYC(), getOurIdentity().getOwningKey());

        // generating transaction
        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState)
                .addCommand(command);

        // signing transaction
        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        // counter party session
        progressTracker.setCurrentStep(COUNTER_PARTY_SESSION);
        FlowSession otherPartySession = initiateFlow(lender);

        // finalising transaction
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new FinalityFlow(signedTransaction, otherPartySession));
    }
}
