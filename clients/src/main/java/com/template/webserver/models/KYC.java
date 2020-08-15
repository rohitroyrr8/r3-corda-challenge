package com.template.webserver.models;

import java.util.Date;

public class KYC {
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

    private String party;

    private int creditLimit;
    private String status;
    private Date createdOn;

    public KYC(String identifier, String username, String aadharNumber, String panNumber, String companyPanNumber, int incorporationNumber, String companyName, Date incorporationDate, String incorporationPlace, int cibilScore, int creditLimit, String status, Date createdOn, String party) {
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
        this.party = party;
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

    public String getParty() {
        return party;
    }
}
