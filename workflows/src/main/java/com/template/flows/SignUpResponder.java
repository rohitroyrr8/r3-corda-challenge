package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(SignUp.class)
public class SignUpResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public SignUpResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("User Registered.");

        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
