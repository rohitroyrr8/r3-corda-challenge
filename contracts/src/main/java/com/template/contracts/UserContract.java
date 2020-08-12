package com.template.contracts;

import com.template.enums.KYCStatus;
import com.template.enums.UserStaus;
import com.template.states.KYCState;
import com.template.states.UserState;
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
 *  USER Contract
 *
 */
public class UserContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String CID = "com.template.contracts.UserContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getCommands().size() != 1) {throw new IllegalArgumentException("Transaction must have one command only."); }

        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandData = command.getValue();

        if(commandData instanceof SignUp) {

            // Shape rule
            if(tx.getInputStates().size() != 0 ){ throw new IllegalArgumentException("Sign-up cannot have input state.");}
            if(tx.getOutputStates().size() != 1) {throw new IllegalArgumentException("Sign-up cannot have more than one output state.");}

            // Content Rule
            ContractState outputState = tx.getOutput(0);
            if(!(outputState instanceof UserState)) { throw new IllegalArgumentException("Output must be typeof UserState."); }

            UserState userState = (UserState) outputState;
            if(userState.getOrganisationName() == null) { throw new IllegalArgumentException("Organisation Name is required."); }
            if(userState.getCountry() == null) { throw new IllegalArgumentException("Country is required."); }
            if(userState.getEmail() == null ) { throw new IllegalArgumentException("Email-id is required."); }
            if(userState.getUsername() == null ){ throw new IllegalArgumentException("Username is required."); }
            if(userState.getPassword() == null) { throw new IllegalArgumentException("Email-id is required."); }
            if(userState.getRegisteredAs() == null) { throw new IllegalArgumentException("Registered As is required."); }

            // Signer Rule

            Party currentParty = userState.getOwner();
            PublicKey publicKey = currentParty.getOwningKey();

        if(!requiredSigners.contains(publicKey)) {
            throw new IllegalArgumentException("Submitter Key is required."); }

        }

        else if(commandData instanceof Login) {
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Login must have one input state."); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Login must have one output state."); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            if(!(outputState instanceof UserState)) { throw new IllegalArgumentException("Output must be typeof UserState."); }

            UserState userState = (UserState) inputState;
            if(userState.getStatus() == UserStaus.Disabled.toString()) { throw new IllegalArgumentException("This user is disabled."); }

            Party currentParty = userState.getOwner();
            PublicKey publicKey = currentParty.getOwningKey();

            if(!requiredSigners.contains(publicKey)) { throw new IllegalArgumentException("Authorised Party must sign the transaction."); }
        }

        else { throw new IllegalArgumentException(("Invalid Command.")); }
    }

    public static class SignUp implements CommandData {};
    public static class Login implements CommandData {};

}