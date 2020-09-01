package com.template.webserver.models;

import com.template.models.Invoice;

import java.util.Date;

public class PurchaseOrder {
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

    private Invoice[] invoices;

    public PurchaseOrder(String identifier, int interestRate, int period, String buyerName, String sellerName, String name, String model, String companyName, String color, String fuelType, Double rate, int quantity, Double amount, String supplyBillsUrl, String grnUrl, String username, Date createdOn, String status, Double amountPaid, Invoice[] invoices) {
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
    }

    public String getIdentifier() {
        return identifier;
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

    public String getSupplyBillsUrl() {
        return supplyBillsUrl;
    }

    public String getGrnUrl() {
        return grnUrl;
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

    public Invoice[] getInvoices() {
        return invoices;
    }
}
