package com.template.webserver.models;

import java.util.Date;

public class User {
    private String identifier;

    private String organisationName;
    private String country;
    private String email;
    private String username;
    private String password;
    private String registeredAs;

    private String status;
    private Date createdOn;
    private Date lastLoginOn;

    public User(String identifier, String organisationName, String country, String email, String username, String password, String registeredAs, String status, Date createdOn, Date lastLoginOn) {
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

    public String getRegisteredAs() {
        return registeredAs;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getLastLoginOn() {
        return lastLoginOn;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
