package com.template.webserver.models;

import java.util.Date;

public class KYC {
    private String identifier;
    private String username;
    private double creditLimit;

    public KYC(String identifier, String username, double creditLimit) {
        this.identifier = identifier;
        this.username = username;
        this.creditLimit = creditLimit;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUsername() {
        return username;
    }

    public double getCreditLimit() {
        return creditLimit;
    }
}
