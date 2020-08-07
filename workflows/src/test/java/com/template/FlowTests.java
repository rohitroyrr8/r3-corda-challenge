package com.template;

import com.google.common.collect.ImmutableList;
import com.template.contracts.MetalContract;
import com.template.flows.IssueMetal;
import com.template.flows.Responder;
import com.template.flows.TransferMetal;
import com.template.states.MetalState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.TransactionState;
import net.corda.core.transactions.SignedTransaction;
import net.corda.node.Corda;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class FlowTests {
    private final MockNetwork network = new MockNetwork(new MockNetworkParameters(ImmutableList.of(
        TestCordapp.findCordapp("com.template.contracts"),
        TestCordapp.findCordapp("com.template.flows")
    )));

    private final StartedMockNode Mint = network.createNode();
    private final StartedMockNode traderA = network.createNode();
    private final StartedMockNode traderB = network.createNode();

    @Before
    public void setup() {
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    /****************** ISSUE METAL FLOW TESTS ********************/

    @Test
    public void transactionHasNoInputHasMetalStateOutputAndCorrectOwner() throws Exception {
        IssueMetal flow = new IssueMetal("gold", 10, traderA.getInfo().getLegalIdentities().get(0));
        CordaFuture<SignedTransaction> future = Mint.startFlow(flow);
        setup();
        SignedTransaction signedTransaction = future.get();

        assertEquals(0, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
        MetalState output = signedTransaction.getTx().outputsOfType(MetalState.class).get(0);

        assertEquals(traderA.getInfo().getLegalIdentities().get(0), output.getOwner());
    }

    @Test
    public void transactionHasCorrectContractWithOneIssueCommandAndIssuerAsSigner() throws Exception {
        IssueMetal flow = new IssueMetal("gold", 10, traderA.getInfo().getLegalIdentities().get(0));
        CordaFuture<SignedTransaction> future = Mint.startFlow(flow);
        setup();
        SignedTransaction signedTransaction = future.get();

        TransactionState output = signedTransaction.getTx().getOutputs().get(0);
        assertEquals("com.template.contracts.MetalContract", output.getContract());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert (command.getValue() instanceof MetalContract.Issue);

        assertEquals(1, command.getSigners().size());
        assertTrue(command.getSigners().contains(Mint.getInfo().getLegalIdentities().get(0).getOwningKey()));

    }

    /****************** TRANSFER METAL FLOW TESTS ********************/

    @Test
    public void transactionHasOneInputAndOneOutput() throws Exception {
        IssueMetal issueFlow = new IssueMetal("gold", 10, traderA.getInfo().getLegalIdentities().get(0));
        TransferMetal transferMetal = new TransferMetal("gold", 10, traderB.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> issueFuture = Mint.startFlow(issueFlow);
        setup();

        CordaFuture<SignedTransaction> transferFuture = traderA.startFlow(transferMetal);
        setup();

        SignedTransaction signedTransaction = transferFuture.get();

        assertEquals(1, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
    }

    @Test
    public void transactionHasOneTransferCommandWithOwnerAsSigner() throws Exception {
        IssueMetal issueFlow = new IssueMetal("gold", 10, traderA.getInfo().getLegalIdentities().get(0));
        TransferMetal transferMetal = new TransferMetal("gold", 10, traderB.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> issueFuture = Mint.startFlow(issueFlow);
        setup();

        CordaFuture<SignedTransaction> transferFuture = traderA.startFlow(transferMetal);
        setup();

        SignedTransaction signedTransaction = transferFuture.get();

        assertEquals(1, signedTransaction.getTx().getCommands().size());
        Command command = signedTransaction.getTx().getCommands().get(0);

        assert (command.getValue() instanceof MetalContract.Transfer);
        assertTrue(command.getSigners().contains(traderA.getInfo().getLegalIdentities().get(0).getOwningKey()));

    }
}
