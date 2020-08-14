package com.template.flows.orders;

import co.paralleluniverse.fibers.Suspendable;
import com.template.flows.orders.SubmitPurchaseOrder;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

// ******************
// * Issue Metal Responder flow *
// ******************
@InitiatedBy(SubmitPurchaseOrder.class)
public class SubmitPurchaseOrderResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public SubmitPurchaseOrderResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("Purchase order submitted..");

        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
