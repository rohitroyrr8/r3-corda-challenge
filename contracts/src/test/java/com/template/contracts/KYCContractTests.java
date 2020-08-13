package com.template.contracts;

import com.template.states.KYCState;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.contracts.DummyState;
import net.corda.testing.core.DummyCommandData;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.util.Date;

import static net.corda.testing.node.NodeTestUtils.transaction;

public class KYCContractTests {

    private final TestIdentity Buyer = new TestIdentity(new CordaX500Name("Buyer", "London", "GB"));
    private final TestIdentity Seller = new TestIdentity(new CordaX500Name("Seller", "London", "GB"));
    private final TestIdentity Lender = new TestIdentity(new CordaX500Name("Lender", "London", "GB"));

    private final MockServices ledgerServices = new MockServices();

    KYCState kycstate = new KYCState("123241", "Google Inc.", "Buyer",
            48124341, "VDGED43SJ", "FSGS3445J",
            5342432, "Google Inc.", new Date(), "Gurgaon",
            799, 4333, "Submitted", new Date(), Buyer.getParty(), Lender.getParty());


    KYCState kycStateInput = new KYCState("123241", "Google Inc.", "Buyer",
            48124341, "VDGED43SJ", "FSGS3445J",
            5342432, "Google Inc.", new Date(), "Gurgaon",
            799, 4333, "Submitted", new Date(), Buyer.getParty(), Lender.getParty());

    KYCState kycStateOutput = new KYCState("123241", "Google Inc.", "Buyer",
            48124341, "VDGED43SJ", "FSGS3445J",
            5342432, "Google Inc.", new Date(), "Gurgaon",
            799, 4333, "Approved", new Date(), Buyer.getParty(), Lender.getParty());


    @Test
    public void kycContractImplementsContract() {
        assert (new KYCContract() instanceof Contract);
    }

    /*************************** ISSUE COMMAND TESTS ****************************/

    @Test
    public void metalContractRequiresZeroInputInIssueTransaction() {
        transaction(ledgerServices, tx -> {
            // Has an input, will fail
            tx.input(KYCContract.CID, kycstate);
            tx.command(Buyer.getPublicKey(), new KYCContract.SubmitKYC());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Does not have input, will verify
            tx.output(KYCContract.CID, kycstate);
            tx.command(Buyer.getPublicKey(), new KYCContract.SubmitKYC());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void kycContractRequiresOneOutputInIssueTransaction() {
        transaction(ledgerServices, tx -> {
            // more than one output, will fail
            tx.output(KYCContract.CID, kycstate);
            tx.output(KYCContract.CID, kycstate);
            tx.command(Buyer.getPublicKey(), new KYCContract.SubmitKYC());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Does have ont output only, will verify
            tx.output(KYCContract.CID, kycstate);
            tx.command(Buyer.getPublicKey(), new KYCContract.SubmitKYC());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void kycContractRequiresOutputToBeMetalState() {
        transaction(ledgerServices, tx -> {
            // having dummy state, will fail
            tx.output(KYCContract.CID, new DummyState());
            tx.output(KYCContract.CID, kycstate);
            tx.command(Buyer.getPublicKey(), new KYCContract.SubmitKYC());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // having metal state as output, will verify
            tx.output(KYCContract.CID, kycstate);
            tx.command(Buyer.getPublicKey(), new KYCContract.SubmitKYC());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void metalContractRequiresTheTransactionToBeIssueCommand() {

        transaction(ledgerServices, tx -> {
            // Has Issue Command, will verify
            tx.output(KYCContract.CID, kycstate);
            tx.command(Buyer.getPublicKey(), new KYCContract.SubmitKYC());
            tx.verifies();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // having dummy command, will fail
            tx.output(KYCContract.CID, new DummyState());
            tx.output(KYCContract.CID, kycstate);
            tx.command(Buyer.getPublicKey(), DummyCommandData.INSTANCE);
            tx.fails();
            return null;
        });

    }

    @Test
    public void metalContractRequiresIssuersToBeRequiredSigners() {
        transaction(ledgerServices, tx -> {
            // issuer is not signing the command, will fail
            tx.output(KYCContract.CID, new DummyState());
            tx.output(KYCContract.CID, kycstate);
            tx.command(Seller.getPublicKey(), new KYCContract.SubmitKYC());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // issuer is signing the command, will verify
            tx.output(KYCContract.CID, kycstate);
            tx.command(Buyer.getPublicKey(), new KYCContract.SubmitKYC());
            tx.verifies();
            return null;
        });
    }

    /*************************** APPROVE COMMAND TESTS ****************************/

    @Test
    public void metalContractRequiresOneInputAndOneOutputStateInTransferTransaction() {
        transaction(ledgerServices, tx -> {
            // contain one input and one output, will verify
            tx.input(KYCContract.CID, kycStateInput);
            tx.output(KYCContract.CID, kycStateOutput);
            tx.command(Lender.getPublicKey(), new KYCContract.RejectKYC());
            tx.verifies();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // contain only output, will fail
            tx.output(KYCContract.CID, kycStateOutput);
            tx.command(Lender.getPublicKey(), new KYCContract.ApproveKYC());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // contain only input, will fail
            tx.input(KYCContract.CID, kycStateInput);
            tx.command(Lender.getPublicKey(), new KYCContract.ApproveKYC());
            tx.fails();
            return null;
        });
    }

    @Test
    public void metalContractRequiresTransactionToBeTransferCommand() {
//        transaction(ledgerServices, tx -> {
//            // not having transfer command, will fail
//            tx.input(KYCContract.CID, kycStateInput);
//            tx.output(KYCContract.CID, kycStateOutput);
//            tx.command(Lender.getPublicKey(), DummyCommandData.INSTANCE);
//            tx.fails();
//            return null;
//        });

        transaction(ledgerServices, tx -> {
            // having transfer command, will verify
            tx.input(KYCContract.CID, kycStateInput);
            tx.output(KYCContract.CID, kycStateOutput);
            tx.command(Lender.getPublicKey(), new KYCContract.RejectKYC());
            tx.verifies();
            return null;
        });


    }

    @Test
    public void kycContractRequiresLenderToBeRequiredSigner() {
        transaction(ledgerServices, tx -> {
            // not having transfer command, will fail
            tx.input(KYCContract.CID, kycStateInput);
            tx.output(KYCContract.CID, kycStateOutput);
            tx.command(Buyer.getPublicKey(), new KYCContract.ApproveKYC());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // having transfer command, will verify
            tx.input(KYCContract.CID, kycStateInput);
            tx.output(KYCContract.CID, kycStateOutput);
            tx.command(Lender.getPublicKey(), new KYCContract.ApproveKYC());
            tx.verifies();
            return null;
        });
    }

}