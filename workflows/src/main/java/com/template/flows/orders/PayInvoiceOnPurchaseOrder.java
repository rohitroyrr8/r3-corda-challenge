package com.template.flows.orders;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.PurchaseOrderContract;
import com.template.enums.InvoiceStatus;
import com.template.enums.PurchaseOrderStatus;
import com.template.models.Invoice;
import com.template.states.PurchaseOrderState;
import com.template.utils.CommonUtils;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class PayInvoiceOnPurchaseOrder extends FlowLogic<SignedTransaction> {
    private int index = 0;
    private String identifier;

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

    public PayInvoiceOnPurchaseOrder(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
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

        StateAndRef<PurchaseOrderState> inputStateStateAndRef = null;
        inputStateStateAndRef = this.checkForOrderState();

        Party seller = inputStateStateAndRef.getState().getData().getSeller();
        Party lender = inputStateStateAndRef.getState().getData().getLender();

        Invoice[] invoices = inputStateStateAndRef.getState().getData().getInvoices();
        if(invoices == null || invoices.length < 1) { throw new FlowException("No Invoice found."); }

        Invoice invoice = invoices[invoices.length-1];
        invoice.setStatus(InvoiceStatus.Paid.toString());
        invoice.setPaidOn(new Date());
        invoices[invoices.length-1] = invoice;

        Double paidAmount = inputStateStateAndRef.getState().getData().getAmountPaid() + invoice.getAmount();

        String orderStatus = inputStateStateAndRef.getState().getData().getStatus();
        if(paidAmount >= inputStateStateAndRef.getState().getData().getTotalPayment()) {
            orderStatus = PurchaseOrderStatus.Closed.toString();
        }

        PurchaseOrderState outputState = new PurchaseOrderState(
                identifier,
                inputStateStateAndRef.getState().getData().getInterestRate(),
                inputStateStateAndRef.getState().getData().getPeriod(),
                inputStateStateAndRef.getState().getData().getBuyerName(),
                inputStateStateAndRef.getState().getData().getSellerName(),
                inputStateStateAndRef.getState().getData().getName(),
                inputStateStateAndRef.getState().getData().getModel(),
                inputStateStateAndRef.getState().getData().getCompanyName(),
                inputStateStateAndRef.getState().getData().getColor(),
                inputStateStateAndRef.getState().getData().getFuelType(),
                inputStateStateAndRef.getState().getData().getRate(),
                inputStateStateAndRef.getState().getData().getQuantity(),
                inputStateStateAndRef.getState().getData().getAmount(),
                inputStateStateAndRef.getState().getData().getGrnUrl(),
                inputStateStateAndRef.getState().getData().getSupplyBillsUrl(),
                inputStateStateAndRef.getState().getData().getUsername(),
                inputStateStateAndRef.getState().getData().getCreatedOn(),
                orderStatus, paidAmount, invoices,
                getOurIdentity(), seller, lender,
                inputStateStateAndRef.getState().getData().getMonthlyEMI(),
                inputStateStateAndRef.getState().getData().getTotalPayment());

        Command command = new Command(new PurchaseOrderContract.PayInvoice(), getOurIdentity().getOwningKey());

        // generating transaction
        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState)
                .addCommand(command);
        transactionBuilder.addInputState(inputStateStateAndRef);

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

    private StateAndRef<PurchaseOrderState> checkForOrderState() throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        List<StateAndRef<PurchaseOrderState>> purchaseOrderStateAndRefsList = getServiceHub().getVaultService().queryBy(PurchaseOrderState.class, queryCriteria).getStates();

        boolean isFound = false;
        for(int i=0; i < purchaseOrderStateAndRefsList.size(); i++) {
            if(purchaseOrderStateAndRefsList.get(i).getState().getData().getIdentifier().equals(identifier)) {
                isFound = true;
                index = i;
                break;
            }
        }

        if(!isFound) {throw new FlowException("No un-consumed state found."); }
        return purchaseOrderStateAndRefsList.get(index);
    }
}
