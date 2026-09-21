package com.sg.flooringmastery.ui;

import com.sg.flooringmastery.dto.Order;
import com.sg.flooringmastery.dto.Product;
import com.sg.flooringmastery.dto.Tax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

public class FlooringMasteryView {
    //class to output and take in data
    private UserIo io;
    private static final DateTimeFormatter date_Input = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public FlooringMasteryView(UserIo io) {
        this.io = io;
    }

    public int displayMainMenuAndGetSelection() {
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print("* <<Flooring Program>>");
        io.print("* 1. Display Orders");
        io.print("* 2. Add an Order");
        io.print("* 3. Edit an Order");
        io.print("* 4. Remove an Order");
        io.print("* 5. Export All Order");
        io.print("* 6. Quit");
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");

        return io.readInt("Please select an option:", 1, 6);
    }

    public LocalDate getDateInput(){
        //loop until proper date is entered in correct format
        while (true) {
            String input = io.readString("Please enter the order date (MM/DD/YYYY): ").trim();
            try {
                //tries to convert text to LocalDate
                LocalDate date = LocalDate.parse(input, date_Input);
                //checks if new formatted date is the same as user input date
                if (date.format(date_Input).equals(input)) {
                    return date; //breaks loop and returns date
                }
            } catch (DateTimeParseException e) {

            }
            //if date incorrect and format incorrect
            io.print("Please enter a real date in MM/DD/YYYY format, with a year from 0001 to 9999.");
        }
    }

    //gets list of orders from OrderList
    public void displayOrders(List<Order> orderList){
        if (orderList.isEmpty()) {
            displayErrorMessage("Orders not found for this date.");
        } else {
            for (Order order : orderList) { //for each line in order
                displayOrderInfo(order);
            }
        }

    }

    public void displayOrderInfo(Order order){
        io.print("Order Number: " + order.getOrderNumber());
        io.print("Order Date: " + order.getOrderDate());
        io.print("Customer Name: " + order.getCustomerName());
        io.print("State: " + order.getState());
        io.print("Tax Rate: " + order.getTaxRate().toPlainString() + "%");
        io.print("Product Type: " + order.getProductType());
        io.print("Area: " + order.getArea().toPlainString() + " sq ft");
        io.print("Material price per sq ft: $" + order.getCostPerSquareFoot().toPlainString());
        io.print("Labour price per sq ft: $" + order.getLabourCostPerSquareFoot().toPlainString());
        io.print("Material Cost: $" + order.getMaterialCost().toPlainString());
        io.print("Labour Cost: $" + order.getLabourCost().toPlainString());
        io.print("Tax: $" + order.getTax().toPlainString());
        io.print("Total Cost: $" + order.getTotal().toPlainString());
    }

    public void displayAddOrderBanner(){
        io.print("==Add Order==");
    }

