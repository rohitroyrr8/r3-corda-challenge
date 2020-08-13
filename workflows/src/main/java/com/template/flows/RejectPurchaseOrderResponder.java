package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(RejectPurchaseOrder.class)
public class RejectPurchaseOrderResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public RejectPurchaseOrderResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("Purchase Order rejected.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
