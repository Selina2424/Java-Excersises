package com.sg.flooringmastery.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class Order {
    private int orderNumber;
    private String customerName;
    private String state;
    private Date orderDate;
    private BigDecimal taxRate;
    private String productType;
    private BigDecimal costPerSquareFoot;
    private BigDecimal labourCostPerSquareFoot;
    private BigDecimal materialCost;
    private BigDecimal area;
    private BigDecimal labourCost;
    private BigDecimal tax;
    private BigDecimal total;

    public Order() {

    }

    //creates new order using original orders values
    public Order(Order original) {
        this.orderNumber = original.orderNumber;
        this.customerName = original.customerName;
        this.state = original.state;
        if (original.orderDate != null) {
            this.orderDate = new Date(original.orderDate.getTime());
        }
        this.taxRate = original.taxRate;
        this.productType = original.productType;
        this.costPerSquareFoot = original.costPerSquareFoot;
        this.labourCostPerSquareFoot = original.labourCostPerSquareFoot;
        this.materialCost = original.materialCost;
        this.area = original.area;
        this.labourCost = original.labourCost;
        this.tax = original.tax;
        this.total = original.total;
    }

    // Return the unique order number.
    public int getOrderNumber() {
        return orderNumber;
    }

    // Store the unique order number in this object.
    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    // Return the customer name.
    public String getCustomerName() {
        return customerName;
    }

    // Store the customer name in this object.
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Return the state name or order state abbreviation.
    public String getState() {
        return state;
    }

    // Store the state name or order state abbreviation in this object.
    public void setState(String state) {
        this.state = state;
    }

    // Return the scheduled date of the flooring order.
    public Date getOrderDate() {
        if (orderDate == null) {
            return null;
        }
        return new Date(orderDate.getTime());
    }

    // Store the scheduled date of the flooring order in this object.
    public void setOrderDate(Date orderDate) {
        if (orderDate == null) {
            this.orderDate = null;
        } else {
            this.orderDate = new Date(orderDate.getTime());
        }
    }

    //return the tax percentage, such as 4.45
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    //store the tax percentage, such as 4.45 in this object
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    //return the selected flooring product name.
    public String getProductType() {
        return productType;
    }

    //store the selected flooring product name in this object
    public void setProductType(String productType) {
        this.productType = productType;
    }

    //return the material price for one square foot
    public BigDecimal getCostPerSquareFoot() {
        return costPerSquareFoot;
    }

    //store the material price for one square foot in this object
    public void setCostPerSquareFoot(BigDecimal costPerSquareFoot) {
        this.costPerSquareFoot = costPerSquareFoot;
    }

    //return the labour price for one square foot
    public BigDecimal getLabourCostPerSquareFoot() {
        return labourCostPerSquareFoot;
    }

    //store the labour price for one square foot in this object
    public void setLabourCostPerSquareFoot(BigDecimal labourCostPerSquareFoot) {
        this.labourCostPerSquareFoot = labourCostPerSquareFoot;
    }

    //return the calculated material cost
    public BigDecimal getMaterialCost() {
        return materialCost;
    }

    //store the calculated material cost in this object.
    public void setMaterialCost(BigDecimal materialCost) {
        this.materialCost = materialCost;
    }

    //return the flooring area in square feet
    public BigDecimal getArea() {
        return area;
    }

    //store the flooring area in square feet in this object
    public void setArea(BigDecimal area) {
        this.area = area;
    }

    //return the calculated labour cost
    public BigDecimal getLabourCost() {
        return labourCost;
    }

    //store the calculated labour cost in this object
    public void setLabourCost(BigDecimal labourCost) {
        this.labourCost = labourCost;
    }

    //return the calculated tax amount
    public BigDecimal getTax() {
        return tax;
    }

    //store the calculated tax amount in this object
    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    //return the final cost including materials, labour and tax
    public BigDecimal getTotal() {
        return total;
    }

    //store the final cost including materials, labour and tax in this object
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    //compares dto values
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
        Order other = (Order) object;

        //checks if every value in both order objects are equal
        //if all true then return true
        return orderNumber == other.orderNumber
                && Objects.equals(customerName, other.customerName)
                && Objects.equals(state, other.state)
                && Objects.equals(orderDate, other.orderDate)
                && Objects.equals(taxRate, other.taxRate)
                && Objects.equals(productType, other.productType)
                && Objects.equals(costPerSquareFoot, other.costPerSquareFoot)
                && Objects.equals(labourCostPerSquareFoot, other.labourCostPerSquareFoot)
                && Objects.equals(materialCost, other.materialCost)
                && Objects.equals(area, other.area)
                && Objects.equals(labourCost, other.labourCost)
                && Objects.equals(tax, other.tax)
                && Objects.equals(total, other.total);
    }

    //create a hash value from the same fields used by equals
    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, customerName, state, orderDate, taxRate, productType, costPerSquareFoot, labourCostPerSquareFoot, materialCost, area, labourCost, tax, total);
    }

    //return a short text description that is useful when inspecting the object
    @Override
    public String toString() {
        return "Order{" + customerName + "}";
    }
}