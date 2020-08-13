package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(ApprovePurchaseOrder.class)
public class ApprovePurchaseOrderResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public ApprovePurchaseOrderResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("Purchase Order approved.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
