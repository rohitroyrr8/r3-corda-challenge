package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

// ******************
// * Issue Metal Responder flow *
// ******************
@InitiatedBy(TransferMetal.class)
public class TransferMetalResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public TransferMetalResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("Transfer Metal received.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
