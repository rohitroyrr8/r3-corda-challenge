package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.KYCContract;
import com.template.contracts.MetalContract;
import com.template.enums.KYCStatus;
import com.template.states.KYCState;
import com.template.states.MetalState;
import com.template.utils.CommonUtils;
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
    private int aadharNumber;
    private String panNumber;
    private String companyPanNumber;
    private int incorporationNumber;
    private String companyName;
    private Date incorporationDate;
    private String incorporationPlace;
    private int cibilScore;


    private Party approvedOrRejectedBy;

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

    public SubmitKYC(int aadharNumber, String panNumber, String companyPanNumber, int incorporationNumber,
                     String companyName, Date incorporationDate, String incorporationPlace, int cibilScore,
                     Party approvedOrRejectedBy) {
        this.aadharNumber = aadharNumber;
        this.panNumber = panNumber;
        this.companyPanNumber = companyPanNumber;
        this.incorporationNumber = incorporationNumber;
        this.companyName = companyName;
        this.incorporationDate = incorporationDate;
        this.incorporationPlace = incorporationPlace;
        this.cibilScore = cibilScore;
        this.approvedOrRejectedBy = approvedOrRejectedBy;
    }

    public int getAadharNumber() {
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

    public Party getApprovedOrRejectedBy() {
        return approvedOrRejectedBy;
    }

    public ProgressTracker.Step getRETRIEVING_NOTARY() {
        return RETRIEVING_NOTARY;
    }

    public ProgressTracker.Step getGENERATING_TRANSACTION() {
        return GENERATING_TRANSACTION;
    }

    public ProgressTracker.Step getSIGNING_TRANSACTION() {
        return SIGNING_TRANSACTION;
    }

    public ProgressTracker.Step getCOUNTER_PARTY_SESSION() {
        return COUNTER_PARTY_SESSION;
    }

    public ProgressTracker.Step getFINALISING_TRANSACTION() {
        return FINALISING_TRANSACTION;
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

//        MetalState outputState = new MetalState(name, weight, getOurIdentity(), owner);
        KYCState outputState = new KYCState(CommonUtils.randomAlphaNumeric(16), "Lender",
                "Lender", aadharNumber, panNumber, companyPanNumber, incorporationNumber,
                companyName, incorporationDate, incorporationPlace, cibilScore, 0, KYCStatus.Submitted.toString(),
                new Date(), getOurIdentity(), approvedOrRejectedBy);

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
        FlowSession otherPartySession = initiateFlow(approvedOrRejectedBy);

        // finalising transaction
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new FinalityFlow(signedTransaction, otherPartySession));
    }
}
