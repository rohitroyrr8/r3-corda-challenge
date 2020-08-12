package com.template.states;

import com.template.contracts.PurchaseOrderContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@BelongsToContract(PurchaseOrderContract.class)
public class PurchaseOrderState implements ContractState {
    private String identifier;
    private String name;
    private String model;
    private String companyName;
    private String color;
    private String fuelType;
    private int rate;
    private int quantity;
    private int amount;
    private boolean isReceived;
    private Date createdOn;
    private String status;

    private int period;
    private int interestRate;
    private int paidAmountByBuyer;

    private Party issuer;
    private Party owner;

    public PurchaseOrderState(String identifier, String name, String model, String companyName, String color, String fuelType, int rate, int quantity, int amount, boolean isReceived, Date createdOn, String status, int period, int interestRate, int paidAmountByBuyer, Party issuer, Party owner) {
        this.identifier = identifier;
        this.name = name;
        this.model = model;
        this.companyName = companyName;
        this.color = color;
        this.fuelType = fuelType;
        this.rate = rate;
        this.quantity = quantity;
        this.amount = amount;
        this.isReceived = isReceived;
        this.createdOn = createdOn;
        this.status = status;
        this.period = period;
        this.interestRate = interestRate;
        this.paidAmountByBuyer = paidAmountByBuyer;
        this.issuer = issuer;
        this.owner = owner;
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

    public int getRate() {
        return rate;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAmount() {
        return amount;
    }

    public Party getIssuer() {
        return issuer;
    }

    public Party getOwner() {
        return owner;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getStatus() {
        return status;
    }

    public int getPeriod() {
        return period;
    }

    public int getInterestRate() {
        return interestRate;
    }

    public int getPaidAmountByBuyer() {
        return paidAmountByBuyer;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(issuer, owner);
    }
}
