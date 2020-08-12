package com.template.utils;

import com.template.states.KYCState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import java.util.List;

public class QueryUtils extends FlowLogic<SignedTransaction> {
    private int index = 0;

    public StateAndRef<KYCState> checkForUnConsumedKYCState(String identifier) throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        List<StateAndRef<KYCState>> kycStateAndRefList = getServiceHub().getVaultService().queryBy(KYCState.class, queryCriteria).getStates();

        boolean isFound = false;
        for(int i = 0; i < kycStateAndRefList.size(); i++) {
            if(kycStateAndRefList.get(i).getState().getData().getIdentifier().equals(identifier)) {
                isFound = true;
                index = i;
                break;
            }
        }
        if(!isFound) {throw new FlowException("No un-consumed state found."); }
        return kycStateAndRefList.get(index);
    }

    @Override
    public SignedTransaction call() throws FlowException {
        return null;
    }
}
