package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.OrderDao;
import com.sg.flooringmastery.dto.Order;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//test only DAO that stores orders in a map instead of writing files
public class OrderDaoStubImpl implements OrderDao {
    private Map<LocalDate, Map<Integer, Order>> orders = new HashMap<>();

    @Override
    public int getNextOrderNumber() {
        int largestOrderNumber = 0;
        for (Map<Integer, Order> dailyOrders : orders.values()) {
            for (Integer orderNumber : dailyOrders.keySet()) {
                if (orderNumber > largestOrderNumber) {
                    largestOrderNumber = orderNumber;
                }
            }
        }
        return largestOrderNumber + 1;
    }

    @Override
    public Order addOrder(Order order) {
        LocalDate date = convertToLocalDate(order);
        if (!orders.containsKey(date)) {
            orders.put(date, new HashMap<>());
        }
        orders.get(date).put(order.getOrderNumber(), new Order(order));
        return new Order(order);
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) {
        if (!orders.containsKey(date)) {
            return null;
        }
        Order order = orders.get(date).get(orderNumber);
        if (order == null) {
            return null;
        }
        return new Order(order);
    }

    @Override
    public Order editOrder(Order order) {
        LocalDate date = convertToLocalDate(order);
        if (getOrder(date, order.getOrderNumber()) == null) {
            return null;
        }
        orders.get(date).put(order.getOrderNumber(), new Order(order));
        return new Order(order);
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) {
        if (!orders.containsKey(date)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(orders.get(date).values());
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() {
        return orders;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) {
        if (!orders.containsKey(date)) {
            return null;
        }
        return orders.get(date).remove(orderNumber);
    }

    private LocalDate convertToLocalDate(Order order) {
        return order.getOrderDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
