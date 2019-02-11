/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store.database;

import com.application.mailarchive.MailUtils;
import com.application.mailarchive.Main;
import com.application.mailarchive.store.Database;

import java.io.IOException;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.ini4j.Wini;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class SQLiteDB extends Database {

    public SQLiteDB(Wini cfg) {
        super(cfg);
    }

    private boolean checkDB() throws SQLException {
        Integer counted;
        ResultSet res;
        String query = "SELECT count(*) FROM sqlite_master";

        try (Statement stmt = this.getConn().createStatement();) {
            res = stmt.executeQuery(query);

            res.next();
            counted = res.getInt(1);
        }

        return counted > 0;
    }

    private void initDB() throws SQLException {
        String[] tables;

        tables = new String[]{
            "PRAGMA journal_mode=WAL",
            "CREATE TABLE headers(account, folder, received, fromaddr, toaddr, msgid)",
            "CREATE VIRTUAL TABLE messages USING FTS5(msgid, body)"
        };

        try (Statement stmt = this.getConn().createStatement();) {
            for (String table : tables) {
                stmt.execute(table);
            }
        }
    }

    private void archiveHeaders(String account, String folder, Message data) throws SQLException, MessagingException {
        String query, from, to, msgid;
        Date received;
        
        query = "INSERT INTO headers VALUES(?, ?, ?, ?, ?, ?)";
        from = MailUtils.getRecipient(data.getFrom());
        to = MailUtils.getRecipient(data.getRecipients(Message.RecipientType.TO));
        received = new Date(data.getReceivedDate().getTime());
        msgid = data.getHeader("Message-ID")[0];

        try (PreparedStatement pstmt = this.getConn().prepareStatement(query)) {
            pstmt.setString(1, account);
            pstmt.setString(2, folder);
            pstmt.setDate(3, received);
            pstmt.setString(4, from);
            pstmt.setString(5, to);
            pstmt.setString(6, msgid);

            pstmt.executeUpdate();
        }
    }

    private void archiveMessage(String msgid, String body) throws SQLException {
        String query;

        query = "INSERT INTO messages VALUES(?, ?)";
        try (PreparedStatement pstmt = this.getConn().prepareStatement(query)) {
            pstmt.setString(1, msgid);
            pstmt.setString(2, body);

            pstmt.executeUpdate();
        }
    }

    @Override
    public void open() {
        String dbpath;

        dbpath = this.getCfg().get("general", "database");

        try {
            this.setConn(DriverManager.getConnection("jdbc:sqlite:" + dbpath));
            if (!this.checkDB()) {
                this.initDB();
            }
            this.getConn().setAutoCommit(false);
        } catch (SQLException ex) {
            Main.logger.debug("Failed to open database: " + ex.getMessage());
        }
    }

    @Override
    public void archive(String account, String folder, Message data) throws MessagingException, IOException {
        try {
            if (!this.headerExists(account, folder, data.getHeader("Message-ID")[0])) {
                this.archiveHeaders(account, folder, data);
            }

            if (!this.messageExists(data.getHeader("Message-ID")[0])) {
                this.archiveMessage(data.getHeader("Message-ID")[0], MailUtils.getBodyPart(data));
            }
        } catch (SQLException ex) {
            Main.logger.debug("Failed to archive message: " + ex.getMessage());
        }
    }
}
