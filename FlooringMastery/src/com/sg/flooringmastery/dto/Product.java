package com.sg.flooringmastery.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private String productType;
    private BigDecimal costPerSquareFoot;
    private BigDecimal labourCostPerSquareFoot;

    public Product() {

    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String prooductType) {
        this.productType = prooductType;
    }

    public BigDecimal getCostPerSquareFoot() {
        return costPerSquareFoot;
    }

    public void setCostPerSquareFoot(BigDecimal costPerSquareFoot) {
        this.costPerSquareFoot = costPerSquareFoot;
    }

    public BigDecimal getLabourCostPerSquareFoot() {
        return labourCostPerSquareFoot;
    }

    public void setLabourCostPerSquareFoot(BigDecimal labourCostPerSquareFoot) {
        this.labourCostPerSquareFoot = labourCostPerSquareFoot;
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
        Product other = (Product) object;
        //checks if every value in both order objects are equal
        //if all true then return true
        return Objects.equals(productType, other.productType)
                && Objects.equals(costPerSquareFoot, other.costPerSquareFoot)
                && Objects.equals(labourCostPerSquareFoot, other.labourCostPerSquareFoot);
    }

    //create a hash value from the same fields used by equals.
    @Override
    public int hashCode() {
        return Objects.hash(productType, costPerSquareFoot, labourCostPerSquareFoot);
    }

    //return a short text description
    @Override
    public String toString() {
        return "Product{" + productType + "}";
    }
}

