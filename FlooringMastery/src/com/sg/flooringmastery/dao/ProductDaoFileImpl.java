package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.dto.Product;
import com.sg.flooringmastery.service.PersistenceException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProductDaoFileImpl implements ProductDao {
    public static final String product_file = "Data/Products.txt";
    public static final String delimiter = ",";
    private static final String header = "ProductType,CostPerSquareFoot,LaborCostPerSquareFoot";
    private String filename;
    private Map<String, Product> allProducts = new HashMap<>();

    //app uses the usual file inside Data
    public ProductDaoFileImpl() {
        this(product_file);
    }

    //a test can provide a separate file to keep the real data unchanged
    public ProductDaoFileImpl(String filename) {
        this.filename = filename;
    }

    //reloads the file and returns a sorted list of the available products
    @Override
    public List<Product> getAllProducts() throws PersistenceException {
        loadFile();
        List<String> keys = new ArrayList<>(allProducts.keySet());
        Collections.sort(keys);
        List<Product> result = new ArrayList<>();
        for (String key : keys) {
            result.add(allProducts.get(key));
        }
        return result;
    }

    //read the header then turns each remaining row into a product object
    private void loadFile() throws PersistenceException {
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(filename)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not read " + filename + ". Check the working directory.", e);
        }
        Map<String, Product> loaded = new HashMap<>();
        int lineNumber = 1;
        try {
            if (!scanner.hasNextLine() || !scanner.nextLine().equals(header)) {
                throw new PersistenceException("Missing or invalid header in " + filename);
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                Product current = unmarshallProduct(line);
                String key = current.getProductType().toLowerCase();
                if (loaded.containsKey(key)) {
                    throw new IllegalArgumentException("Duplicate product entry.");
                }
                loaded.put(key, current);
            }
            if (scanner.ioException() != null) {
                throw new PersistenceException("Could not finish reading " + filename, scanner.ioException());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenceException("Invalid product in " + filename
                    + " at line " + lineNumber + ": " + e.getMessage(), e);
        } finally {
            scanner.close(); // Close the file even when a row contains invalid data.
        }
        if (loaded.isEmpty()) {
            throw new PersistenceException("No products are available in " + filename);
        }
        allProducts = loaded;
    }

    //unmarshalling means converting one text line into an object
    private Product unmarshallProduct(String line) {
        String[] tokens = line.split(delimiter);
        if (tokens.length != 3) {
            throw new IllegalArgumentException("A product row must have 3 fields");
        }
        Product product = new Product();
        product.setProductType(tokens[0].trim());
        product.setCostPerSquareFoot(new BigDecimal(tokens[1].trim()));
        product.setLabourCostPerSquareFoot(new BigDecimal(tokens[2].trim()));
        if (product.getProductType().isEmpty() || product.getCostPerSquareFoot().signum() < 0
                || product.getLabourCostPerSquareFoot().signum() < 0) {
            throw new IllegalArgumentException("Products need a name and positive prices");
        }
        return product;
    }
}
