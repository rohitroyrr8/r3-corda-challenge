package com.template.flows.kyc;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(RejectKYC.class)
public class RejectKYCResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public RejectKYCResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("KYC Rejected.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
