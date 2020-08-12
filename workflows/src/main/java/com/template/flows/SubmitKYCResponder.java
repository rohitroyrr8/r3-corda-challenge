package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(SubmitKYC.class)
public class SubmitKYCResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public SubmitKYCResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("KYC submitted successfully.");

        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
