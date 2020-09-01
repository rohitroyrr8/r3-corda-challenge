package com.template.contracts;

import com.template.enums.PurchaseOrderStatus;
import com.template.states.PurchaseOrderState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

/**
 *  Purchase Order Contract
 *
 *  1. check if requested amount is below credit limit or not
 *  2. update status on approve / reject / mark as received (update isReceived as true)
 *
 */
public class InvoiceContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String CID = "com.template.contracts.PurchaseOrderContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getCommands().size() != 1) {throw new IllegalArgumentException("Transaction must have one command only."); }

        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandData = command.getValue();


        if(commandData instanceof SubmitPurchaseOrder) {
            // ISSUE TRANSACTION LOGIC

            // 1. Shape Rule
            if(tx.getInputStates().size() != 0) { throw new IllegalArgumentException("Purchase Order cannot have input states."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order cannot have more than one states."); }

            // 2. Content Rule
            ContractState outputState = tx.getOutput(0);
            if(!(outputState instanceof PurchaseOrderState)) {throw new IllegalArgumentException("Output must be purchase order state."); }

            PurchaseOrderState state = (PurchaseOrderState) outputState;

            // 3. Signer Rule
            Party buyer = state.getBuyer();
            PublicKey issuerKey = buyer.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Authorised Party must sign the transaction."); }
        }

        else if(commandData instanceof ConfirmPurchaseOrder) {
            // ISSUE TRANSACTION LOGIC

            // 1. Shape Rule
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one input state only."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one output state only"); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            PurchaseOrderState state = (PurchaseOrderState) inputState;
            if(state.getStatus().equals(PurchaseOrderStatus.Denied.toString())) { throw new IllegalArgumentException("Selected order is already denied"); }
            if(!state.getStatus().equals(PurchaseOrderStatus.Submitted.toString())) { throw new IllegalArgumentException("This action cannot be performed at this stage."); }

            if(!(outputState instanceof PurchaseOrderState)) {throw new IllegalArgumentException("Output must be metal state."); }

            // 3. Signer Rule
            Party seller = state.getSeller();
            PublicKey issuerKey = seller.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Authorised Party must sign the transaction."); }
        }

        else if(commandData instanceof DenyPurchaseOrder) {

            // 1. Shape Rule
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one input state only."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one output state only"); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            PurchaseOrderState state = (PurchaseOrderState) inputState;
            if(state.getStatus().equals(PurchaseOrderStatus.Confirmed.toString())) { throw new IllegalArgumentException("Selected order is already confirmed"); }
            if(!state.getStatus().equals(PurchaseOrderStatus.Submitted.toString())) { throw new IllegalArgumentException("This action cannot be performed at this stage."); }

            if(!(outputState instanceof PurchaseOrderState)) {throw new IllegalArgumentException("Output must be purchase order state."); }

            // 3. Signer Rule
            Party seller = state.getSeller();
            PublicKey issuerKey = seller.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Authorised Party must sign the transaction."); }
        }

        else if(commandData instanceof ApprovePurchaseOrder) {
            // ISSUE TRANSACTION LOGIC

            // 1. Shape Rule
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one input state only."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one output state only"); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            PurchaseOrderState state = (PurchaseOrderState) inputState;
            if(state.getStatus().equals(PurchaseOrderStatus.Approved.toString())) { throw new IllegalArgumentException("Selected order is already approved"); }
            if(!state.getStatus().equals(PurchaseOrderStatus.Confirmed.toString())) { throw new IllegalArgumentException("This action cannot be performed at this stage."); }

            if(!(outputState instanceof PurchaseOrderState)) {throw new IllegalArgumentException("Output must be metal state."); }

            // 3. Signer Rule
            Party seller = state.getSeller();
            PublicKey issuerKey = seller.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Authorised Party must sign the transaction."); }
        }

        else if(commandData instanceof RejectPurchaseOrder) {

            // 1. Shape Rule
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one input state only."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one output state only"); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            PurchaseOrderState state = (PurchaseOrderState) inputState;
            if(state.getStatus().equals(PurchaseOrderStatus.Rejected.toString())) { throw new IllegalArgumentException("Selected order is already rejected"); }
            if(!state.getStatus().equals(PurchaseOrderStatus.Confirmed.toString())) { throw new IllegalArgumentException("This action cannot be performed at this stage."); }

            if(!(outputState instanceof PurchaseOrderState)) {throw new IllegalArgumentException("Output must be purchase order state."); }

            // 3. Signer Rule
            Party seller = state.getSeller();
            PublicKey issuerKey = seller.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Authorised Party must sign the transaction."); }
        }

        else if(commandData instanceof StartShipmentPurchaseOrder) {

            // 1. Shape Rule
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one input state only."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one output state only"); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            PurchaseOrderState state = (PurchaseOrderState) inputState;
            if(state.getStatus().equals(PurchaseOrderStatus.ShipmentStarted.toString())) { throw new IllegalArgumentException("Selected order is already in shipment stage."); }
            if(!state.getStatus().equals(PurchaseOrderStatus.Approved.toString())) { throw new IllegalArgumentException("This action cannot be performed at this stage."); }

            if(!(outputState instanceof PurchaseOrderState)) {throw new IllegalArgumentException("Output must be purchase order state."); }

            // 3. Signer Rule
            Party seller = state.getSeller();
            PublicKey issuerKey = seller.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Authorised Party must sign the transaction."); }
        }

        else if(commandData instanceof MarkAsReceivedPurchaseOrder) {
            // ISSUE TRANSACTION LOGIC

            // 1. Shape Rule
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one input state only."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one output state only"); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            PurchaseOrderState state = (PurchaseOrderState) inputState;
            if(!state.getStatus().equals(PurchaseOrderStatus.Approved.toString())) { throw new IllegalArgumentException("Selected order is not approved yet."); }
            if(state.getStatus().equals(PurchaseOrderStatus.Received.toString())) { throw new IllegalArgumentException("Selected order is already marked as received"); }
            if(!state.getStatus().equals(PurchaseOrderStatus.ShipmentStarted.toString())) { throw new IllegalArgumentException("This action cannot be performed at this stage."); }

            if(!(outputState instanceof PurchaseOrderState)) {throw new IllegalArgumentException("Output must be purchase order state."); }

            // 3. Signer Rule
            Party buyer = state.getBuyer();
            PublicKey issuerKey = buyer.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Authorised Party must sign the transaction."); }
        }

        else if(commandData instanceof PayEMIForPurchasedOrder) {
            // ISSUE TRANSACTION LOGIC

            // 1. Shape Rule
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one input state only."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Purchase Order must have one output state only"); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            PurchaseOrderState state = (PurchaseOrderState) inputState;
            if(!state.getStatus().equals(PurchaseOrderStatus.Received.toString())) { throw new IllegalArgumentException("This action cannot be performed at this stage."); }

            if(!(outputState instanceof PurchaseOrderState)) {throw new IllegalArgumentException("Output must be purchase order state."); }

            // 3. Signer Rule
            Party buyer = state.getBuyer();
            PublicKey issuerKey = buyer.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Authorised Party must sign the transaction."); }
        }
        else {
            System.out.println(commandData.getClass().getSimpleName());
            throw new IllegalArgumentException(("Invalid Command..." + commandData.getClass().getSimpleName()));
        }
    }

    public static class SubmitPurchaseOrder implements CommandData {};
    public static class ConfirmPurchaseOrder implements CommandData {};
    public static class DenyPurchaseOrder implements CommandData {};
    public static class ApprovePurchaseOrder implements CommandData {};
    public static class RejectPurchaseOrder implements CommandData {};
    public static class StartShipmentPurchaseOrder implements CommandData {};
    public static class MarkAsReceivedPurchaseOrder implements CommandData {};
    public static class PayEMIForPurchasedOrder implements CommandData {};
}