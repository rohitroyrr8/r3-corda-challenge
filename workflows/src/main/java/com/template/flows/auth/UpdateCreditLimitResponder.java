package com.template.flows.auth;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.UserState;
import net.corda.core.flows.*;

@InitiatedBy(UpdateCreditLimit.class)
public class UpdateCreditLimitResponder extends FlowLogic<UserState> {
    private FlowSession otherPartySession;

    public UpdateCreditLimitResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public UserState call() throws FlowException {
        System.out.println("User's credit limit updated successfully.");
        subFlow(new ReceiveFinalityFlow(otherPartySession));
        return null;
    }
}
