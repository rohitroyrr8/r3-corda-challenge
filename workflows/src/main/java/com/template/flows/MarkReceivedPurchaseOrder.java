package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.PurchaseOrderContract;
import com.template.enums.PurchaseOrderStatus;
import com.template.states.PurchaseOrderState;
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

@InitiatingFlow
@StartableByRPC
public class MarkReceivedPurchaseOrder extends FlowLogic<SignedTransaction> {
    private int index = 0;
    private String identifier;

    private Party seller;
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

    public MarkReceivedPurchaseOrder(String identifier, Party seller, Party lender) {
        this.identifier = identifier;
        this.seller = seller;
        this.lender = lender;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Party getBuyer() {
        return seller;
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

        StateAndRef<PurchaseOrderState> inputStateStateAndRef = null;
        inputStateStateAndRef = this.checkForMetalState();

        Party seller = inputStateStateAndRef.getState().getData().getSeller();
        Party lender = inputStateStateAndRef.getState().getData().getLender();

        PurchaseOrderState outputState = new PurchaseOrderState(identifier,
                inputStateStateAndRef.getState().getData().getName(),
                inputStateStateAndRef.getState().getData().getModel(),
                inputStateStateAndRef.getState().getData().getCompanyName(),
                inputStateStateAndRef.getState().getData().getColor(),
                inputStateStateAndRef.getState().getData().getFuelType(),
                inputStateStateAndRef.getState().getData().getRate(),
                inputStateStateAndRef.getState().getData().getQuantity(),
                inputStateStateAndRef.getState().getData().getAmount(),
                inputStateStateAndRef.getState().getData().getUsername(),
                inputStateStateAndRef.getState().getData().getCreatedOn(),
                PurchaseOrderStatus.Received.toString(), 0d, getOurIdentity(), seller, lender);

        Command command = new Command(new PurchaseOrderContract.MarkAsReceivedPurchaseOrder(), getOurIdentity().getOwningKey());

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
        FlowSession buyerPartySession = initiateFlow(seller);
        FlowSession lenderPartySession = initiateFlow(lender);

        // finalising transaction
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new FinalityFlow(signedTransaction, buyerPartySession, lenderPartySession));
    }

    private StateAndRef<PurchaseOrderState> checkForMetalState() throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        List<StateAndRef<PurchaseOrderState>> purchaseOrderStateAndRefsList = getServiceHub().getVaultService().queryBy(PurchaseOrderState.class, queryCriteria).getStates();

        boolean isFound = false;
        for(int i=0; i < purchaseOrderStateAndRefsList.size(); i++) {
            if(purchaseOrderStateAndRefsList.get(i).getState().getData().getIdentifier().equals(identifier)) {
                isFound = true;
                index = i;
                break;
            }
        }

        if(!isFound) {throw new FlowException("No un-consumed state found."); }
        return purchaseOrderStateAndRefsList.get(index);
    }
}
