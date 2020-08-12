package com.template;

import com.google.common.collect.ImmutableList;
import com.template.contracts.KYCContract;
import com.template.flows.ApproveKYC;
import com.template.flows.SubmitKYC;
import com.template.states.KYCState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.TransactionState;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KYCFlowTests {
    private final MockNetwork network = new MockNetwork(new MockNetworkParameters(ImmutableList.of(
        TestCordapp.findCordapp("com.template.contracts"),
        TestCordapp.findCordapp("com.template.flows")
    )));

    private final StartedMockNode buyer = network.createNode();
    private final StartedMockNode seller = network.createNode();
    private final StartedMockNode lender = network.createNode();

    @Before
    public void setup() {
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    /****************** SUBMIT KYC FLOW TESTS ********************/

    @Test
    public void transactionHasNoInputAndOneOutputStateAndHasCorrectParty() throws Exception {
        SubmitKYC flow = new SubmitKYC(4843035, "BPZPR4763F", "SFSEE5323K", 345345345, "Google Inc.",
                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0));
        CordaFuture<SignedTransaction> signedTransactionCordaFuture = buyer.startFlow(flow);
        setup();
        SignedTransaction signedTransaction = signedTransactionCordaFuture.get();

        assertEquals(0, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
        KYCState output = signedTransaction.getTx().outputsOfType(KYCState.class).get(0);

        assertEquals(buyer.getInfo().getLegalIdentities().get(0), output.getSubmittedBy());
        assertEquals(lender.getInfo().getLegalIdentities().get(0), output.getApprovedOrRejectedBy());
    }

    @Test
    public void transactionHasCorrectContractWithOneSubmitKYCCommandAndBuyerAsSigner() throws Exception {
        SubmitKYC flow = new SubmitKYC(4843035, "BPZPR4763F", "SFSEE5323K", 345345345, "Google Inc.",
                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0));
        CordaFuture<SignedTransaction> signedTransactionCordaFuture = buyer.startFlow(flow);
        setup();
        SignedTransaction signedTransaction = signedTransactionCordaFuture.get();

        TransactionState transactionState = signedTransaction.getTx().getOutputs().get(0);
        assertEquals("com.template.contracts.KYCContract", transactionState.getContract());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert(command.getValue() instanceof KYCContract.SubmitKYC);

        assertEquals(1, command.getSigners().size());
        assertTrue(command.getSigners().contains(buyer.getInfo().getLegalIdentities().get(0).getOwningKey()));
    }

    /** APPROVE KYC FLOW TESTS **/

    @Test
    public void transactionHasOneInputAndOneOutput() throws Exception {
        SubmitKYC submitKYCFlow = new SubmitKYC(4843035, "BPZPR4763F", "SFSEE5323K", 345345345, "Google Inc.",
                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0));
        ApproveKYC approveKYCFlow = new ApproveKYC("234234");

        CordaFuture<SignedTransaction> submitKYCFuture = buyer.startFlow(submitKYCFlow);
        setup();

        CordaFuture<SignedTransaction> approveKYCFuture = buyer.startFlow(approveKYCFlow);
        setup();

        SignedTransaction signedTransaction = approveKYCFuture.get();

        assertEquals(1, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());

    }
//
//    /****************** TRANSFER METAL FLOW TESTS ********************/
//
//    @Test
//    public void transactionHasOneInputAndOneOutput() throws Exception {
//        IssueMetal issueFlow = new IssueMetal("gold", 10, traderA.getInfo().getLegalIdentities().get(0));
//        TransferMetal transferMetal = new TransferMetal("gold", 10, traderB.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> issueFuture = Mint.startFlow(issueFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> transferFuture = traderA.startFlow(transferMetal);
//        setup();
//
//        SignedTransaction signedTransaction = transferFuture.get();
//
//        assertEquals(1, signedTransaction.getTx().getInputs().size());
//        assertEquals(1, signedTransaction.getTx().getOutputs().size());
//    }
//
//    @Test
//    public void transactionHasOneTransferCommandWithOwnerAsSigner() throws Exception {
//        IssueMetal issueFlow = new IssueMetal("gold", 10, traderA.getInfo().getLegalIdentities().get(0));
//        TransferMetal transferMetal = new TransferMetal("gold", 10, traderB.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> issueFuture = Mint.startFlow(issueFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> transferFuture = traderA.startFlow(transferMetal);
//        setup();
//
//        SignedTransaction signedTransaction = transferFuture.get();
//
//        assertEquals(1, signedTransaction.getTx().getCommands().size());
//        Command command = signedTransaction.getTx().getCommands().get(0);
//
//        assert (command.getValue() instanceof MetalContract.Transfer);
//        assertTrue(command.getSigners().contains(traderA.getInfo().getLegalIdentities().get(0).getOwningKey()));
//
//    }
}
