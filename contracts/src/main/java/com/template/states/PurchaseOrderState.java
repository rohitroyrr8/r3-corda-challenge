package com.template.states;

import com.template.contracts.MetalContract;
import com.template.contracts.PurchaseOrderContract;
import com.template.models.Invoice;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@BelongsToContract(PurchaseOrderContract.class)
public class PurchaseOrderState implements ContractState {
    private String identifier;
    private int interestRate;
    private int period;
    private String buyerName;
    private String sellerName;
    private String name;
    private String model;
    private String companyName;
    private String color;
    private String fuelType;
    private Double rate;
    private int quantity;
    private Double amount;
    
    private String supplyBillsUrl;
    private String grnUrl;
    private String username;
    private Date createdOn;
    private String status;
    private Double amountPaid;
    private Double monthlyEMI;
    private Double totalPayment;
    
    private Invoice[] invoices;

    private Party buyer;
    private Party seller;
    private Party lender;

    public PurchaseOrderState(String identifier, int interestRate, int period, String buyerName, String sellerName,
                              String name, String model, String companyName, String color, String fuelType, Double rate,
                              int quantity, Double amount, String supplyBillsUrl, String grnUrl, String username,
                              Date createdOn, String status, Double amountPaid, Invoice[] invoices, Party buyer,
                              Party seller, Party lender, Double monthlyEMI, Double totalPayment) {
        this.identifier = identifier;
        this.interestRate = interestRate;
        this.period = period;
        this.buyerName = buyerName;
        this.sellerName = sellerName;
        this.name = name;
        this.model = model;
        this.companyName = companyName;
        this.color = color;
        this.fuelType = fuelType;
        this.rate = rate;
        this.quantity = quantity;
        this.amount = amount;
        this.supplyBillsUrl = supplyBillsUrl;
        this.grnUrl = grnUrl;
        this.username = username;
        this.createdOn = createdOn;
        this.status = status;
        this.amountPaid = amountPaid;
        this.invoices = invoices;
        this.seller = seller;
        this.buyer = buyer;
        this.lender = lender;
        this.monthlyEMI = monthlyEMI;
        this.totalPayment = totalPayment;
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getStatus() {
        return status;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public Party getSeller() {
        return seller;
    }

    public Party getBuyer() {
        return buyer;
    }

    public Party getLender() {
        return lender;
    }

    public int getInterestRate() {
        return interestRate;
    }

    public int getPeriod() {
        return period;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getSupplyBillsUrl() {
        return supplyBillsUrl;
    }

    public String getGrnUrl() {
        return grnUrl;
    }

    public Invoice[] getInvoices() {
        return invoices;
    }

    public void setBuyer( Party buyer) {
        this.buyer = buyer;
    }

    public void setSeller( Party seller) {
        this.seller = seller;
    }

    public void setLender( Party lender) {
        this.lender = lender;
    }

    public Double getMonthlyEMI() {
        return monthlyEMI;
    }

    public Double getTotalPayment() {
        return totalPayment;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(buyer, seller, lender);
    }
}