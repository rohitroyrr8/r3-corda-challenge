package com.template.states;

import com.template.contracts.KYCContract;
import com.template.contracts.UserContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@BelongsToContract(UserContract.class)
public class UserState implements ContractState {
    private String identifier;

    private String organisationName;
    private String country;
    private String email;
    private String username;
    private String password; // need to be hashed
    private String registeredAs;

    private String status;
    private Date createdOn;
    private Date lastLoginOn;
    private Double lastCreditLimit;

    private Party buyer;
    private Party seller;
    private Party lender;

    public UserState(String identifier, String organisationName, String country,
                     String email, String username, String password, String registeredAs, String status,
                     Date createdOn, Date lastLoginOn, Party buyer, Party seller, Party lender, Double lastCreditLimit) {
        this.identifier = identifier;
        this.organisationName = organisationName;
        this.country = country;
        this.email = email;
        this.username = username;
        this.password = password;
        this.registeredAs = registeredAs;
        this.status = status;
        this.createdOn = createdOn;
        this.lastLoginOn = lastLoginOn;
        this.buyer = buyer;
        this.seller = seller;
        this.lender = lender;
        this.lastCreditLimit = lastCreditLimit;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getRegisteredAs() {
        return registeredAs;
    }

    public Date getLastLoginOn() {
        return lastLoginOn;
    }

    public Party getBuyer() {
        return buyer;
    }

    public Party getSeller() {
        return seller;
    }

    public Party getLender() {
        return lender;
    }

    public Double getLastCreditLimit() {
        return lastCreditLimit;
    }

    public void setBuyer(Party buyer) {
        this.buyer = buyer;
    }

    public void setSeller(Party seller) {
        this.seller = seller;
    }

    public void setLender(Party lender) {
        this.lender = lender;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(buyer, seller, lender);
    }
}
