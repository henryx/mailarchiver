/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store;

import com.application.mailarchive.Main;

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
public abstract class Database implements Store {

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
     * @param cfg the cfg to set
     */
    @Override
    public void setCfg(Wini cfg) {
        this.cfg = cfg;
    }

    /**
     * @return the cfg
     */
    @Override
    public Wini getCfg() {
        return cfg;
    }

    @Override
    public abstract void open();

    @Override
    public boolean isOpened() {
        try {
            return this.conn instanceof Connection && !this.conn.isClosed();
        } catch (SQLException ex) {
            return false;
        }
    }

    @Override
    public boolean commit() {
        try {
            this.getConn().commit();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    @Override
    public void close() {
        try {
            if (!this.getConn().getAutoCommit()) {
                // FIXME: check if is needed a rollback instead of a commit
                this.getConn().commit();
            }

            this.getConn().close();
        } catch (SQLException ex) {
            Main.logger.debug("Failed to close the database : " + ex.getMessage());
        }
    }

    @Override
    public abstract void archive(String account, String folder, Message data) throws MessagingException, IOException;

    @Override
    public boolean headerExists(String account, String folder, Message data) {
        ResultSet res;
        String msgid, query;
        int count;

        query = "SELECT Count(*) FROM headers WHERE account = ? AND folder = ? AND msgid = ?";

        try {
            msgid = data.getHeader("Message-ID")[0];
        } catch (MessagingException | NullPointerException ex) {
            msgid = "";
        }

        try (PreparedStatement pstmt = this.getConn().prepareStatement(query)) {
            pstmt.setString(1, account);
            pstmt.setString(2, folder);
            pstmt.setString(3, msgid);

            res = pstmt.executeQuery();
            res.next();

            count = res.getInt(1);

            return count > 0;
        } catch (SQLException ex) {
            Main.logger.debug("Failed to check if header exist: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean messageExists(String msgid) {
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
        } catch (SQLException ex) {
            Main.logger.debug("Failed to check if message exist: " + ex.getMessage());
            return false;
        }
    }
}
