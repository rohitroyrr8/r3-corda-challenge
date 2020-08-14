package com.template.flows.orders;

import co.paralleluniverse.fibers.Suspendable;
import com.template.flows.orders.MarkReceivedPurchaseOrder;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(MarkReceivedPurchaseOrder.class)
public class MarkReceivedPurchaseOrderResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public MarkReceivedPurchaseOrderResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("Purchase Order marked received.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
