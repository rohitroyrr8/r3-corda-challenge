package com.template.flows.orders;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.PurchaseOrderContract;
import com.template.enums.PurchaseOrderStatus;
import com.template.states.PurchaseOrderState;
import net.corda.core.contracts.Command;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Date;

@InitiatingFlow
@StartableByRPC
public class SubmitPurchaseOrder extends FlowLogic<SignedTransaction> {
    private String identifier;
    private String name;
    private String model;
    private String companyName;
    private String color;
    private String fuelType;
    private Double rate;
    private int quantity;
    private Double amount;
    private String username;

    private Party seller;
    private Party lender;

    private final ProgressTracker.Step RETRIEVING_NOTARY = new ProgressTracker.Step("Retrieving the notary.");
    private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction.");
    private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing transaction with out private keys.");
    private final ProgressTracker.Step COUNTER_PARTY_SESSION = new ProgressTracker.Step("Sending flow to the counter-party");
    private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and recording transaction.");

    private final ProgressTracker progressTracker = new ProgressTracker(
            RETRIEVING_NOTARY,
            GENERATING_TRANSACTION,
            SIGNING_TRANSACTION,
            COUNTER_PARTY_SESSION,
            FINALISING_TRANSACTION
    );

    public SubmitPurchaseOrder(String identifier, String name, String model, String companyName, String color, String fuelType, Double rate, int quantity, Double amount, String username, Party seller, Party lender) {
        this.identifier = identifier;
        this.name = name;
        this.model = model;
        this.companyName = companyName;
        this.color = color;
        this.fuelType = fuelType;
        this.rate = rate;
        this.quantity = quantity;
        this.amount = amount;
        this.username = username;
        this.seller = seller;
        this.lender = lender;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getColor() {
        return color;
    }

    public String getFuelType() {
        return fuelType;
    }

    public Double getRate() {
        return rate;
    }

    public int getQuantity() {
        return quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public String getUsername() {
        return username;
    }

    public Party getSeller() {
        return seller;
    }

    public Party getLender() {
        return lender;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {

        // Retrieving notary identity
        progressTracker.setCurrentStep(RETRIEVING_NOTARY);
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        PurchaseOrderState outputState = new PurchaseOrderState(identifier, name, model, companyName, color, fuelType, rate,
                quantity, amount, username, new Date(), PurchaseOrderStatus.Submitted.toString(), 0d, getOurIdentity(), seller, lender);

        Command command = new Command(new PurchaseOrderContract.SubmitPurchaseOrder(), getOurIdentity().getOwningKey());

        // generating transaction
        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState)
                .addCommand(command);

        // signing transaction
        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        // counter party session
        progressTracker.setCurrentStep(COUNTER_PARTY_SESSION);
        FlowSession sellerPartySession = initiateFlow(seller);
        FlowSession lenderPartySession = initiateFlow(lender);

        // finalising transaction
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new FinalityFlow(signedTransaction, sellerPartySession, lenderPartySession));
    }
}
