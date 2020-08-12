package com.template.webserver.models;

public class Item {
    private String name;
    private String model;
    private String companyName;
    private String color;
    private String fuelType;
    private Double rate;

    public Item(String name, String model, String companyName, String color, String fuelType, Double rate) {
        this.name = name;
        this.model = model;
        this.companyName = companyName;
        this.color = color;
        this.fuelType = fuelType;
        this.rate = rate;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", companyName='" + companyName + '\'' +
                ", color='" + color + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", rate=" + rate +
                '}';
    }
}
