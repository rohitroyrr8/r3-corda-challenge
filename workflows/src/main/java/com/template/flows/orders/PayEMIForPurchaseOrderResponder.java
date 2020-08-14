package com.template.flows.orders;

import co.paralleluniverse.fibers.Suspendable;
import com.template.flows.orders.PayEMIForPurchaseOrder;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(PayEMIForPurchaseOrder.class)
public class PayEMIForPurchaseOrderResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public PayEMIForPurchaseOrderResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("EMI for Purchase Order received.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
