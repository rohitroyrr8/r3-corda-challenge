package com.template;

import com.google.common.collect.ImmutableList;
import com.template.contracts.KYCContract;
import com.template.flows.auth.Login;
import com.template.flows.auth.SignUp;
import com.template.flows.auth.UpdateCreditLimit;
import com.template.flows.kyc.ApproveKYC;
import com.template.flows.kyc.RejectKYC;
import com.template.flows.kyc.SubmitKYC;
import com.template.states.KYCState;
import com.template.states.UserState;
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

//    @Test
//    public void transactionHasNoInputAndOneOutputStateAndHasCorrectParty() throws Exception {
//        SubmitKYC submitKYCFlow = new SubmitKYC("KYC_1","rohitroy", "4843035", "BPZPR4763F", "SFSEE5323K",
//                345345345, "Google Inc.",
//                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0), 30000.d);
//        CordaFuture<SignedTransaction> signedTransactionCordaFuture = buyer.startFlow(submitKYCFlow);
//        setup();
//        SignedTransaction signedTransaction = signedTransactionCordaFuture.get();
//
//        assertEquals(0, signedTransaction.getTx().getInputs().size());
//        assertEquals(1, signedTransaction.getTx().getOutputs().size());
//        KYCState output = signedTransaction.getTx().outputsOfType(KYCState.class).get(0);
//
//        assertEquals(buyer.getInfo().getLegalIdentities().get(0), output.getOwner());
//        assertEquals(lender.getInfo().getLegalIdentities().get(0), output.getLender());
//    }
//
//    @Test
//    public void transactionHasCorrectContractWithOneSubmitKYCCommandAndBuyerAsSigner() throws Exception {
//        SubmitKYC submitKYCFlow = new SubmitKYC("KYC_1","rohitroy", "4843035", "BPZPR4763F", "SFSEE5323K",
//                345345345, "Google Inc.",
//                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0), 30000.d);
//        CordaFuture<SignedTransaction> signedTransactionCordaFuture = buyer.startFlow(submitKYCFlow);
//        setup();
//        SignedTransaction signedTransaction = signedTransactionCordaFuture.get();
//
//        TransactionState transactionState = signedTransaction.getTx().getOutputs().get(0);
//        assertEquals("com.template.contracts.KYCContract", transactionState.getContract());
//
//        Command command = signedTransaction.getTx().getCommands().get(0);
//        assert(command.getValue() instanceof KYCContract.SubmitKYC);
//
//        assertEquals(1, command.getSigners().size());
//        assertTrue(command.getSigners().contains(buyer.getInfo().getLegalIdentities().get(0).getOwningKey()));
//    }
//
//    /** APPROVE KYC FLOW TESTS **/
//
//    @Test
//    public void approveKYCTransactionHasOneInputAndOneOutput() throws Exception {
//        SubmitKYC submitKYCFlow = new SubmitKYC("KYC_1","rohitroy", "4843035", "BPZPR4763F", "SFSEE5323K",
//                345345345, "Google Inc.",
//                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0), 30000.d);
//
//        ApproveKYC approveKYCFlow = new ApproveKYC("KYC_1", 200000d, buyer.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> submitKYCFuture = buyer.startFlow(submitKYCFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> approveKYCFuture = lender.startFlow(approveKYCFlow);
//        setup();
//
//        SignedTransaction signedTransaction = approveKYCFuture.get();
//
//        assertEquals(1, signedTransaction.getTx().getInputs().size());
//        assertEquals(1, signedTransaction.getTx().getOutputs().size());
//
//    }
//
//    @Test
//    public void transactionHasOneApproveKYCCommandWithLenderAsSigner() throws Exception {
//        SubmitKYC submitKYCFlow = new SubmitKYC("KYC_1","rohitroy", "4843035", "BPZPR4763F", "SFSEE5323K",
//                345345345, "Google Inc.",
//                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0), 30000.d);
//
//        ApproveKYC approveKYCFlow = new ApproveKYC("KYC_1", 20000d, buyer.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> submitKYCFuture = buyer.startFlow(submitKYCFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> approveKYCFuture = lender.startFlow(approveKYCFlow);
//        setup();
//
//        SignedTransaction signedTransaction = approveKYCFuture.get();
//
//        assertEquals(1, signedTransaction.getTx().getCommands().size());
//        Command command = signedTransaction.getTx().getCommands().get(0);
//
//        assert (command.getValue() instanceof KYCContract.ApproveKYC);
//        assertTrue(command.getSigners().contains(lender.getInfo().getLegalIdentities().get(0).getOwningKey()));
//    }
//
//    /***************REJECT KYC TESTS *******************/
//    @Test
//    public void rejectKYCTransactionHasOneInputAndOneOutput() throws Exception {
//        SubmitKYC submitKYCFlow = new SubmitKYC("KYC_1","rohitroy", "4843035", "BPZPR4763F", "SFSEE5323K",
//                345345345, "Google Inc.",
//                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0), 30000.d);
//
//        RejectKYC rejectKYCFlow = new RejectKYC("KYC_1", buyer.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> submitKYCFuture = buyer.startFlow(submitKYCFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> approveKYCFuture = lender.startFlow(rejectKYCFlow);
//        setup();
//
//        SignedTransaction signedTransaction = approveKYCFuture.get();
//
//        assertEquals(1, signedTransaction.getTx().getInputs().size());
//        assertEquals(1, signedTransaction.getTx().getOutputs().size());
//
//    }

