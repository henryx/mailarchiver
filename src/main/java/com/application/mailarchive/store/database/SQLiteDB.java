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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
                "CREATE TABLE headers(account, folder, mail, received, fromaddr, toaddr, msgid, subj)",
                "CREATE VIRTUAL TABLE messages USING FTS5(account, folder, nail, msgid, body)"
        };

        try (Statement stmt = this.getConn().createStatement()) {
            for (String table : tables) {
                stmt.execute(table);
            }
        }
    }

    private void archiveHeaders(String account, String folder, String msgid, Message data) throws SQLException, MessagingException {
        Integer mail;
        String query, from, to, subject;
        Timestamp received;

        query = "INSERT INTO headers VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        mail = data.getMessageNumber();
        from = MailUtils.getRecipient(data.getFrom());
        to = MailUtils.getRecipient(data.getRecipients(Message.RecipientType.TO));
        received = new Timestamp(data.getReceivedDate().getTime());
        subject = data.getSubject();

        try (PreparedStatement pstmt = this.getConn().prepareStatement(query)) {
            pstmt.setString(1, account);
            pstmt.setString(2, folder);
            pstmt.setInt(3, mail);
            pstmt.setTimestamp(4, received);
            pstmt.setString(5, from);
            pstmt.setString(6, to);
            pstmt.setString(7, msgid);
            pstmt.setString(8, subject);

            pstmt.executeUpdate();
        }
    }

    private void archiveMessage(String account, String folder, Integer mail, String msgid, String body) throws SQLException {
        String query;

        query = "INSERT INTO messages VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = this.getConn().prepareStatement(query)) {
            pstmt.setString(1, account);
            pstmt.setString(2, folder);
            pstmt.setInt(3, mail);
            pstmt.setString(4, msgid);
            pstmt.setString(5, body);

            pstmt.executeUpdate();
        }
    }

    @Override
    public void open() {
        String dbpath;

        dbpath = this.getCfg().get("general", "database");

        try {
            this.setConn(DriverManager.getConnection(String.format("jdbc:sqlite:%s?journal_mode=WAL", dbpath)));
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
        String msgid;

        try {
            msgid = data.getHeader("Message-ID")[0];
        } catch (NullPointerException ex) {
            msgid = "";
        }

        try {
            if (!this.headerExists(account, folder, msgid)) {
                this.archiveHeaders(account, folder, msgid, data);
            }

            if (!this.messageExists(msgid)) {
                this.archiveMessage(account, folder, data.getMessageNumber(), msgid, MailUtils.getBodyPart(data));
            }
        } catch (SQLException ex) {
            Main.logger.debug("Failed to archive message: " + ex.getMessage());
        }
    }
}
