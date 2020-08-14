package com.template;

import com.google.common.collect.ImmutableList;
import com.template.contracts.PurchaseOrderContract;
import com.template.flows.orders.*;
import com.template.states.PurchaseOrderState;
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

public class PurchaseOrderFlowTests {
    private final MockNetwork network = new MockNetwork(new MockNetworkParameters(ImmutableList.of(
        TestCordapp.findCordapp("com.template.contracts"),
        TestCordapp.findCordapp("com.template.flows")
    )));

    private final StartedMockNode Buyer = network.createNode();
    private final StartedMockNode Seller = network.createNode();
    private final StartedMockNode Lender = network.createNode();

    @Before
    public void setup() {
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    /****************** SUBMIT PURCHASE ORDER FLOW TESTS ********************/

    @Test
    public void transactionHasNoInputHasMetalStateOutputAndCorrectOwner() throws Exception {
        SubmitPurchaseOrder flow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> future = Buyer.startFlow(flow);
        setup();
        SignedTransaction signedTransaction = future.get();

        assertEquals(0, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
        PurchaseOrderState output = signedTransaction.getTx().outputsOfType(PurchaseOrderState.class).get(0);

        assertEquals(Buyer.getInfo().getLegalIdentities().get(0), output.getBuyer());
    }

    @Test
    public void transactionHasCorrectContractWithOneIssueCommandAndIssuerAsSigner() throws Exception {
        SubmitPurchaseOrder flow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> future = Buyer.startFlow(flow);
        setup();
        SignedTransaction signedTransaction = future.get();

        TransactionState output = signedTransaction.getTx().getOutputs().get(0);
        assertEquals("com.template.contracts.PurchaseOrderContract", output.getContract());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert (command.getValue() instanceof PurchaseOrderContract.SubmitPurchaseOrder);

        System.out.println(command.getSigners().size());
        assertEquals(1, command.getSigners().size());
        assertTrue(command.getSigners().contains(Buyer.getInfo().getLegalIdentities().get(0).getOwningKey()));

    }

    /****************** APPROVE METAL FLOW TESTS ********************/

    @Test
    public void transactionHasOneInputAndOneOutput() throws Exception {
        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        ApprovePurchaseOrder approvePurchaseOrderFlow = new ApprovePurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
        setup();

        SignedTransaction signedTransaction = approveFuture.get();

        assertEquals(1, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
    }

    @Test
    public void transactionHasOneApproveCommandWithSellerAsSigner() throws Exception {
        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        ApprovePurchaseOrder approvePurchaseOrderFlow = new ApprovePurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
        setup();

        SignedTransaction signedTransaction = approveFuture.get();
        assertEquals(1, signedTransaction.getTx().getCommands().size());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert (command.getValue() instanceof PurchaseOrderContract.ApprovePurchaseOrder);
        assertTrue(command.getSigners().contains(Seller.getInfo().getLegalIdentities().get(0).getOwningKey()));
    }

    /***************** REJECT PURCHASE ORDER *******************/

    @Test
    public void rejectTransactionHasOneInputAndOneOutput() throws Exception {
        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        RejectPurchaseOrder approvePurchaseOrderFlow = new RejectPurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
        setup();

        SignedTransaction signedTransaction = approveFuture.get();

        assertEquals(1, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
    }

    @Test
    public void rejectTransactionHasOneApproveCommandWithSellerAsSigner() throws Exception {
        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        RejectPurchaseOrder rejectPurchaseOrderFlow = new RejectPurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(rejectPurchaseOrderFlow);
        setup();

        SignedTransaction signedTransaction = approveFuture.get();
        assertEquals(1, signedTransaction.getTx().getCommands().size());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert (command.getValue() instanceof PurchaseOrderContract.ApprovePurchaseOrder);
        assertTrue(command.getSigners().contains(Seller.getInfo().getLegalIdentities().get(0).getOwningKey()));
    }

    /*****************RECEIVED PURCHASE ORDER TESTS **********************/

    @Test
    public void receivedTransactionHasOneInputAndOneOutput() throws Exception {
        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        ApprovePurchaseOrder approvePurchaseOrderFlow = new ApprovePurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        MarkReceivedPurchaseOrder receivedPurchaseOrderFlow = new MarkReceivedPurchaseOrder("ORDER_1",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> receivedFuture = Buyer.startFlow(receivedPurchaseOrderFlow);
        setup();

        SignedTransaction signedTransaction = receivedFuture.get();

        assertEquals(1, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
    }


    @Test
    public void receivedTransactionHasOneReceiveCommandWithBuyerAsSigner() throws Exception {
        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        ApprovePurchaseOrder approvePurchaseOrderFlow = new ApprovePurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        MarkReceivedPurchaseOrder receivedPurchaseOrderFlow = new MarkReceivedPurchaseOrder("ORDER_1",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> receivedFuture = Buyer.startFlow(receivedPurchaseOrderFlow);
        setup();

        SignedTransaction signedTransaction = receivedFuture.get();
        assertEquals(1, signedTransaction.getTx().getCommands().size());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert (command.getValue() instanceof PurchaseOrderContract.MarkAsReceivedPurchaseOrder);
        assertTrue(command.getSigners().contains(Buyer.getInfo().getLegalIdentities().get(0).getOwningKey()));
    }

    /************* PAY EMI PURCHASE ORDER TEST ****************/
    @Test
    public void patEMITransactionHasOneInputAndOneOutput() throws Exception {
        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        ApprovePurchaseOrder approvePurchaseOrderFlow = new ApprovePurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        MarkReceivedPurchaseOrder receivedPurchaseOrderFlow = new MarkReceivedPurchaseOrder("ORDER_1",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        PayEMIForPurchaseOrder payEMIForPurchaseOrderFlow = new PayEMIForPurchaseOrder("ORDER_1", 50d,
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> receivedFuture = Buyer.startFlow(receivedPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> payEMIFuture = Buyer.startFlow(payEMIForPurchaseOrderFlow);
        setup();

        SignedTransaction signedTransaction = receivedFuture.get();

        assertEquals(1, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
    }


    @Test
    public void receivedTransactionHasOnePayEMICommandWithBuyerAsSigner() throws Exception {
        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1","Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        ApprovePurchaseOrder approvePurchaseOrderFlow = new ApprovePurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        MarkReceivedPurchaseOrder receivedPurchaseOrderFlow = new MarkReceivedPurchaseOrder("ORDER_1",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
        PayEMIForPurchaseOrder payEMIForPurchaseOrderFlow = new PayEMIForPurchaseOrder("ORDER_1", 50d,
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> receivedFuture = Buyer.startFlow(receivedPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> payEMIFuture = Buyer.startFlow(payEMIForPurchaseOrderFlow);
        setup();

        SignedTransaction signedTransaction = receivedFuture.get();
        assertEquals(1, signedTransaction.getTx().getCommands().size());

        Command command = signedTransaction.getTx().getCommands().get(0);
        assert (command.getValue() instanceof PurchaseOrderContract.MarkAsReceivedPurchaseOrder);
        assertTrue(command.getSigners().contains(Buyer.getInfo().getLegalIdentities().get(0).getOwningKey()));
    }
}
