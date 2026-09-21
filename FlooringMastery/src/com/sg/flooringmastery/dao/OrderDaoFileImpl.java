package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.dto.Order;
import com.sg.flooringmastery.service.PersistenceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Date;

public class OrderDaoFileImpl implements OrderDao {
    public static final String ORDER_FOLDER = "Orders";
    public static final String DELIMITER = ",";
    public static final String HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,"
            + "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("MMddyyyy");
    private String orderFolder;

    //first find a date, then find an order number within that dates map
    private Map<LocalDate, Map<Integer, Order>> orders = new HashMap<>();

    //app uses the normal Orders folder.
    public OrderDaoFileImpl() {
        this(ORDER_FOLDER);
    }

    //tests can supply a different folder so they never change your real orders
    public OrderDaoFileImpl(String orderFolder) {
        this.orderFolder = orderFolder;
    }

    //find the highest existing number across every date then add one
    @Override
    public int getNextOrderNumber() throws PersistenceException {
        loadFromFile();
        int largestOrderNumber = 0;
        for (Map<Integer, Order> dailyOrders : orders.values()) {
            for (Integer number : dailyOrders.keySet()) {
                if (number > largestOrderNumber) {
                    largestOrderNumber = number;
                }
            }
        }
        if (largestOrderNumber == Integer.MAX_VALUE) {
            throw new PersistenceException("No more order numbers are available.");
        }
        return largestOrderNumber + 1;
    }

    //put a confirmed new order into its date's map and save that date's file
    @Override
    public Order addOrder(Order order) throws PersistenceException {
        loadFromFile();
        for (Map<Integer, Order> dailyOrders : orders.values()) {
            if (dailyOrders.containsKey(order.getOrderNumber())) {
                throw new PersistenceException("Order number " + order.getOrderNumber() + " already exists.");
            }
        }
        LocalDate date = convertToLocalDate(order.getOrderDate());
        Map<Integer, Order> updated = new HashMap<>();
        if (orders.containsKey(date)) {
            updated.putAll(orders.get(date));
        }
        updated.put(order.getOrderNumber(), new Order(order));
        writeToFile(date, updated);
        orders.put(date, updated);
        return new Order(order);
    }

    //return one order, or null when that date and number are not found
    @Override
    public Order getOrder(LocalDate date, int orderNumber) throws PersistenceException {
        loadFromFile();
        Map<Integer, Order> dailyOrders = orders.get(date);
        if (dailyOrders == null || !dailyOrders.containsKey(orderNumber)) {
            return null;
        }
        //return a copy: changing it must not change the DAO until an edit is confirmed
        return new Order(dailyOrders.get(orderNumber));
    }

    //return this date's orders in order number order for a clear display
    @Override
    public List<Order> getOrdersForDate(LocalDate date) throws PersistenceException {
        loadFromFile();
        List<Order> orderList = new ArrayList<>();
        if (orders.containsKey(date)) {
            List<Integer> numbers = new ArrayList<>(orders.get(date).keySet());
            Collections.sort(numbers);
            for (Integer number : numbers) {
                orderList.add(new Order(orders.get(date).get(number)));
            }
        }
        return orderList;
    }

    //copy every date's orders so the service can pass them to the export DAO
    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() throws PersistenceException {
        loadFromFile();
        Map<LocalDate, Map<Integer, Order>> copy = new HashMap<>();
        for (LocalDate date : orders.keySet()) {
            Map<Integer, Order> dailyCopy = new HashMap<>();
            for (Order order : orders.get(date).values()) {
                dailyCopy.put(order.getOrderNumber(), new Order(order));
            }
            copy.put(date, dailyCopy);
        }
        return copy;
    }

    //replace a confirmed order
    //return null if it no longer exists
    @Override
    public Order editOrder(Order order) throws PersistenceException {
        loadFromFile();
        LocalDate orderDate = convertToLocalDate(order.getOrderDate());
        Map<Integer, Order> existing = orders.get(orderDate);
        if (existing == null || !existing.containsKey(order.getOrderNumber())) {
            return null;
        }
        Map<Integer, Order> updated = new HashMap<>(existing);
        updated.put(order.getOrderNumber(), new Order(order));
        writeToFile(orderDate, updated);
        orders.put(orderDate, updated);
        return new Order(order);
    }

