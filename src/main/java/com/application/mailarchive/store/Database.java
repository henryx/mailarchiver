/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store;

import java.sql.Connection;
import java.sql.SQLException;
import org.ini4j.Wini;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public abstract class Database implements AutoCloseable {

    protected Wini cfg;
    private Connection conn;

    public Database(Wini cfg) {
        this.cfg = cfg;
    }

    /**
     * @return the conn
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * @param conn the conn to set
     */
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public abstract void open() throws SQLException;

    @Override
    public void close() throws SQLException {
        this.getConn().close();
    }
}
