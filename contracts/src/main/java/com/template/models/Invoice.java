package com.template.models;

import net.corda.core.serialization.CordaSerializable;

import java.util.Date;

@CordaSerializable
public class Invoice {
    private String identifier;
    private Double amount;
    private Date requestedOn;
    private Date paidOn;
    private String status;

    public Invoice(String identifier, Double amount, Date requestedOn, Date paidOn, String status) {
        this.identifier = identifier;
        this.amount = amount;
        this.requestedOn = requestedOn;
        this.paidOn = paidOn;
        this.status = status;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Double getAmount() {
        return amount;
    }

    public Date getRequestedOn() {
        return requestedOn;
    }

    public Date getPaidOn() {
        return paidOn;
    }

    public String getStatus() {
        return status;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setRequestedOn(Date requestedOn) {
        this.requestedOn = requestedOn;
    }

    public void setPaidOn(Date paidOn) {
        this.paidOn = paidOn;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
