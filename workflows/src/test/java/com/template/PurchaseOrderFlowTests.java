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

//    @Test
//    public void transactionHasNoInputHasMetalStateOutputAndCorrectOwner() throws Exception {
//        SubmitPurchaseOrder flow = new SubmitPurchaseOrder("ORDER_1", 1, 1,"buyername", "sellername",
//                "Scorpio S9", "2019", "Mahindra",
//                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
//                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> future = Buyer.startFlow(flow);
//        setup();
//        SignedTransaction signedTransaction = future.get();
//
//        assertEquals(0, signedTransaction.getTx().getInputs().size());
//        assertEquals(1, signedTransaction.getTx().getOutputs().size());
//        PurchaseOrderState output = signedTransaction.getTx().outputsOfType(PurchaseOrderState.class).get(0);
//
//        assertEquals(Buyer.getInfo().getLegalIdentities().get(0), output.getBuyer());
//    }
//
//    @Test
//    public void transactionHasCorrectContractWithOneIssueCommandAndIssuerAsSigner() throws Exception {
//        SubmitPurchaseOrder flow = new SubmitPurchaseOrder("ORDER_1", 1, 1,"buyername", "sellername",
//                "Scorpio S9", "2019", "Mahindra",
//                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
//                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> future = Buyer.startFlow(flow);
//        setup();
//        SignedTransaction signedTransaction = future.get();
//
//        TransactionState output = signedTransaction.getTx().getOutputs().get(0);
//        assertEquals("com.template.contracts.PurchaseOrderContract", output.getContract());
//
//        Command command = signedTransaction.getTx().getCommands().get(0);
//        assert (command.getValue() instanceof PurchaseOrderContract.SubmitPurchaseOrder);
//
//        System.out.println(command.getSigners().size());
//        assertEquals(1, command.getSigners().size());
//        assertTrue(command.getSigners().contains(Buyer.getInfo().getLegalIdentities().get(0).getOwningKey()));
//
//    }

    /****************** APPROVE METAL FLOW TESTS ********************/

//    @Test
//    public void transactionHasOneInputAndOneOutput() throws Exception {
//        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1", 1, 1,"buyername", "sellername",
//                "Scorpio S9", "2019", "Mahindra",
//                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
//                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//        ApprovePurchaseOrder approvePurchaseOrderFlow = new ApprovePurchaseOrder("ORDER_1",
//                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
//        setup();
//
//        SignedTransaction signedTransaction = approveFuture.get();
//
//        assertEquals(1, signedTransaction.getTx().getInputs().size());
//        assertEquals(1, signedTransaction.getTx().getOutputs().size());
//    }
//
//    @Test
//    public void transactionHasOneApproveCommandWithSellerAsSigner() throws Exception {
//        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1", 1, 1,"buyername", "sellername",
//                "Scorpio S9", "2019", "Mahindra",
//                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
//                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//        ApprovePurchaseOrder approvePurchaseOrderFlow = new ApprovePurchaseOrder("ORDER_1",
//                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
//        setup();
//
//        SignedTransaction signedTransaction = approveFuture.get();
//        assertEquals(1, signedTransaction.getTx().getCommands().size());
//
//        Command command = signedTransaction.getTx().getCommands().get(0);
//        assert (command.getValue() instanceof PurchaseOrderContract.ApprovePurchaseOrder);
//        assertTrue(command.getSigners().contains(Seller.getInfo().getLegalIdentities().get(0).getOwningKey()));
//    }

    /***************** REJECT PURCHASE ORDER *******************/

