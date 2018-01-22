/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.ini4j.Wini;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public abstract class Database implements AutoCloseable {

    private Connection conn;
    private Wini cfg;

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

    /**
     * @return the cfg
     */
    public Wini getCfg() {
        return cfg;
    }

    public abstract void open() throws SQLException;

    @Override
    public void close() throws SQLException {
        if (!this.getConn().getAutoCommit()) {
            // FIXME: check if is needed a rollback instead of a commit
            this.getConn().commit();
        }
        
        this.getConn().close();
    }
    
    public abstract void archive(String account, String folder, Message data) throws SQLException, MessagingException, IOException;
}
