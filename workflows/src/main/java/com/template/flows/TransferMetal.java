package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.MetalContract;
import com.template.states.MetalState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class TransferMetal extends FlowLogic<SignedTransaction> {
    private String name;
    private int weight;
    private Party newOwner;
    private int index = 0;

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

    public TransferMetal(String name, int weight, Party newOwner) {
        this.name = name;
        this.weight = weight;
        this.newOwner = newOwner;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public Party getNewOwner() {
        return newOwner;
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

        StateAndRef<MetalState> inputStateStateAndRef = null;
        inputStateStateAndRef = this.checkForMetalState();

        Party issuer = inputStateStateAndRef.getState().getData().getIssuer();

        MetalState outputState = new MetalState(name, weight, issuer, newOwner);
        Command command = new Command(new MetalContract.Transfer(), getOurIdentity().getOwningKey());

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
        FlowSession otherPartySession = initiateFlow(newOwner);
        FlowSession issuerPartySession = initiateFlow(issuer);

        // finalising transaction
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new FinalityFlow(signedTransaction, otherPartySession, issuerPartySession));
    }

    private StateAndRef<MetalState> checkForMetalState() throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        List<StateAndRef<MetalState>> metalStateAndRefList = getServiceHub().getVaultService().queryBy(MetalState.class, queryCriteria).getStates();

        boolean isFound = false;
        for(int i=0; i < metalStateAndRefList.size(); i++) {
            if(metalStateAndRefList.get(i).getState().getData().getName().equals(name)
            && metalStateAndRefList.get(i).getState().getData().getWeight() == weight) {
                isFound = true;
                index = i;
                break;
            }
        }

        if(!isFound) {throw new FlowException("No un-consumed state found."); }
        return metalStateAndRefList.get(index);
    }
}
