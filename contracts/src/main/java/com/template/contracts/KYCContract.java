package com.template.contracts;

import com.template.enums.KYCStatus;
import com.template.states.KYCState;
import com.template.states.MetalState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import javax.xml.transform.sax.SAXSource;
import java.security.PublicKey;
import java.util.List;

/**
 *  KYC Contract
 *
 *  1. On submit
 *      check for mandatory fields
 *      throw error if CIBIL score is less than 750
 *      update status
 *
 */
public class KYCContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String CID = "com.template.contracts.KYCContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getCommands().size() != 1) {throw new IllegalArgumentException("Transaction must have one command only."); }

        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandData = command.getValue();

        if(commandData instanceof SubmitKYC) {
            /**  SUBMIT Transaction Logic  **/

            // Shape rule
            if(tx.getInputStates().size() != 0 ){ throw new IllegalArgumentException("Submit KYC cannot have input state.");}
            if(tx.getOutputStates().size() != 1) {throw new IllegalArgumentException("Submit KYC cannot have more than one output state.");}

            // Content Rule
            ContractState outputState = tx.getOutput(0);
            if(!(outputState instanceof KYCState)) { throw new IllegalArgumentException("Output must be KYCState."); }

            KYCState kycState = (KYCState) outputState;
            if(kycState.getAadharNumber() <= 0) { throw new IllegalArgumentException("Aadhar Number is required."); }
            if(kycState.getPanNumber() == null) { throw new IllegalArgumentException("PAN Number is required."); }
            if(kycState.getCompanyPanNumber() == null) { throw new IllegalArgumentException("Company PAN Number is required."); }
            if(kycState.getIncorporationNumber() <= 0) { throw new IllegalArgumentException("Incorporation Number is required."); }
            if(kycState.getIncorporationDate() == null) { throw new IllegalArgumentException("Incorporation Date is required."); }
            if(kycState.getIncorporationPlace() == null) { throw new IllegalArgumentException("Incorporation Place is required."); }
            if(kycState.getCibilScore() <= 0) { throw new IllegalArgumentException("CIBIL Score is required."); }

            if (kycState.getCibilScore() < 750) {
                throw new IllegalArgumentException("KYC is rejected as CIBIL Score is less than 750.");
            }
            // Signer Rule
            Party submittedBy = kycState.getOwner();
            PublicKey submitterKey = submittedBy.getOwningKey();

            if(!requiredSigners.contains(submitterKey)) { throw new IllegalArgumentException("Submitter Key is required."); }
        }

        else if(commandData instanceof ApproveKYC) {
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Approve KYC must have one input state."+tx.getInputStates().size()); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Approve KYC must have one output state."); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            if(!(outputState instanceof KYCState)) { throw new IllegalArgumentException("Output must be KYCState."); }

            KYCState kycState = (KYCState) inputState;
            if(kycState.getStatus() == KYCStatus.Approved.toString()) { throw new IllegalArgumentException("KYC is already approved."); }

            // Signer Rule
            Party signingParty = kycState.getLender();
            PublicKey signingKey = signingParty.getOwningKey();

            if(!requiredSigners.contains(signingKey)) { throw new IllegalArgumentException("Authorised Party must sign the transaction."); }

        }

        else if(commandData instanceof RejectKYC) {
            if(tx.getInputStates().size() != 1) { throw new IllegalArgumentException("Reject KYC must have one input state." + tx.getInputStates().size()); }
            if(tx.getOutputStates().size() != 1) { throw new IllegalArgumentException("Reject KYC must have one output state."); }

            // 2. Content Rule
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            if(!(outputState instanceof KYCState)) { throw new IllegalArgumentException("Output must be KYCState."); }

            KYCState kycState = (KYCState) inputState;
            if(kycState.getStatus() == KYCStatus.Rejected.toString()) { throw new IllegalArgumentException("KYC is already rejected."); }

            // Signer Rule
            Party signingParty = kycState.getLender();
            PublicKey signingKey = signingParty.getOwningKey();

            if(!requiredSigners.contains(signingKey)) { throw new IllegalArgumentException("Authorised Party must sign the transaction."); }

        }

        else {
            throw new IllegalArgumentException("Invalid Command...");
        }
    }

    public static class SubmitKYC implements CommandData {};
    public static class ApproveKYC implements CommandData {};
    public static class RejectKYC implements CommandData {};
}