package com.template.webserver.models;

import java.util.Date;

public class PurchaseOrder {
    private String identifier;
    private String name;
    private String model;
    private String companyName;
    private String color;
    private String fuelType;
    private Double rate;
    private int quantity;
    private Double amount;
    private String sellerName;
    private String buyerName; // buyer name and seller name need to be added in purchaseOrder state and remove username

    private String username;
    private Date createdOn;
    private String status;
    private Double amountPaid;

    public PurchaseOrder(String identifier, String name, String model, String companyName, String color, String fuelType,
                         Double rate, int quantity, Double amount, String username, Date createdOn, String status, Double amountPaid, String sellerName, String buyerName) {
        this.identifier = identifier;
        this.name = name;
        this.model = model;
        this.companyName = companyName;
        this.color = color;
        this.fuelType = fuelType;
        this.rate = rate;
        this.quantity = quantity;
        this.amount = amount;
        this.createdOn = createdOn;
        this.status = status;
        this.amountPaid = amountPaid;
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.username = username;
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getStatus() {
        return status;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getUsername() {
        return username;
    }
}
