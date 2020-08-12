package com.template.contracts;

import com.template.states.MetalState;
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
public class PurchaseOrderContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String CID = "com.template.contracts.MetalContract";

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
            if(tx.getInputStates().size() != 0) { throw new IllegalArgumentException("Issue cannot have input states."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Issue cannot have more than one states."); }

            // 2. Content Rule
            ContractState outputState = tx.getOutput(0);
            if(!(outputState instanceof MetalState)) {throw new IllegalArgumentException("Output must be metal state."); }

            MetalState metalState = (MetalState) outputState;
            if(!metalState.getName().equals("gold") && !metalState.getName().equals("silver")) { throw new IllegalArgumentException("Metal can only be Gold or Silver."); }

            // 3. Signer Rule
            Party issuer = metalState.getIssuer();
            PublicKey issuerKey = issuer.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Issuer must sign the transaction."); }
        }

        else if(commandData instanceof ApprovePurchaseOrder) {
            // ISSUE TRANSACTION LOGIC

            // 1. Shape Rule
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Transfer transaction must have one input state only."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Transfer transaction must have one output state only"); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            if(!(outputState instanceof MetalState)) {throw new IllegalArgumentException("Output must be metal state."); }

            MetalState metalState = (MetalState) inputState;
            if(!metalState.getName().equals("gold") && !metalState.equals("silver")) { throw new IllegalArgumentException("Metal can only be Gold or Silver."); }

            // 3. Signer Rule
            Party owner = metalState.getOwner();
            PublicKey issuerKey = owner.getOwningKey();

            if(!requiredSigners.contains(issuerKey)) {throw new IllegalArgumentException("Owner must sign the transaction."); }
        }
        else {
            throw new IllegalArgumentException(("Invalid Command..."));
        }
    }

    public static class SubmitPurchaseOrder implements CommandData {};
    public static class ApprovePurchaseOrder implements CommandData {};
    public static class RejectPurchaseOrder implements CommandData {};
    public static class MarkAsReceivedPurchaseOrder implements CommandData {};
    public static class PayEMIToPurchaseOrder implements CommandData {};
}