//    @Test
//    public void rejectTransactionHasOneInputAndOneOutput() throws Exception {
//        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1", 1, 1,"buyername", "sellername",
//                "Scorpio S9", "2019", "Mahindra",
//                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
//                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//        RejectPurchaseOrder approvePurchaseOrderFlow = new RejectPurchaseOrder("ORDER_1",
//                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
//        setup();
//
//        SignedTransaction signedTransaction = approveFuture.get();
//
//        assertEquals(1, signedTransaction.getTx().getInputs().size());
//        assertEquals(1, signedTransaction.getTx().getOutputs().size());
//    }
//
//    @Test
//    public void rejectTransactionHasOneApproveCommandWithSellerAsSigner() throws Exception {
//        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1", 1, 1,"buyername", "sellername",
//                "Scorpio S9", "2019", "Mahindra",
//                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
//                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//        RejectPurchaseOrder rejectPurchaseOrderFlow = new RejectPurchaseOrder("ORDER_1",
//                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));
//
//        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
//        setup();
//
//        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(rejectPurchaseOrderFlow);
//        setup();
//
//        SignedTransaction signedTransaction = approveFuture.get();
//        assertEquals(1, signedTransaction.getTx().getCommands().size());
//
//        Command command = signedTransaction.getTx().getCommands().get(0);
//        assert (command.getValue() instanceof PurchaseOrderContract.ApprovePurchaseOrder);
//        assertTrue(command.getSigners().contains(Seller.getInfo().getLegalIdentities().get(0).getOwningKey()));
//    }

    /***************** Raise Invoice Flow Tests ************************/

    @Test
    public void raiseInvoiceTransactionHasOneInputAndOneOutput() throws Exception {
        SubmitPurchaseOrder submitPurchaseOrderFlow = new SubmitPurchaseOrder("ORDER_1", 12, 1,"buyername", "sellername",
                "Scorpio S9", "2019", "Mahindra",
                "White", "Patrol", 200d, 1, 200d, "rohitroyrr8",
                Seller.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        ConfirmPurchaseOrder confirmPurchaseOrder = new ConfirmPurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Seller.getInfo().getLegalIdentities().get(0));

//        DenyPurchaseOrder confirmPurchaseOrder = new DenyPurchaseOrder("ORDER_1",
//                Buyer.getInfo().getLegalIdentities().get(0), Seller.getInfo().getLegalIdentities().get(0));

        ApprovePurchaseOrder approvePurchaseOrderFlow = new ApprovePurchaseOrder("ORDER_1",
                Buyer.getInfo().getLegalIdentities().get(0), Lender.getInfo().getLegalIdentities().get(0));

        SanctionPurchaseOrder sanctionPurchaseOrder = new SanctionPurchaseOrder("ORDER_1");
        StartShipmentPurchaseOrder startShipmentPurchaseOrder = new StartShipmentPurchaseOrder("ORDER_1", "https://google.com/bills");
        MarkReceivedPurchaseOrder receivedPurchaseOrderFlow = new MarkReceivedPurchaseOrder("ORDER_1", "https://google.com/grn");

        RaiseInvoiceOnPurchaseOrder raiseInvoiceOnPurchaseOrder = new RaiseInvoiceOnPurchaseOrder("ORDER_1");
        PayInvoiceOnPurchaseOrder payInvoiceOnPurchaseOrder = new PayInvoiceOnPurchaseOrder("ORDER_1");

        CordaFuture<SignedTransaction> submitFuture = Buyer.startFlow(submitPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction>  confirmFuture = Lender.startFlow(confirmPurchaseOrder);
        setup();

        CordaFuture<SignedTransaction> approveFuture = Seller.startFlow(approvePurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> sanctionFuture = Lender.startFlow(sanctionPurchaseOrder);
        setup();

        CordaFuture<SignedTransaction> shipmentFuture = Seller.startFlow(startShipmentPurchaseOrder);
        setup();

        CordaFuture<SignedTransaction> receivedFuture = Buyer.startFlow(receivedPurchaseOrderFlow);
        setup();

        CordaFuture<SignedTransaction> raiseFuture = Lender.startFlow(raiseInvoiceOnPurchaseOrder);
        setup();

        CordaFuture<SignedTransaction> payFuture = Buyer.startFlow(payInvoiceOnPurchaseOrder);
        setup();

        SignedTransaction signedTransaction = payFuture.get();

        assertEquals(1, signedTransaction.getTx().getInputs().size());
        assertEquals(1, signedTransaction.getTx().getOutputs().size());
    }
}
