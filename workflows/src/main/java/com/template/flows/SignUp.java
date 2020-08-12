package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.MetalContract;
import com.template.contracts.UserContract;
import com.template.enums.UserStaus;
import com.template.states.MetalState;
import com.template.states.UserState;
import com.template.utils.CommonUtils;
import net.corda.core.contracts.Command;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Date;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class SignUp extends FlowLogic<SignedTransaction> {
    private String organisationName;
    private String country;
    private String email;
    private String username;
    private String password; // need to be hashed
    private String registeredAs;

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

    public SignUp(String organisationName, String country, String email, String username, String password, String registeredAs, Party lender) {
        this.organisationName = organisationName;
        this.country = country;
        this.email = email;
        this.username = username;
        this.password = password;
        this.registeredAs = registeredAs;
        this.lender = lender;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRegisteredAs() {
        return registeredAs;
    }

    public Party getLender() {
        return lender;
    }

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

        UserState outputState = new UserState(CommonUtils.randomAlphaNumeric(16), organisationName, country, email, username,
                password, registeredAs, UserStaus.Active.toString(), new Date(), null, getOurIdentity(), lender);

        Command command = new Command(new UserContract.SignUp(), getOurIdentity().getOwningKey());

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

        FlowSession lenderPartySession = initiateFlow(lender);

        // finalising transaction
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new FinalityFlow(signedTransaction, lenderPartySession));
    }
}
