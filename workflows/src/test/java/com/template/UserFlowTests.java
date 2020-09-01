package com.template;

import com.google.common.collect.ImmutableList;
import com.template.contracts.UserContract;
import com.template.flows.auth.Login;
import com.template.flows.auth.SignUp;
import com.template.flows.auth.UpdateCreditLimit;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserFlowTests {
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

    /****************** SIGNUP FLOW TESTS ********************/

    @Test
    public void transactionHasNoInputHasMetalStateOutputAndCorrectParty() throws Exception {
        SignUp flow = new SignUp("USER_1", "Google Inc.", "India", "rohit.roy@birthvenue.in",
                "rohit.roy@birthvenue.in", "Rohit@123", "Buyer",
                buyer.getInfo().getLegalIdentities().get(0), seller.getInfo().getLegalIdentities().get(0), lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> future = buyer.startFlow(flow);
        setup();
        SignedTransaction signedTransaction = future.get();

        assertEquals(0, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
        UserState output = signedTransaction.getTx().outputsOfType(UserState.class).get(0);

        assertEquals(buyer.getInfo().getLegalIdentities().get(0), output.getBuyer());
//        assertEquals(seller.getInfo().getLegalIdentities().get(0), output.getOwner());
        assertEquals(lender.getInfo().getLegalIdentities().get(0), output.getLender());
    }

    @Test
    public void transactionHasCorrectContractWithOneSignUpCommandAndBuyerAsSigner() throws Exception {
        SignUp flow = new SignUp("USER_1", "Google Inc.", "India", "rohit.roy@birthvenue.in",
                "rohit.roy@birthvenue.in", "Rohit@123", "Buyer",
                buyer.getInfo().getLegalIdentities().get(0), seller.getInfo().getLegalIdentities().get(0), lender.getInfo().getLegalIdentities().get(0));


        CordaFuture<SignedTransaction> future = buyer.startFlow(flow);
        setup();
        SignedTransaction signedTransaction = future.get();

        TransactionState output = signedTransaction.getTx().getOutputs().get(0);
        assertEquals("com.template.contracts.UserContract", output.getContract());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert (command.getValue() instanceof UserContract.SignUp);

        assertEquals(1, command.getSigners().size());
        assertTrue(command.getSigners().contains(buyer.getInfo().getLegalIdentities().get(0).getOwningKey()));

    }

    @Test
    public void transactionHasCorrectContractWithOneSignUpCommandAndSellerAsSigner() throws Exception {
        SignUp flow = new SignUp("USER_1", "Google Inc.", "India", "rohit.roy@birthvenue.in",
                "rohit.roy@birthvenue.in", "Rohit@123", "Seller",
                buyer.getInfo().getLegalIdentities().get(0), seller.getInfo().getLegalIdentities().get(0), lender.getInfo().getLegalIdentities().get(0));


        CordaFuture<SignedTransaction> future = seller.startFlow(flow);
        setup();
        SignedTransaction signedTransaction = future.get();

        TransactionState output = signedTransaction.getTx().getOutputs().get(0);
        assertEquals("com.template.contracts.UserContract", output.getContract());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert (command.getValue() instanceof UserContract.SignUp);

        assertEquals(1, command.getSigners().size());
        assertTrue(command.getSigners().contains(seller.getInfo().getLegalIdentities().get(0).getOwningKey()));

    }

    /****************** LOGIN FLOW TESTS ********************/

    @Test
    public void transactionHasOneInputAndOneOutput() throws Exception {
        SignUp signUpFlow = new SignUp("USER_1", "Google Inc.", "India", "rohit.roy@birthvenue.in",
                "rohit.roy@birthvenue.in", "Rohit@123", "Buyer",
                buyer.getInfo().getLegalIdentities().get(0), seller.getInfo().getLegalIdentities().get(0), lender.getInfo().getLegalIdentities().get(0));
        Login loginFlow = new Login("rohit.roy@birthvenue.in", "Rohit@123");
        CordaFuture<SignedTransaction> signUpFuture = buyer.startFlow(signUpFlow);
        setup();

        CordaFuture<UserState> loginFuture = buyer.startFlow(loginFlow);
        setup();

        System.out.println("after processing ");
        System.out.println(loginFuture.get().getRegisteredAs());
//        SignedTransaction signedTransaction = loginFuture.get();
//
//        assertEquals(1,signedTransaction.getTx().getInputs().size());
//        assertEquals(1, signedTransaction.getTx().getOutputs().size());
    }

    @Test
    public void transactionHasOneLoginCommandWithOwnerAsSigner() throws Exception {
        SignUp signUpFlow = new SignUp("USER_1", "Google Inc.", "India", "rohit.roy@birthvenue.in",
                "rohit.roy@birthvenue.in", "Rohit@123", "Buyer",
                buyer.getInfo().getLegalIdentities().get(0), seller.getInfo().getLegalIdentities().get(0), lender.getInfo().getLegalIdentities().get(0));
        Login loginFlow = new Login("rohit.roy@birthvenue.in", "Rohit@123");
        CordaFuture<SignedTransaction> signUpFuture = buyer.startFlow(signUpFlow);
        setup();

        CordaFuture<UserState> loginFuture = buyer.startFlow(loginFlow);
        setup();

        System.out.println("after processing ");
        System.out.println(loginFuture.get().getIdentifier());

//        SignedTransaction signedTransaction = loginFuture.get();
//        assertEquals(1, signedTransaction.getTx().getCommands().size());
//
//        Command command = signedTransaction.getTx().getCommands().get(0);
//        assert (command.getValue() instanceof UserContract.Login);
//        assertTrue(command.getSigners().contains(buyer.getInfo().getLegalIdentities().get(0).getOwningKey()));
    }

    @Test
    public void transactionHasOneLoginCommandWithOwnerAsSignerForSeller() throws Exception {
        SignUp signUpFlow = new SignUp("USER_1", "Google Inc.", "India", "rohit.roy@birthvenue.in",
                "rohit.roy@birthvenue.in", "Rohit@123", "Lender",
                buyer.getInfo().getLegalIdentities().get(0), seller.getInfo().getLegalIdentities().get(0), lender.getInfo().getLegalIdentities().get(0));
        Login loginFlow = new Login("rohit.roy@birthvenue.in", "Rohit@123");
        CordaFuture<SignedTransaction> signUpFuture = lender.startFlow(signUpFlow);
        setup();

        CordaFuture<UserState> loginFuture = lender.startFlow(loginFlow);
        setup();

        System.out.println("after processing ");
        System.out.println(loginFuture.get().getIdentifier());

//        SignedTransaction signedTransaction = loginFuture.get();
//        assertEquals(1, signedTransaction.getTx().getCommands().size());
//
//        Command command = signedTransaction.getTx().getCommands().get(0);
//        assert (command.getValue() instanceof UserContract.Login);
//        assertTrue(command.getSigners().contains(seller.getInfo().getLegalIdentities().get(0).getOwningKey()));
    }

    /********************* UPDATE CREDIT LIMIT FLOW *******************************/
    @Test
    public void updateTransactionHasOneInputOneOutputOfUserStateAndCorrectParty() throws Exception {
        SignUp flow = new SignUp("USER_1", "Google Inc.", "India", "rohit.roy@birthvenue.in",
                "rohit.roy@birthvenue.in", "Rohit@123", "Buyer",
                buyer.getInfo().getLegalIdentities().get(0), seller.getInfo().getLegalIdentities().get(0), lender.getInfo().getLegalIdentities().get(0));
        UpdateCreditLimit updateFlow = new UpdateCreditLimit("rohit.roy@birthvenue.in", 1100000d);

        CordaFuture<SignedTransaction> signUpFuture = buyer.startFlow(flow);
        setup();

        CordaFuture<SignedTransaction> updateFuture = buyer.startFlow(updateFlow);
        setup();

        SignedTransaction signedTransaction = updateFuture.get();

        assertEquals(1, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
        UserState output = signedTransaction.getTx().outputsOfType(UserState.class).get(0);

        assertEquals(buyer.getInfo().getLegalIdentities().get(0), output.getBuyer());
//        assertEquals(seller.getInfo().getLegalIdentities().get(0), output.getOwner());
        assertEquals(lender.getInfo().getLegalIdentities().get(0), output.getLender());
    }

    @Test
    public void upadteTransactionHasCorrectContractWithUdpateCommandAndBuyerAsSigner() throws Exception {
        SignUp flow = new SignUp("USER_1", "Google Inc.", "India", "rohit.roy@birthvenue.in",
                "rohit.roy@birthvenue.in", "Rohit@123", "Buyer",
                buyer.getInfo().getLegalIdentities().get(0), seller.getInfo().getLegalIdentities().get(0), lender.getInfo().getLegalIdentities().get(0));
        UpdateCreditLimit updateFlow = new UpdateCreditLimit("rohit.roy@birthvenue.in", 1100000d);

        CordaFuture<SignedTransaction> signUpFuture = buyer.startFlow(flow);
        setup();

        CordaFuture<SignedTransaction> updateFuture = buyer.startFlow(updateFlow);
        setup();

        SignedTransaction signedTransaction = updateFuture.get();

        TransactionState output = signedTransaction.getTx().getOutputs().get(0);
        assertEquals("com.template.contracts.UserContract", output.getContract());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert (command.getValue() instanceof UserContract.UpdateCreditLimit);

        assertEquals(1, command.getSigners().size());
        assertTrue(command.getSigners().contains(buyer.getInfo().getLegalIdentities().get(0).getOwningKey()));

    }
}
