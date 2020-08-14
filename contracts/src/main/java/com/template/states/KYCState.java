package com.template.states;

import com.template.contracts.KYCContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@BelongsToContract(KYCContract.class)
public class KYCState implements ContractState {
    private String identifier;
    private String username;
    private String aadharNumber;
    private String panNumber;
    private String companyPanNumber;
    private int incorporationNumber;
    private String companyName;
    private Date incorporationDate;
    private String incorporationPlace;
    private int cibilScore;

    private int creditLimit;
    private String status;
    private Date createdOn;

    private Party owner;
    private Party lender;

    public KYCState(String identifier, String username, String aadharNumber, String panNumber, String companyPanNumber, int incorporationNumber, String companyName, Date incorporationDate, String incorporationPlace, int cibilScore, int creditLimit, String status, Date createdOn, Party owner, Party lender) {
        this.identifier = identifier;
        this.username = username;
        this.aadharNumber = aadharNumber;
        this.panNumber = panNumber;
        this.companyPanNumber = companyPanNumber;
        this.incorporationNumber = incorporationNumber;
        this.companyName = companyName;
        this.incorporationDate = incorporationDate;
        this.incorporationPlace = incorporationPlace;
        this.cibilScore = cibilScore;
        this.creditLimit = creditLimit;
        this.status = status;
        this.createdOn = createdOn;
        this.owner = owner;
        this.lender = lender;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUsername() {
        return username;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public String getCompanyPanNumber() {
        return companyPanNumber;
    }

    public int getIncorporationNumber() {
        return incorporationNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Date getIncorporationDate() {
        return incorporationDate;
    }

    public String getIncorporationPlace() {
        return incorporationPlace;
    }

    public int getCibilScore() {
        return cibilScore;
    }

    public int getCreditLimit() {
        return creditLimit;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Party getOwner() {
        return owner;
    }

    public Party getLender() {
        return lender;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(owner, lender);
    }
}
