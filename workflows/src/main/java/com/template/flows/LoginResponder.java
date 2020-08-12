package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

// ******************
// * Issue Metal Responder flow *
// ******************
@InitiatedBy(Login.class)
public class LoginResponder extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public LoginResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("Transfer Metal received.");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));

    }
}
