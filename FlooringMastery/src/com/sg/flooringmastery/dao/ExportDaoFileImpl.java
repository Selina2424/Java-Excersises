package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.dto.Order;
import com.sg.flooringmastery.service.PersistenceException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExportDaoFileImpl implements ExportDao {
    public static final String export_File = "Backup/DataExport.txt";
    public static final String delimiter = ",";
    private static final String header = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total,OrderDate";
    private String exportFile;

    //app uses the Backup folder
    public ExportDaoFileImpl() {
        this(export_File);
    }

    //tests can write their export to a separate file
    public ExportDaoFileImpl(String exportFile) {
        this.exportFile = exportFile;
    }

    //create export lines, create the folder if needed, and replace the previous export
    @Override
    public void exportData(Map<LocalDate, Map<Integer, Order>> allOrders) throws PersistenceException {
        DateTimeFormatter exportDate = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        List<String> lines = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>(allOrders.keySet());
        Collections.sort(dates);
        for (LocalDate date : dates) {
            List<Integer> numbers = new ArrayList<>(allOrders.get(date).keySet());
            Collections.sort(numbers);
            for (Integer number : numbers) {
                Order order = allOrders.get(date).get(number);
                lines.add(marshallOrder(order) + delimiter + date.format(exportDate));
            }
        }
        File file = new File(exportFile);
        File folder = file.getAbsoluteFile().getParentFile();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new PersistenceException("Could not create the Backup folder.");
        }
        PrintWriter out;
        try {
            // No 'true', so it overwrite instead of adding duplicate export rows
            out = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new PersistenceException("Could not export orders to " + exportFile, e);
        }
        out.println(header);
        for (String line : lines) {
            out.println(line);
        }
        out.close();
        if (out.checkError()) {
            throw new PersistenceException("An error occurred while exporting orders.");
        }
    }

    //converts an order object into one line for the export file
    private String marshallOrder(Order order) {
        return order.getOrderNumber() + delimiter
                + addQuotesToCustomerName(order.getCustomerName()) + delimiter
                + order.getState() + delimiter
                + order.getTaxRate() + delimiter
                + order.getProductType() + delimiter
                + order.getArea() + delimiter
                + order.getCostPerSquareFoot() + delimiter
                + order.getLabourCostPerSquareFoot() + delimiter
                + order.getMaterialCost() + delimiter
                + order.getLabourCost() + delimiter
                + order.getTax() + delimiter
                + order.getTotal();
    }

    //customer names with commas need quotation marks in a comma separated file
    private String addQuotesToCustomerName(String customerName) {
        if (customerName.contains(delimiter)) {
            return "\"" + customerName + "\"";
        }
        return customerName;
    }
}
