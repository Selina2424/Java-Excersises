package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.dto.Tax;
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

public class TaxDaoFileImpl implements TaxDao {
    public static final String tax_File = "Data/Taxes.txt";
    public static final String delimiter = ",";
    private static final String header = "State,StateName,TaxRate";
    private String filename;
    private Map<String, Tax> allTaxes = new HashMap<>();

    //app uses the usual file inside Data.
    public TaxDaoFileImpl() {
        this(tax_File);
    }

    //a test can provide a separate file to keep the real data unchanged
    public TaxDaoFileImpl(String filename) {
        this.filename = filename;
    }

    //reload the file and return a sorted list of the available taxes
    @Override
    public List<Tax> getAllTaxes() throws PersistenceException {
        loadFile();
        List<String> keys = new ArrayList<>(allTaxes.keySet());
        Collections.sort(keys);
        List<Tax> result = new ArrayList<>();
        for (String key : keys) {
            result.add(allTaxes.get(key));
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
        Map<String, Tax> loaded = new HashMap<>();
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
                Tax current = unmarshallTax(line);
                String key = current.getStateAbr();
                if (loaded.containsKey(key)) {
                    throw new IllegalArgumentException("Duplicate tax entry.");
                }
                loaded.put(key, current);
            }
            if (scanner.ioException() != null) {
                throw new PersistenceException("Could not finish reading " + filename, scanner.ioException());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenceException("Invalid tax in " + filename
                    + " at line " + lineNumber + ": " + e.getMessage(), e);
        } finally {
            scanner.close(); // Close the file even when a row contains invalid data.
        }
        if (loaded.isEmpty()) {
            throw new PersistenceException("No taxes are available in " + filename);
        }
        allTaxes = loaded;
    }

    //unmarshalling means converting one text line into an object
    private Tax unmarshallTax(String line) {
        String[] tokens = line.split(delimiter);
        if (tokens.length != 3) {
            throw new IllegalArgumentException("A tax row must contain 3 fields.");
        }
        Tax tax = new Tax(tokens[1].trim());
        tax.setStateAbr(tokens[0].trim().toUpperCase());
        tax.setTaxRate(new BigDecimal(tokens[2].trim()));
        if (tax.getStateAbr().isEmpty() || tax.getState().isEmpty() || tax.getTaxRate().signum() < 0) {
            throw new IllegalArgumentException("Taxes need a state abbreviation, name and non-negative rate.");
        }
        return tax;
    }
}
