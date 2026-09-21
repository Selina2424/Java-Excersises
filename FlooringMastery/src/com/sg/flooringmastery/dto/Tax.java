package com.sg.flooringmastery.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class Tax {
    private String state;
    private String stateAbr;
    private BigDecimal taxRate;

    public Tax(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateAbr() {
        return stateAbr;
    }

    public void setStateAbr(String stateAbr) {
        this.stateAbr = stateAbr;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    //compares DTO values
    //returns true if object are the same
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        //if object is null or not an order object
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        //converts object to an order so the fields are compared
        Tax other = (Tax) object;
        //checks if every value in both order objects are equal
        //if all true then return true
        return Objects.equals(state, other.state)
                && Objects.equals(stateAbr, other.stateAbr)
                && Objects.equals(taxRate, other.taxRate);
    }

    //create a hash value from the same fields used by equals.
    @Override
    public int hashCode() {
        return Objects.hash(state, stateAbr, taxRate);
    }

    //return a short text description
    @Override
    public String toString() {
        return "Tax{" + state + "}";
    }
}