    //remove one confirmed order and rewrite the affected date's file
    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws PersistenceException {
        loadFromFile();
        Map<Integer, Order> existing = orders.get(date);
        if (existing == null || !existing.containsKey(orderNumber)) {
            return null;
        }
        Map<Integer, Order> updated = new HashMap<>(existing);
        Order removed = updated.remove(orderNumber);
        writeToFile(date, updated);
        orders.put(date, updated);
        return new Order(removed);
    }

    //load fresh data so deleted records cannot remain in an old map
    private void loadFromFile() throws PersistenceException {
        Map<LocalDate, Map<Integer, Order>> loaded = new HashMap<>();
        List<Integer> usedNumbers = new ArrayList<>();
        File folder = new File(orderFolder);
        if (!folder.exists()) {
            orders = loaded; //a new installation may not have any orders yet
            return;
        }
        File[] files = folder.listFiles();
        if (files == null) {
            throw new PersistenceException("Could not read the Orders folder: " + orderFolder);
        }
        for (File file : files) {
            String name = file.getName();
            if (!name.startsWith("Orders_") || !name.endsWith(".txt")) {
                continue; //ignore unrelated files in the folder
            }
            if (name.length() != 19) {
                throw new PersistenceException("Invalid order filename: " + name);
            }
            LocalDate date;
            try {
                //Orders_01022030.txt: characters 7 to 14 contain the date
                String dateText = name.substring(7, 15);
                date = LocalDate.parse(dateText, FILE_DATE);
                //format the date back to text to check the filename format
                if (!date.format(FILE_DATE).equals(dateText)) {
                    throw new PersistenceException("Invalid year in " + name);
                }
            } catch (DateTimeParseException e) {
                throw new PersistenceException("Invalid date in " + name, e);
            }
            Map<Integer, Order> dailyOrders = readOrderFile(file, date);
            for (Integer number : dailyOrders.keySet()) {
                if (usedNumbers.contains(number)) {
                    throw new PersistenceException("Duplicate order number " + number + " in " + name);
                }
                usedNumbers.add(number);
            }
            loaded.put(date, dailyOrders);
        }
        //only replace the existing map after all files have been read successfully
        orders = loaded;
    }

    //read one header and convert each data line into an Order object
    private Map<Integer, Order> readOrderFile(File file, LocalDate date) throws PersistenceException {
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(file)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not read " + file.getName(), e);
        }
        Map<Integer, Order> dailyOrders = new HashMap<>();
        int lineNumber = 1;
        try {
            if (!scanner.hasNextLine() || !scanner.nextLine().equals(HEADER)) {
                throw new PersistenceException("Missing or invalid header in " + file.getName());
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                Order order = unmarshallOrder(line, date);
                if (dailyOrders.containsKey(order.getOrderNumber())) {
                    throw new IllegalArgumentException("Duplicate order number.");
                }
                dailyOrders.put(order.getOrderNumber(), order);
            }
            if (scanner.ioException() != null) {
                throw new PersistenceException("Could not finish reading " + file.getName(), scanner.ioException());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenceException("Invalid order in " + file.getName()
                    + " at line " + lineNumber + ": " + e.getMessage(), e);
        } finally {
            scanner.close(); //finally runs whether reading succeeds or an exception is thrown
        }
        return dailyOrders;
    }

