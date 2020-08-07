package com.template.contracts;

import com.template.states.MetalState;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.contracts.DummyState;
import net.corda.testing.core.DummyCommandData;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import static net.corda.testing.node.NodeTestUtils.transaction;
public class ContractTests {

    private final TestIdentity Mint = new TestIdentity(new CordaX500Name("mint", "", "GB"));
    private final TestIdentity TraderA = new TestIdentity(new CordaX500Name("traderA", "", "GB"));
    private final TestIdentity TraderB = new TestIdentity(new CordaX500Name("traderB", "", "GB"));

    private final MockServices ledgerServices = new MockServices();

    private MetalState metalState = new MetalState("gold", 10, Mint.getParty(), TraderA.getParty());
    private MetalState metalStateInput = new MetalState("gold", 10, Mint.getParty(), TraderA.getParty());
    private MetalState metalStateOutput = new MetalState("gold", 10, Mint.getParty(), TraderB.getParty());


    @Test
    public void metalContractImplementsContract() {
        assert (new MetalContract() instanceof Contract);
    }

    /*************************** ISSUE COMMAND TESTS ****************************/

    @Test
    public void metalContractRequiresZeroInputInIssueTransaction() {
        transaction(ledgerServices, tx -> {
            // Has an input, will fail
            tx.input(MetalContract.CID, metalState);
            tx.command(Mint.getPublicKey(), new MetalContract.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Does not have input, will verify
            tx.output(MetalContract.CID, metalState);
            tx.command(Mint.getPublicKey(), new MetalContract.Issue());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void metalContractRequiresOneOutputInIssueTransaction() {
        transaction(ledgerServices, tx -> {
            // more than one output, will fail
            tx.output(MetalContract.CID, metalState);
            tx.output(MetalContract.CID, metalState);
            tx.command(Mint.getPublicKey(), new MetalContract.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Does have ont output only, will verify
            tx.output(MetalContract.CID, metalState);
            tx.command(Mint.getPublicKey(), new MetalContract.Issue());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void metalContractRequiresOutputToBeMetalState() {
        transaction(ledgerServices, tx -> {
            // having dummy state, will fail
            tx.output(MetalContract.CID, new DummyState());
            tx.output(MetalContract.CID, metalState);
            tx.command(Mint.getPublicKey(), new MetalContract.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // having metal state as output, will verify
            tx.output(MetalContract.CID, metalState);
            tx.command(Mint.getPublicKey(), new MetalContract.Issue());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void metalContractRequiresTheTransactionToBeIssueCommand() {

        transaction(ledgerServices, tx -> {
            // Has Issue Command, will verify
            tx.output(MetalContract.CID, metalState);
            tx.command(Mint.getPublicKey(), new MetalContract.Issue());
            tx.verifies();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // having dummy command, will fail
            tx.output(MetalContract.CID, new DummyState());
            tx.output(MetalContract.CID, metalState);
            tx.command(Mint.getPublicKey(), DummyCommandData.INSTANCE);
            tx.fails();
            return null;
        });

    }

    @Test
    public void metalContractRequiresIssuersToBeRequiredSigners() {
        transaction(ledgerServices, tx -> {
            // issuer is not signing the command, will fail
            tx.output(MetalContract.CID, new DummyState());
            tx.output(MetalContract.CID, metalState);
            tx.command(TraderA.getPublicKey(), new MetalContract.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // issuer is signing the command, will verify
            tx.output(MetalContract.CID, metalState);
            tx.command(Mint.getPublicKey(), new MetalContract.Issue());
            tx.verifies();
            return null;
        });
    }

    /*************************** TRANSFER COMMAND TESTS ****************************/

    @Test
    public void metalContractRequiresOneInputAndOneOutputStateInTransferTransaction() {
        transaction(ledgerServices, tx -> {
            // contain one input and one output, will verify
            tx.input(MetalContract.CID, metalStateInput);
            tx.output(MetalContract.CID, metalStateOutput);
            tx.command(TraderA.getPublicKey(), new MetalContract.Transfer());
            tx.verifies();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // contain only output, will fail
            tx.output(MetalContract.CID, metalStateOutput);
            tx.command(TraderA.getPublicKey(), new MetalContract.Transfer());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // contain only input, will fail
            tx.input(MetalContract.CID, metalStateInput);
            tx.command(TraderA.getPublicKey(), new MetalContract.Transfer());
            tx.fails();
            return null;
        });
    }

    @Test
    public void metalContractRequiresTransactionToBeTransferCommand() {
        transaction(ledgerServices, tx -> {
            // not having transfer command, will fail
            tx.input(MetalContract.CID, metalStateInput);
            tx.output(MetalContract.CID, metalStateOutput);
            tx.command(TraderA.getPublicKey(), DummyCommandData.INSTANCE);
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // having transfer command, will verify
            tx.input(MetalContract.CID, metalStateInput);
            tx.output(MetalContract.CID, metalStateOutput);
            tx.command(TraderA.getPublicKey(), new MetalContract.Transfer());
            tx.verifies();
            return null;
        });


    }

    @Test
    public void metalContractRequiresOwnerToBeRequiredSigner() {
        transaction(ledgerServices, tx -> {
            // not having transfer command, will verify
            tx.input(MetalContract.CID, metalStateInput);
            tx.output(MetalContract.CID, metalStateOutput);
            tx.command(Mint.getPublicKey(), new MetalContract.Transfer());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // having transfer command, will verify
            tx.input(MetalContract.CID, metalStateInput);
            tx.output(MetalContract.CID, metalStateOutput);
            tx.command(TraderA.getPublicKey(), new MetalContract.Transfer());
            tx.verifies();
            return null;
        });
    }

}