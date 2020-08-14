package com.template.flows.auth;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.MetalContract;
import com.template.contracts.UserContract;
import com.template.states.MetalState;
import com.template.states.UserState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Date;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class Login extends FlowLogic<SignedTransaction> {
    private int index = 0;
    private String username;
    private String password;

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

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
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

        StateAndRef<UserState> inputStateStateAndRef = null;
        inputStateStateAndRef = this.checkForUserState();

        Party owner = inputStateStateAndRef.getState().getData().getOwner();
        Party lender =inputStateStateAndRef.getState().getData().getLender();

        UserState transactionState = inputStateStateAndRef.getState().getData();
        UserState outputState = new UserState(transactionState.getIdentifier(), transactionState.getOrganisationName(),
                transactionState.getCountry(), transactionState.getEmail(), transactionState.getUsername(), transactionState.getPassword(),
                transactionState.getRegisteredAs(), transactionState.getStatus(), transactionState.getCreatedOn(), new Date(), owner, lender);

        Command command = new Command(new UserContract.Login(), getOurIdentity().getOwningKey());

        // generating transaction
        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState)
                .addCommand(command);
        transactionBuilder.addInputState(inputStateStateAndRef);

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

    private StateAndRef<UserState> checkForUserState() throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        List<StateAndRef<UserState>> userStateAndRefList = getServiceHub().getVaultService().queryBy(UserState.class, queryCriteria).getStates();

        boolean isFound = false;
        for(int i=0; i < userStateAndRefList.size(); i++) {
            if(userStateAndRefList.get(i).getState().getData().getUsername().equals(username)
            && userStateAndRefList.get(i).getState().getData().getPassword().equals(password)) {
                isFound = true;
                index = i;
                break;
            }
        }

        if(!isFound) {throw new FlowException("No un-consumed state found."); }
        return userStateAndRefList.get(index);
    }
}
