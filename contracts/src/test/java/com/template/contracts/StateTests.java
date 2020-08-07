package com.template.contracts;

import com.template.states.MetalState;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class StateTests {
    private final MockServices ledgerServices = new MockServices();

    private final Party Mint = new TestIdentity(new CordaX500Name("mint", "", "GB")).getParty();
    private final Party Trader = new TestIdentity(new CordaX500Name("trader", "", "GB")).getParty();

    @Test
    public void metalStateImplementsContractState() {
        assertTrue(new MetalState("gold", 10, Mint, Trader) instanceof ContractState);
    }

    @Test
    public void metalStateHasTwoParticipants() {
        /** metal state contain two state issuer and owner **/
        MetalState metalState = new MetalState("gold", 10, Mint, Trader);

        assertEquals(2, metalState.getParticipants().size());
        assertTrue(metalState.getParticipants().contains(Mint));
        assertTrue(metalState.getParticipants().contains(Trader));
    }

    @Test
    public void metalStateHasGettersForAllFields() {
        MetalState metalState = new MetalState("gold", 10, Mint, Trader);

        assertEquals("gold", metalState.getName());
        assertEquals(10, metalState.getWeight());
        assertEquals(Mint, metalState.getIssuer());
        assertEquals(Trader, metalState.getOwner());
    }

}