package com.sg.flooringmastery.service;
import com.sg.flooringmastery.dto.*;
import java.time.LocalDate;
import java.util.List;

public interface ServiceLayer {

    List<Order> getOrderByDate(LocalDate date) throws PersistenceException;

    Order getOrder(LocalDate date, int orderNumber) throws NoSuchOrderException, PersistenceException;

    List<Tax> getTaxes() throws PersistenceException;

    List<Product> getProducts() throws PersistenceException;

    //prepare methods to check and calculate without saving
    Order prepareNewOrder(Order order) throws PersistenceException;

    Order prepareEditedOrder(LocalDate date, int orderNumber, Order changes)
            throws NoSuchOrderException, PersistenceException;

    //after user confirms these methods are checked
    void createOrder(Order order) throws PersistenceException;

    void editOrder(LocalDate date, int orderNumber, Order changes)

            throws NoSuchOrderException, PersistenceException;

    void removeOrder(LocalDate date, int orderNumber) throws NoSuchOrderException, PersistenceException;

    void exportAllData() throws PersistenceException;

}
