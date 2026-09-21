package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.service.PersistenceException;
import com.sg.flooringmastery.dto.Order;
import java.time.LocalDate;
import java.util.Map;

public interface ExportDao {
    void exportData(Map<LocalDate, Map<Integer, Order>> allOrders) throws PersistenceException;
}
