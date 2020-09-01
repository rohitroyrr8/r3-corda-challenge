package com.template.flows.orders;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(RaiseInvoiceOnPurchaseOrder.class)
public class RaiseInvoiceOnPurchaseOrderResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public RaiseInvoiceOnPurchaseOrderResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("Invoice against order raised.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
