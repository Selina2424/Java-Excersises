package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.dto.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class OrderDaoFileImplTest {
    //temp folder for real orders folder
    private File testFolder;
    //stores orderdoa being tested
    private OrderDao dao;
    private LocalDate orderDate;

    @BeforeEach //runs before every test method so one test doesnt affect another
    public void setUp() {
        //create a file object to represent a folder called test orders
        testFolder = new File("TestOrders");
        //remove any testorder folder left by and earlier test
        deleteTestFolder();
        //create a fresh, empty testorder folder
        testFolder.mkdir();
        //creat the real file dao, but use test folder
        dao = new OrderDaoFileImpl(testFolder.getPath());
        //used fixed date
        orderDate = LocalDate.of(2030, 6, 1);
    }

    @AfterEach //runs after each test method so tests can restore/delete temp files
    public void cleanUp() {
        deleteTestFolder();
    }

    @Test //test to run
    public void testAddAndGetOrder() throws Exception {
        //create a complete order with order number 1
        Order order = createOrder(1);
        //save order using dao
        dao.addOrder(order);
        //load the same order using its date and order number.
        Order result = dao.getOrder(orderDate, 1);

        //The order read from the file should equal the order saved
        assertEquals(order, result);
    }

    @Test //test to run
    public void testEditOrder() throws Exception {
        //creates and saves and order
        Order order = createOrder(1);
        dao.addOrder(order);
        //changes the customer name on the order object
        order.setCustomerName("Edited Name");
        //saves edited order
        dao.editOrder(order);
        //loads order again and checks that new name was saved
        assertEquals("Edited Name",
                dao.getOrder(orderDate, 1).getCustomerName());
    }

    @Test //test to run
    public void testRemoveOrder() throws Exception {
        //creates and saves order num1
        dao.addOrder(createOrder(1));
        //removes the order and stores the returned order
        Order removed = dao.removeOrder(orderDate, 1);
        //the dao should return the order that it removed
        assertNotNull(removed);
        //after try to load the removed order it should return null
        assertNull(dao.getOrder(orderDate, 1));
    }

    @Test //tests to run
    public void testNextOrderNumber() throws Exception {
        //add order with number 4
        dao.addOrder(createOrder(4));
        //the next available order num should be 5
        assertEquals(5, dao.getNextOrderNumber());
    }

    //creates a complete order that can be written to and read from a file
    private Order createOrder(int orderNumber) {
        Order order = new Order();
        //user number given by the test
        order.setOrderNumber(orderNumber);
        //order stores its date as date but tests use localdate
        //atStartOfDay() gives the start of the selected date
        //atZone() applies the computers time zone
        //toInstant() creates a point in time
        //date.from() converts that point into date

        order.setOrderDate(Date.from(orderDate.atStartOfDay(
                ZoneId.systemDefault()).toInstant()));

        //sets all values needed for a complete order
        order.setCustomerName("Ada Lovelace");
        order.setState("CA");
        order.setTaxRate(new BigDecimal("25.00"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("249.00"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLabourCostPerSquareFoot(new BigDecimal("4.15"));
        order.setMaterialCost(new BigDecimal("871.50"));
        order.setLabourCost(new BigDecimal("1033.35"));
        order.setTax(new BigDecimal("476.21"));
        order.setTotal(new BigDecimal("2381.06"));
        return order;
    }

    //removes files made by a test so each test starts with an empty folder
    private void deleteTestFolder() {
        //only try to delete it if the file object exists
        if (testFolder != null && testFolder.exists()) {
            //gets all files stored in TestOrders
            File[] files = testFolder.listFiles();
            //listFiles() could return null if the folder cant be read
            if (files != null) {
                //delete each order file inside the folder
                for (File file : files) {
                    file.delete();
                }
            }
            testFolder.delete();
        }
    }
}
