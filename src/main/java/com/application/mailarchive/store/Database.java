/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.ini4j.Wini;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public abstract class Database implements AutoCloseable {

    private Connection conn;
    private final Wini cfg;

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

    public boolean headerExists(String account, String folder, String msgid) throws SQLException {
        ResultSet res;
        String query;
        int count;

        query = "SELECT Count(*) FROM headers WHERE account = ? AND folder = ? AND msgid = ?";
        try (PreparedStatement pstmt = this.getConn().prepareStatement(query)) {
            pstmt.setString(1, account);
            pstmt.setString(2, folder);
            pstmt.setString(3, msgid);

            res = pstmt.executeQuery();
            res.next();

            count = res.getInt(1);

            return count > 0;
        }
    }

    public boolean messageExists(String msgid) throws SQLException {
        ResultSet res;
        String query;
        int count;

        query = "SELECT Count(*) FROM messages WHERE msgid = ?";
        try (PreparedStatement pstmt = this.getConn().prepareStatement(query)) {
            pstmt.setString(1, msgid);

            res = pstmt.executeQuery();
            res.next();

            count = res.getInt(1);

            return count > 0;
        }
    }
}
