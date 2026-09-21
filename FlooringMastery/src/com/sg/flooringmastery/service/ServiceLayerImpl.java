package com.sg.flooringmastery.service;
import com.sg.flooringmastery.dao.*;
import com.sg.flooringmastery.dto.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ServiceLayerImpl implements ServiceLayer {
    private final OrderDao orderDao;
    private final TaxDao taxDao;
    private final ProductDao productDao;
    private final ExportDao exportDao;
    private final AuditDao auditDao;


    public ServiceLayerImpl(OrderDao orderDao, TaxDao taxDao, ProductDao productDao, ExportDao exportDao, AuditDao auditDao) {
        this.orderDao = orderDao;
        this.taxDao = taxDao;
        this.productDao = productDao;
        this.exportDao = exportDao;
        this.auditDao = auditDao;

    }

    //ask the order DAO for the orders for the requested day
    @Override
    public List<Order> getOrderByDate(LocalDate date) throws PersistenceException {
        return orderDao.getOrdersForDate(date);
    }

    //finds order by its date and number
    @Override
    public Order getOrder(LocalDate date, int orderNumber)
            throws NoSuchOrderException, PersistenceException {
        Order order = orderDao.getOrder(date, orderNumber);
        if (order == null) {
            throw new NoSuchOrderException("Order #" + orderNumber + " was not found on " + date);
        }
        return new Order(order);
    }

    //returns available states and their tax rates from the tax DAO
    @Override
    public List<Tax> getTaxes() throws PersistenceException {
        return taxDao.getAllTaxes();
    }

    //return the available flooring products and prices from the product DAO
    @Override
    public List<Product> getProducts() throws PersistenceException {
        return productDao.getAllProducts();
    }

    //validate and calculate a new order summary without saving
    @Override
    public Order prepareNewOrder(Order order) throws PersistenceException {
        validateOrderData(order);
        LocalDate orderDate = convertToLocalDate(order.getOrderDate());
        if (!orderDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A new order date must be in the future");
        }
        Order prepared = new Order(order);
        Tax tax = findTax(prepared.getState());
        Product product = findProduct(prepared.getProductType());
        prepared.setCustomerName(prepared.getCustomerName().trim());
        applyTax(prepared, tax);
        applyProduct(prepared, product);
        prepared.setOrderNumber(orderDao.getNextOrderNumber());
        calculateCosts(prepared);
        return prepared;
    }

    //save a valid confirmed new order and then record the action in the audit log.
    @Override
    public void createOrder(Order order) throws PersistenceException {
        // Validate at the service boundary too: another UI could call this without using our view.
        Order prepared = prepareNewOrder(order);
        orderDao.addOrder(prepared);
        writeToAudit("Order #" + prepared.getOrderNumber() + " on "
                + convertToLocalDate(prepared.getOrderDate()) + " CREATED");
    }

    //prepare changes from user while protecting the original order number and date
    @Override
    public Order prepareEditedOrder(LocalDate date, int orderNumber, Order changes)
            throws NoSuchOrderException, PersistenceException {
        Order original = getOrder(date, orderNumber);
        validateOrderData(changes);
        if (!date.equals(convertToLocalDate(changes.getOrderDate()))
                || orderNumber != changes.getOrderNumber()) {
            throw new IllegalArgumentException("The order date and order number cannot be changed");
        }
        //start with saved values not totals supplied by the caller.
        Order prepared = new Order(original); //copies original order
        //trims and adds  customer name
        prepared.setCustomerName(changes.getCustomerName().trim());
        //then area
        prepared.setArea(changes.getArea());
        // finds tax details for the selected state
        applyTax(prepared, findTax(changes.getState()));
        //finds products from the  products file then applies product details
        applyProduct(prepared, findProduct(changes.getProductType()));
        //recalculates costs
        calculateCosts(prepared);
        return prepared;
    }

    //save the changes to the existing order
    @Override
    public void editOrder(LocalDate date, int orderNumber, Order changes)
            throws NoSuchOrderException, PersistenceException {
        Order prepared = prepareEditedOrder(date, orderNumber, changes);
        //if prepared order is empty
        if (orderDao.editOrder(prepared) == null) {
            throw new NoSuchOrderException("The order could no longer be found");
        }
        writeToAudit("Order #" + orderNumber + " on " + date + " EDITED.");
    }

    //remove the confirmed order and add removal in the audit log
    @Override
    public void removeOrder(LocalDate date, int orderNumber)
            throws NoSuchOrderException, PersistenceException {
        getOrder(date, orderNumber); //check it exists before attempting to remove it
        if (orderDao.removeOrder(date, orderNumber) == null) {
            throw new NoSuchOrderException("The order could no longer be found");
        }
        writeToAudit("Order #" + orderNumber + " on " + date + " REMOVED");
    }

    //send every active order to the export DAO, then record the export
    @Override
    public void exportAllData() throws PersistenceException {
        exportDao.exportData(orderDao.getAllOrders());
        writeToAudit("All orders EXPORTED"); //writes to file
    }

    //reject missing or invalid order fields before requesting a DAO to save them
    private void validateOrderData(Order order) {
        if (order == null || order.getOrderDate() == null) {
            throw new IllegalArgumentException("An order and order date are required");
        }
        //both upper and lower case allowed and spaces
        if (order.getCustomerName() == null
                || !isValidName(order.getCustomerName().trim())) {
            throw new IllegalArgumentException("Name is required use letters, numbers, spaces, periods and commas");
        }
        if (order.getState() == null || order.getState().trim().isEmpty()
                || order.getProductType() == null || order.getProductType().trim().isEmpty()) {
            throw new IllegalArgumentException("State and product type are required");
        }
        if (order.getArea() == null || order.getArea().compareTo(new BigDecimal("100")) < 0) {
            throw new IllegalArgumentException("The area must be at least 100 square feet");
        }
    }

    // check each character using the allowed characters
    private boolean isValidName(String name) {
        if (name.isEmpty()) {
            return false;
        }
        String allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789., ";
        for (int i = 0; i < name.length(); i++) {
            if (allowed.indexOf(name.charAt(i)) == -1) {
                return false; // indexOf returns -1 when a character is not in the allowed string.
            }
        }
        return true;
    }

    //find the selected state in the tax list
    private Tax findTax(String state) throws PersistenceException {
        for (Tax tax : taxDao.getAllTaxes()) {
            if (tax.getStateAbr().equalsIgnoreCase(state.trim())
                    || tax.getState().equalsIgnoreCase(state.trim())) {
                return tax;
            }
        }
        //throw an error if it is unavailable
        throw new IllegalArgumentException("We cannot sell in that state, choose a state from the list");
    }

    //find the selected product in the product list
    private Product findProduct(String productType) throws PersistenceException {
        for (Product product : productDao.getAllProducts()) {
            if (product.getProductType().equalsIgnoreCase(productType.trim())) {
                return product;
            }
        }
        //throw an error if it is unavailable
        throw new IllegalArgumentException("Choose a product from the available products");
    }

    //copy the state abbreviation and tax percentage onto the order
    private void applyTax(Order order, Tax tax) {
        order.setState(tax.getStateAbr());
        order.setTaxRate(tax.getTaxRate());
    }

    //copy the selected product name and both per square foot prices onto the order
    private void applyProduct(Order order, Product product) {
        order.setProductType(product.getProductType());
        order.setCostPerSquareFoot(product.getCostPerSquareFoot());
        order.setLabourCostPerSquareFoot(product.getLabourCostPerSquareFoot());
    }

    //calculate material cost, labour cost, tax and total using BigDecimal
    private void calculateCosts(Order order) {
        // BigDecimal(String) avoids doubles binary rounding errors for money
        // Round each payable component to cents, then add those same displayed components
        BigDecimal materialCost = order.getArea().multiply(order.getCostPerSquareFoot()) //times area by cost per sqft
                .setScale(2, RoundingMode.HALF_UP); //rounds to 2 decimal places
        BigDecimal labourCost = order.getArea().multiply(order.getLabourCostPerSquareFoot())
                .setScale(2, RoundingMode.HALF_UP);
        //adds material cost and labour cost then multiplies by tax rate then divides by 100 since tax
        // rate is % then rounds to 2 decimal place
        BigDecimal tax = materialCost.add(labourCost).multiply(order.getTaxRate())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = materialCost.add(labourCost).add(tax); //adds material cost and labour costs and tax
        order.setMaterialCost(materialCost);
        order.setLabourCost(labourCost);
        order.setTax(tax);
        order.setTotal(total);
    }

    //records completed action and explains audit failure
    private void writeToAudit(String entry) throws PersistenceException {
        try {
            auditDao.writeAuditEntry(entry);
        } catch (PersistenceException e) {
            //the order/export was already saved so explains to the user so they dont retry it
            throw new PersistenceException("The data is changed, but the audit log could not be written. " +
                    "Do not repeat the change just to fix the log", e);
        }
    }

    //order stores date
    //while the service uses LocalDate for date comparisons
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
