package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.service.PersistenceException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class AuditDaoFileImpl implements AuditDao{
    public static final String audit_File = "audit.txt";
    private String auditFile;

    // App writes to audit.txt in the project folder
    public AuditDaoFileImpl() {
        this(audit_File);
    }

    //tests can choose a separate audit file
    public AuditDaoFileImpl(String auditFile) {
        this.auditFile = auditFile;
    }

    //go up one entry while keeping all earlier entries in the file
    @Override
    public void writeAuditEntry(String entry) throws PersistenceException {
        File file = new File(auditFile);
        File folder = file.getAbsoluteFile().getParentFile();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new PersistenceException("Could not create the audit folder.");
        }
        PrintWriter out;
        try {
            //true means append
            out = new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
            throw new PersistenceException("Could not write the audit log.", e);
        }
        LocalDateTime timestamp = LocalDateTime.now();
        out.println(timestamp + " : " + entry);
        out.close();
        if (out.checkError()) {
            throw new PersistenceException("An error occurred while writing the audit log.");
        }
    }
}
