package com.template.flows.kyc;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(ApproveKYC.class)
public class ApproveKYCResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public ApproveKYCResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("KYC Approved.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
