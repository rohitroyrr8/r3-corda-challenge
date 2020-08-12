package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.KYCContract;
import com.template.contracts.MetalContract;
import com.template.enums.KYCStatus;
import com.template.states.KYCState;
import com.template.states.MetalState;
import com.template.utils.QueryUtils;
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
public class RejectKYC extends FlowLogic<SignedTransaction> {
    private String identifier;
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

    public RejectKYC(String identifier) {
        this.identifier = identifier;
    }

    public int getIndex() {
        return index;
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

        StateAndRef<KYCState> inputStateStateAndRef = null;
        inputStateStateAndRef = new QueryUtils().checkForUnConsumedKYCState(identifier);

        KYCState outputState = new KYCState(inputStateStateAndRef.getState().getData().getIdentifier(),
                inputStateStateAndRef.getState().getData().getVirtualOrganisation(),
                inputStateStateAndRef.getState().getData().getPartyName(),
                inputStateStateAndRef.getState().getData().getAadharNumber(),
                inputStateStateAndRef.getState().getData().getPanNumber(),
                inputStateStateAndRef.getState().getData().getCompanyPanNumber(),
                inputStateStateAndRef.getState().getData().getIncorporationNumber(),
                inputStateStateAndRef.getState().getData().getCompanyName(),
                inputStateStateAndRef.getState().getData().getIncorporationDate(),
                inputStateStateAndRef.getState().getData().getIncorporationPlace(),
                inputStateStateAndRef.getState().getData().getCibilScore(),
                10000,
                KYCStatus.Rejected.toString(),
                inputStateStateAndRef.getState().getData().getCreatedOn(),
                inputStateStateAndRef.getState().getData().getSubmittedBy(),
                getOurIdentity());

        Command command = new Command(new KYCContract.RejectKYC(), getOurIdentity().getOwningKey());

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
        FlowSession otherPartySession = initiateFlow(inputStateStateAndRef.getState().getData().getSubmittedBy());
        FlowSession lenderPartySession = initiateFlow(inputStateStateAndRef.getState().getData().getApprovedOrRejectedBy());

        // finalising transaction
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new FinalityFlow(signedTransaction, otherPartySession, lenderPartySession));
    }

//    private StateAndRef<KYCState> checkForUnConsumedKYCState() throws FlowException {
//        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
//        List<StateAndRef<KYCState>> kycStateAndRefList = getServiceHub().getVaultService().queryBy(KYCState.class, queryCriteria).getStates();
//
//        boolean isFound = false;
//        for(int i = 0; i < kycStateAndRefList.size(); i++) {
//            if(kycStateAndRefList.get(i).getState().getData().getIdentifier().equals(identifier)) {
//                isFound = true;
//                index = i;
//                break;
//            }
//        }
//        if(!isFound) {throw new FlowException("No un-consumed state found."); }
//        return kycStateAndRefList.get(index);
//    }
}
