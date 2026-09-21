package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.dto.Order;
import com.sg.flooringmastery.service.PersistenceException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderDao {
    //finds the highest number across every date, then adds one.
    int getNextOrderNumber() throws PersistenceException;
    Order addOrder(Order order) throws PersistenceException;
    Order getOrder(LocalDate date, int orderNumber) throws PersistenceException;
    Order editOrder(Order order) throws PersistenceException;
    List<Order> getOrdersForDate(LocalDate date) throws PersistenceException;
    Map<LocalDate, Map<Integer, Order>> getAllOrders() throws PersistenceException;
    Order removeOrder(LocalDate date, int orderNumber) throws PersistenceException;
}