//    @Test
//    public void rejectTransactionHasOneApproveKYCCommandWithLenderAsSigner() throws Exception {
//        SubmitKYC submitKYCFlow = new SubmitKYC("KYC_1","rohitroy", "4843035", "BPZPR4763F", "SFSEE5323K",
//                345345345, "Google Inc.",
//                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0)), 30000.d;
//
//        RejectKYC rejectKYCFlow = new RejectKYC("KYC_1", buyer.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> submitKYCFuture = buyer.startFlow(submitKYCFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> approveKYCFuture = lender.startFlow(rejectKYCFlow);
//        setup();
//
//        SignedTransaction signedTransaction = approveKYCFuture.get();
//
//        assertEquals(1, signedTransaction.getTx().getCommands().size());
//        Command command = signedTransaction.getTx().getCommands().get(0);
//
//        assert (command.getValue() instanceof KYCContract.ApproveKYC);
//        assertTrue(command.getSigners().contains(lender.getInfo().getLegalIdentities().get(0).getOwningKey()));
//    }

    @Test
    public void updateCreditLimitWhileApproving() throws Exception {
        SignUp signUpFlow = new SignUp("USER_1", "Google Inc.", "India", "rohit.roy@birthvenue.in",
                "rohit.roy@birthvenue.in", "Rohit@123", "Buyer",
                buyer.getInfo().getLegalIdentities().get(0), seller.getInfo().getLegalIdentities().get(0), lender.getInfo().getLegalIdentities().get(0));

        SubmitKYC submitKYCFlow = new SubmitKYC("KYC_1","rohit.roy@birthvenue.in", "4843035", "BPZPR4763F", "SFSEE5323K",
                345345345, "Google Inc.",
                new Date(), "Gurgaon", 760, lender.getInfo().getLegalIdentities().get(0), 30000.d);

        ApproveKYC approveKYCFlow = new ApproveKYC("KYC_1", 200000d, buyer.getInfo().getLegalIdentities().get(0));

        UpdateCreditLimit updateFlow = new UpdateCreditLimit("rohit.roy@birthvenue.in", 200000d);

        Login loginFlow = new Login("rohit.roy@birthvenue.in", "Rohit@123");

        CordaFuture<SignedTransaction> signUpFuture = buyer.startFlow(signUpFlow);
        setup();

        CordaFuture<SignedTransaction> submitKYCFuture = buyer.startFlow(submitKYCFlow);
        setup();

//        CordaFuture<UserState> loginFuture = buyer.startFlow(loginFlow);
//        setup();
//
//        System.out.println("before submitting kyc  ");
//        System.out.println(loginFuture.get().getLastCreditLimit());

        CordaFuture<SignedTransaction> approveKYCFuture = lender.startFlow(approveKYCFlow);
        setup();

        CordaFuture<SignedTransaction> updateCreditLimitFuture = lender.startFlow(updateFlow);
        setup();

        CordaFuture<UserState> loginFuture = buyer.startFlow(loginFlow);
        setup();

        System.out.println("after login ");
        System.out.println(loginFuture.get().getLastCreditLimit());

    }
}
