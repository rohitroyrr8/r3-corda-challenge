package com.template.flows.auth;

import co.paralleluniverse.fibers.Suspendable;
import com.template.flows.auth.Login;
import com.template.states.UserState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

// ******************
// * Issue Metal Responder flow *
// ******************
@InitiatedBy(Login.class)
public class LoginResponder extends FlowLogic<UserState> {
    private FlowSession otherPartySession;

    public LoginResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public UserState call() throws FlowException {
        System.out.println("User logged-in successfully.");
        subFlow(new ReceiveFinalityFlow(otherPartySession));
        return null;
    }
}
