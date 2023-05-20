package com.example.packageassignment.model;

import java.util.Objects;

public class DeliveryLocation {
    private String country;
    private String state;

    public DeliveryLocation(String country, String state) {
        this.country = country;
        this.state = state;
    }

    // Getters and setters

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        DeliveryLocation other = (DeliveryLocation) obj;
        return Objects.equals(country, other.country) && Objects.equals(state, other.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, state);
    }

}
