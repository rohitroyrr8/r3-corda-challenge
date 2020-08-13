package com.template.states;

import com.template.contracts.MetalContract;
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
    private Double rate;
    private int quantity;
    private Double amount;

    private String username;
    private Date createdOn;
    private String status;
    private Double amountPaid;

    private Party seller;
    private Party buyer;
    private Party lender;

    public PurchaseOrderState(String identifier, String name, String model, String companyName, String color, String fuelType, Double rate, int quantity, Double amount, String username, Date createdOn, String status, Double amountPaid, Party buyer, Party seller, Party lender) {
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
        this.createdOn = createdOn;
        this.status = status;
        this.amountPaid = amountPaid;
        this.buyer = buyer;
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

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(buyer, seller, lender);
    }
}