    public Order getAddOrderInput(List<Tax> taxList, List<Product> productsList){
        LocalDate orderDate = getDateInput();
        //checks if the date entered is in the future
        while (!orderDate.isAfter(LocalDate.now())) {
            io.print("The order date must be in the future, after today.");
            orderDate = getDateInput();
        }

        String name = io.readString("please enter name");
        while (validateNameInput(name) == null) {
            io.print("Enter name (Use letters, numbers, spaces, periods and commas only).");
            name = io.readString("please enter name");
        }

        String state = io.readString("please enter state");
        Tax validTax = validateStateInput(state, taxList);
        while (validTax == null) {
            io.print("We cannot sell in that state. Please try again.");
            state = io.readString("please enter state");
            validTax = validateStateInput(state, taxList);
        }

        //shows the products and prices from the product file
        for (Product product : productsList) {
            io.print(product.getProductType() + " - Material: $" + product.getCostPerSquareFoot()
                    + " Labour: $" + product.getLabourCostPerSquareFoot());
        }
        String productType = io.readString("please enter product type");
        Product validProduct = validateProductInput(productType, productsList);
        while (validProduct == null) {
            io.print("Product not found. Please try again.");
            productType = io.readString("please enter product type");
            validProduct = validateProductInput(productType, productsList);
        }

        //takes string to convert it to bigdecimal
        String areaInput = io.readString("please enter area");
        BigDecimal area = validateAreaInput(areaInput);
        while (area == null) {
            io.print("Area must be a number of at least 100 square feet.");
            areaInput = io.readString("please enter area");
            area = validateAreaInput(areaInput);
        }

        //creates new order with new inputted details
        Order newOrder = new Order();
        //converts the LocalDate input into the Date type used by Order
        Date dateForOrder = Date.from(orderDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        newOrder.setOrderDate(dateForOrder);
        newOrder.setCustomerName(validateNameInput(name));
        newOrder.setState(validTax.getStateAbr());
        newOrder.setProductType(validProduct.getProductType());
        newOrder.setArea(area);
        return newOrder;
    }


    public void displayAddOrderSuccess(){
        io.print("Order has been added successfully");

    }

    public void displayEditOrderBanner(){
        io.print("==Edit Order==");

    }

    public int getOrderNumberInput(){
        return io.readInt("please enter order number");
    }

    public Order getEditOrderInput(Order order, List<Tax> taxList, List<Product> productsList){
        io.print("Editing order #" + order.getOrderNumber());

        //creates a copy so the saved order is not changed before the user confirms
        Order editedOrder = new Order(order);

        String customerName = io.readString("please enter edit for customer name (" + order.getCustomerName() + ")");
        //trims spaces, then checks if the input for customer name is empty
        while (!customerName.trim().isEmpty() && validateNameInput(customerName) == null) {
            io.print("Use letters, numbers, spaces, periods and commas only.");
            customerName = io.readString("please enter edit for customer name (" + order.getCustomerName() + ")");
        }
        if(!customerName.trim().isEmpty()){
            editedOrder.setCustomerName(validateNameInput(customerName));
        }

        String state = io.readString("please enter edit for state (" + order.getState() + ")");
        Tax validTax = validateStateInput(state, taxList);
        while (!state.trim().isEmpty() && validTax == null) {
            io.print("We cannot sell in that state. Please try again.");
            state = io.readString("please enter edit for state (" + order.getState() + ")");
            validTax = validateStateInput(state, taxList);
        }
        if(!state.trim().isEmpty()){
            editedOrder.setState(validTax.getStateAbr());
        }

        String productType = io.readString("please enter edit for product type (" + order.getProductType() + "):");
        Product validProduct = validateProductInput(productType, productsList);
        while (!productType.trim().isEmpty() && validProduct == null) {
            io.print("Product not found. Please try again.");
            productType = io.readString("please enter edit for product type (" + order.getProductType() + "):");
            validProduct = validateProductInput(productType, productsList);
        }
        if(!productType.trim().isEmpty()){
            editedOrder.setProductType(validProduct.getProductType());
        }

        String areaStr = io.readString("please enter edit for area (" + order.getArea() + "):");
        BigDecimal area = validateAreaInput(areaStr);
        while (!areaStr.trim().isEmpty() && area == null) {
            io.print("Area must be a number of at least 100 square feet.");
            areaStr = io.readString("please enter edit for area (" + order.getArea() + "):");
            area = validateAreaInput(areaStr);
        }
        if(!areaStr.trim().isEmpty()){
            editedOrder.setArea(area);
        }
        return editedOrder;
    }

    public void displayEditOrderSuccess(){
        io.print("Order has been edited successfully");

    }

    public void displayRemoveOrderBanner(){
        io.print("==Remove Order==");
    }

    public boolean getConfirmation(){
        String confirmation = io.readString("are you sure you want to confirm? (Y/N)");
        //allows both upper and lower case when taking in input
        return confirmation.equalsIgnoreCase("Y");

    }

    public void displayRemoveOrderSuccess(){
        io.print("Order has been removed successfully");

    }
    public void displayExportDataSuccess(){
        io.print("Order has been exported successfully");
    }

    public void displayExitMessage(){
        io.print("Exiting program..");

    }

    public void displayErrorMessage(String errorMsg){
        io.print("==ERROR==");
        io.print(errorMsg);
    }

    public void displayUnknownCommandMessage(){
        io.print("Unknown command");
    }

    private String validateNameInput(String input){
        String name = input.trim();
        if (name.isEmpty()) {
            return null;
        }
        String allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789., ";
        //checks each character of name
        for (int i = 0; i < name.length(); i++) {
            //return -1 if character is not allowed
            //then if -1 return null
            if (allowed.indexOf(name.charAt(i)) == -1) {
                return null;
            }
        }
        //returns a string without extra white space
        return name;
    }

    private Tax validateStateInput(String strInput, List<Tax> validTax){
        for (Tax tax : validTax) { //checks list of taxes from file
            //if tax is the same as input return tax
            if (tax.getStateAbr().equalsIgnoreCase(strInput.trim()) || tax.getState().equalsIgnoreCase(strInput.trim())) {
                return tax;
            }
        }
        return null; //if state isn't found return null
    }

    private Product validateProductInput(String productInput, List<Product> validProduct){
        for(Product product : validProduct){ //checks list of products from file
            if(product.getProductType().equalsIgnoreCase(productInput.trim())){
                return product;
            }
        }
        return null; //if product isn't found return null
    }

    private BigDecimal validateAreaInput(String input){
        //takes in area without extra white spaces
        if (input.isEmpty()) {
            return null;
        }
        //checks if input contains numbers
        for (int i = 0; i < input.length(); i++) {
            if ("0123456789.".indexOf(input.charAt(i)) == -1) {
                return null;
            }
        }
        try {
            //rejects invalid numbers by reassigning it to BigDecimal
            BigDecimal area = new BigDecimal(input);
            //compares if BigDecimal area is at least 100
            if (area.compareTo(new BigDecimal("100")) >= 0) {
                return area;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }
}