    //write the header followed by each order by using PrintWriter
    private void writeToFile(LocalDate date, Map<Integer, Order> dailyOrders) throws PersistenceException {
        File folder = new File(orderFolder);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new PersistenceException("Could not create the Orders folder.");
        }
        File file = new File(folder, "Orders_" + date.format(FILE_DATE) + ".txt");
        List<String> lines = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>(dailyOrders.keySet());
        Collections.sort(numbers);
        for (Integer number : numbers) {
            lines.add(marshallOrder(dailyOrders.get(number)));
        }
        PrintWriter out;
        try {
            //fileWriter without 'true' replaces the old contents instead of appending
            out = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new PersistenceException("Could not save orders to " + file, e);
        }
        out.println(HEADER);
        for (String line : lines) {
            out.println(line);
        }
        out.close(); //close also outputs pending text to the file
        if (out.checkError()) {
            throw new PersistenceException("An error occurred while saving " + file);
        }
        //an empty list still writes the header, so removing the last order works
    }

    //turns an order object into one line for the order file
    private String marshallOrder(Order order) {
        String orderAsText = order.getOrderNumber() + DELIMITER;
        orderAsText += addQuotesToCustomerName(order.getCustomerName()) + DELIMITER;
        orderAsText += order.getState() + DELIMITER;
        orderAsText += order.getTaxRate() + DELIMITER;
        orderAsText += order.getProductType() + DELIMITER;
        orderAsText += order.getArea() + DELIMITER;
        orderAsText += order.getCostPerSquareFoot() + DELIMITER;
        orderAsText += order.getLabourCostPerSquareFoot() + DELIMITER;
        orderAsText += order.getMaterialCost() + DELIMITER;
        orderAsText += order.getLabourCost() + DELIMITER;
        orderAsText += order.getTax() + DELIMITER;
        orderAsText += order.getTotal();
        return orderAsText;
    }

    //turns one line from an order file back into an Order object
    private Order unmarshallOrder(String orderAsText, LocalDate orderDate) {
        List<String> orderTokens = splitOrderLine(orderAsText);
        if (orderTokens.size() != 12) {
            throw new IllegalArgumentException("An order row must contain 12 fields.");
        }

        Order orderFromFile = new Order();
        orderFromFile.setOrderNumber(Integer.parseInt(orderTokens.get(0).trim()));
        orderFromFile.setCustomerName(orderTokens.get(1));
        orderFromFile.setState(orderTokens.get(2).trim());
        orderFromFile.setTaxRate(new BigDecimal(orderTokens.get(3).trim()));
        orderFromFile.setProductType(orderTokens.get(4).trim());
        orderFromFile.setArea(new BigDecimal(orderTokens.get(5).trim()));
        orderFromFile.setCostPerSquareFoot(new BigDecimal(orderTokens.get(6).trim()));
        orderFromFile.setLabourCostPerSquareFoot(new BigDecimal(orderTokens.get(7).trim()));
        orderFromFile.setMaterialCost(new BigDecimal(orderTokens.get(8).trim()));
        orderFromFile.setLabourCost(new BigDecimal(orderTokens.get(9).trim()));
        orderFromFile.setTax(new BigDecimal(orderTokens.get(10).trim()));
        orderFromFile.setTotal(new BigDecimal(orderTokens.get(11).trim()));
        //the order date comes from the order filename
        Date dateForOrder = Date.from(orderDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        orderFromFile.setOrderDate(dateForOrder);

        if (orderFromFile.getOrderNumber() < 1) {
            throw new IllegalArgumentException("Order numbers must be positive.");
        }
        return orderFromFile;
    }

    //adds quotes only when the valid customer name contains a comma
    private String addQuotesToCustomerName(String customerName) {
        if (customerName.contains(",")) {
            return "\"" + customerName + "\"";
        }
        return customerName;
    }

    //splits a line while keeping a comma inside a quoted customer name
    private List<String> splitOrderLine(String line) {
        List<String> fields = new ArrayList<>();
        String currentField = "";
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentCharacter = line.charAt(i);
            if (currentCharacter == '"') {
                insideQuotes = !insideQuotes;
            } else if (currentCharacter == ',' && !insideQuotes) {
                fields.add(currentField);
                currentField = "";
            } else {
                currentField += currentCharacter;
            }
        }
        if (insideQuotes) {
            throw new IllegalArgumentException("A customer name quote was not closed.");
        }
        fields.add(currentField);
        return fields;
    }

    //convert the date stored in Order into the LocalDate used as the map key
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
