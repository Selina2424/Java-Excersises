package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.dto.Order;
import com.sg.flooringmastery.dto.Product;
import com.sg.flooringmastery.dto.Tax;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OtherDaoFileImplTest {
    private File testFolder;
    private File taxFile;
    private File productFile;
    private File exportFile;
    private File auditFile;

    @BeforeEach //runs before every test method so one test doesnt affect another
    public void setUp() throws Exception {
        testFolder = new File("TestData");
        deleteTestFolder();
        testFolder.mkdir();

        taxFile = new File(testFolder, "Taxes.txt");
        PrintWriter taxWriter = new PrintWriter(new FileWriter(taxFile));
        taxWriter.println("State,StateName,TaxRate");
        taxWriter.println("CA,California,25.00");
        taxWriter.close();

        productFile = new File(testFolder, "Products.txt");
        PrintWriter productWriter = new PrintWriter(new FileWriter(productFile));
        productWriter.println("ProductType,CostPerSquareFoot,LaborCostPerSquareFoot");
        productWriter.println("Tile,3.50,4.15");
        productWriter.close();

        exportFile = new File(testFolder, "DataExport.txt");
        auditFile = new File(testFolder, "audit.txt");
    }

    @AfterEach //runs after each test method so tests can restore/delete temp files
    public void cleanUp() {
        deleteTestFolder();
    }

    @Test //runs test
    public void testTaxDaoReadsTaxFile() throws Exception {
        TaxDao taxDao = new TaxDaoFileImpl(taxFile.getPath());

        List<Tax> taxes = taxDao.getAllTaxes();

        assertEquals(1, taxes.size());
        assertEquals("CA", taxes.get(0).getStateAbr());
        assertEquals(new BigDecimal("25.00"), taxes.get(0).getTaxRate());
    }

    @Test //runs test
    public void testProductDaoReadsProductFile() throws Exception {
        ProductDao productDao = new ProductDaoFileImpl(productFile.getPath());

        List<Product> products = productDao.getAllProducts();

        assertEquals(1, products.size());
        assertEquals("Tile", products.get(0).getProductType());
        assertEquals(new BigDecimal("3.50"), products.get(0).getCostPerSquareFoot());
    }

    @Test //runs test
    public void testExportOverwritesOldExport() throws Exception {
        ExportDao exportDao = new ExportDaoFileImpl(exportFile.getPath());
        LocalDate date = LocalDate.of(2030, 6, 1);
        Order order = createOrder(date);
        Map<Integer, Order> dailyOrders = new HashMap<>();
        dailyOrders.put(1, order);
        Map<LocalDate, Map<Integer, Order>> allOrders = new HashMap<>();
        allOrders.put(date, dailyOrders);

        exportDao.exportData(allOrders);
        exportDao.exportData(allOrders);

        assertEquals(2, countLines(exportFile));
    }

    @Test //runs tests
    public void testAuditAppendsEntries() throws Exception {
        AuditDao auditDao = new AuditDaoFileImpl(auditFile.getPath());

        auditDao.writeAuditEntry("First entry");
        auditDao.writeAuditEntry("Second entry");

        assertEquals(2, countLines(auditFile));
    }

    private int countLines(File file) throws Exception {
        Scanner scanner = new Scanner(file);
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            lineCount++;
        }
        scanner.close();
        return lineCount;
    }

    private Order createOrder(LocalDate date) {
        Order order = new Order();
        order.setOrderNumber(1);
        order.setOrderDate(Date.from(date.atStartOfDay(
                ZoneId.systemDefault()).toInstant()));
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

    private void deleteTestFolder() {
        if (testFolder != null && testFolder.exists()) {
            File[] files = testFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            testFolder.delete();
        }
    }
}
