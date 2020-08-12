package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

// ******************
// * Issue Metal Responder flow *
// ******************
@InitiatedBy(ApproveKYC.class)
public class ApproveKYCResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public ApproveKYCResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("KYC approved successfully.");

        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
