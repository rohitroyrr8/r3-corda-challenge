package com.template.contracts;

import com.template.states.KYCState;
import com.template.states.MetalState;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class KYCStateTests {
    private final MockServices ledgerServices = new MockServices();

    private final Party Buyer = new TestIdentity(new CordaX500Name("Buyer", "London", "GB")).getParty();
    private final Party Seller = new TestIdentity(new CordaX500Name("Seller", "London", "GB")).getParty();
    private final Party Lender = new TestIdentity(new CordaX500Name("Lender", "London", "GB")).getParty();

    @Test
    public void metalStateImplementsContractState() {
        assertTrue(new KYCState("123241", "Google Inc.", "Buyer",
                48124341, "VDGED43SJ", "FSGS3445J",
                5342432, "Google Inc.", new Date(), "Gurgaon",
                799, 4333, "Approved", new Date(), Buyer, Lender) instanceof ContractState);
    }

    @Test
    public void metalStateHasTwoParticipants() {
        /** metal state contain two state issuer and owner **/
        KYCState state = new KYCState("123241", "Google Inc.", "Buyer",
                48124341, "VDGED43SJ", "FSGS3445J",
                5342432, "Google Inc.", new Date(), "Gurgaon",
                799, 4333, "Approved", new Date(), Buyer, Lender);

        assertEquals(2, state.getParticipants().size());
        assertTrue(state.getParticipants().contains(Buyer));
        assertTrue(state.getParticipants().contains(Lender));
    }

    @Test
    public void metalStateHasGettersForAllFields() {
        KYCState state = new KYCState("123241", "Google Inc.", "Buyer",
                48124341, "VDGED43SJ", "FSGS3445J",
                5342432, "Google Inc.", new Date(), "Gurgaon",
                799, 4333, "Approved", new Date(), Buyer, Lender);


        assertEquals("123241", state.getIdentifier());
        assertEquals("Google Inc.", state.getVirtualOrganisation());
        assertEquals("Buyer", state.getPartyName());
        assertEquals(48124341, state.getAadharNumber());
        assertEquals("VDGED43SJ", state.getPanNumber());
        assertEquals("FSGS3445J", state.getCompanyPanNumber());
        assertEquals(5342432, state.getIncorporationNumber());
        assertEquals("Google Inc.", state.getCompanyName());
        assertEquals("Gurgaon", state.getIncorporationPlace());
        assertEquals(799, state.getCibilScore());
        assertEquals(4333, state.getCreditLimit());
        assertEquals("Approved", state.getStatus());

        assertEquals(Buyer, state.getOwner());
        assertEquals(Lender, state.getLender());

    }

}