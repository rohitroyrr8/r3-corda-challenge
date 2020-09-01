package com.template.flows.orders;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(PayInvoiceOnPurchaseOrder.class)
public class PayInvoiceOnPurchaseOrderResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public PayInvoiceOnPurchaseOrderResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("Invoice against order paid.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
