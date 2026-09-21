package com.sg.flooringmastery.dao;
import com.sg.flooringmastery.service.PersistenceException;
public interface AuditDao {
    void writeAuditEntry(String entry) throws PersistenceException;
}
