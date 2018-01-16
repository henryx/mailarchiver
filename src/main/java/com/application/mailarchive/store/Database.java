/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store;

import java.sql.SQLException;
import org.ini4j.Wini;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public abstract class Database implements AutoCloseable {

    protected Wini cfg;
    
    public Database(Wini cfg) {
        this.cfg = cfg;
    }
    
    public abstract void open() throws SQLException;
    
    @Override
    public abstract void close() throws SQLException;
}
