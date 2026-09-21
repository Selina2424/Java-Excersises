package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.service.PersistenceException;
import com.sg.flooringmastery.dto.Tax;
import java.util.List;


public interface TaxDao {
    List<Tax> getAllTaxes() throws PersistenceException;

}
