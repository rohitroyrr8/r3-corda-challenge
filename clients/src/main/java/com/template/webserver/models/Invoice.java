package com.template.webserver.models;

import java.util.Date;

public class Invoice {
    private String identifier;
    private String username;
    private String orderIdentifier;
    private Double amount;
    private Date raisedOn;
    private Date payBefore;
    private Date paidOn;
    private String status;

    public Invoice(String identifier, String username, String orderIdentifier, Double amount, Date raisedOn, Date payBefore, Date paidOn, String status) {
        this.identifier = identifier;
        this.username = username;
        this.orderIdentifier = orderIdentifier;
        this.amount = amount;
        this.raisedOn = raisedOn;
        this.payBefore = payBefore;
        this.paidOn = paidOn;
        this.status = status;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUsername() {
        return username;
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }

    public Double getAmount() {
        return amount;
    }

    public Date getRaisedOn() {
        return raisedOn;
    }

    public Date getPayBefore() {
        return payBefore;
    }

    public Date getPaidOn() {
        return paidOn;
    }

    public String getStatus() {
        return status;
    }
}
