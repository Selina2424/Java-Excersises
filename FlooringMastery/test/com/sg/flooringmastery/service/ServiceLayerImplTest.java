package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.AuditDao;
import com.sg.flooringmastery.dao.ExportDao;
import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.dao.TaxDao;
import com.sg.flooringmastery.dto.Order;
import com.sg.flooringmastery.dto.Product;
import com.sg.flooringmastery.dto.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceLayerImplTest {
    private ServiceLayer service;
    private OrderDaoStubImpl orderDao;
    private List<String> auditEntries;

    @BeforeEach //runs before every test method so one test doesnt affect another
    public void setUp() {
        orderDao = new OrderDaoStubImpl();
        auditEntries = new ArrayList<>();

        Tax california = new Tax("California");
        california.setStateAbr("CA");
        california.setTaxRate(new BigDecimal("25.00"));
        TaxDao taxDao = () -> Arrays.asList(california);

        Product tile = new Product();
        tile.setProductType("Tile");
        tile.setCostPerSquareFoot(new BigDecimal("3.50"));
        tile.setLabourCostPerSquareFoot(new BigDecimal("4.15"));
        ProductDao productDao = () -> Arrays.asList(tile);

        ExportDao exportDao = allOrders -> { };
        AuditDao auditDao = entry -> auditEntries.add(entry);

        service = new ServiceLayerImpl(orderDao, taxDao, productDao,
                exportDao, auditDao);
    }

    @Test //runs test
    public void testPrepareNewOrderCalculatesCosts() throws Exception {
        Order order = createOrder(LocalDate.now().plusDays(1));

        Order result = service.prepareNewOrder(order);

        assertEquals(1, result.getOrderNumber());
        assertEquals(new BigDecimal("871.50"), result.getMaterialCost());
        assertEquals(new BigDecimal("1033.35"), result.getLabourCost());
        assertEquals(new BigDecimal("476.21"), result.getTax());
        assertEquals(new BigDecimal("2381.06"), result.getTotal());
    }

    @Test //runs test
    public void testCreateOrderSavesOrderAndWritesAudit() throws Exception {
        Order order = createOrder(LocalDate.now().plusDays(1));

        service.createOrder(order);

        assertEquals(1, service.getOrderByDate(LocalDate.now().plusDays(1)).size());
        assertEquals(1, auditEntries.size());
    }

    @Test //runs test
    public void testAreaBelowOneHundredIsRejected() {
        Order order = createOrder(LocalDate.now().plusDays(1));
        order.setArea(new BigDecimal("99"));

        assertThrows(IllegalArgumentException.class,
                () -> service.prepareNewOrder(order));
    }

    @Test //runs test
    public void testTodayIsRejectedForNewOrder() {
        Order order = createOrder(LocalDate.now());

        assertThrows(IllegalArgumentException.class,
                () -> service.prepareNewOrder(order));
    }

    @Test //runs test
    public void testUnknownStateIsRejected() {
        Order order = createOrder(LocalDate.now().plusDays(1));
        order.setState("XX");

        assertThrows(IllegalArgumentException.class,
                () -> service.prepareNewOrder(order));
    }

    @Test //runs tests
    public void testMissingOrderThrowsNoSuchOrderException() {
        assertThrows(NoSuchOrderException.class,
                () -> service.getOrder(LocalDate.now().plusDays(1), 50));
    }

    //creates a valid order that can be reused by each service test
    private Order createOrder(LocalDate orderDate) {
        Order order = new Order();
        order.setOrderDate(Date.from(orderDate.atStartOfDay(
                ZoneId.systemDefault()).toInstant()));
        order.setCustomerName("Ada Lovelace");
        order.setState("CA");
        order.setProductType("Tile");
        order.setArea(new BigDecimal("249.00"));
        return order;
    }
}
