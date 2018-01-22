/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store.database;

import com.application.mailarchive.store.Database;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.ini4j.Wini;

/**
 *
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
            "CREATE TABLE headers(account, folder, subject, msgid)",
            "CREATE VIRTUAL TABLE messages USING FTS5(msgid, body)"
        };

        try (Statement stmt = this.getConn().createStatement();) {
            for (String table : tables) {
                stmt.execute(table);
            }
        }
    }

    @Override
    public void open() throws SQLException {
        String dbpath;

        dbpath = this.getCfg().get("general", "database");

        this.setConn(DriverManager.getConnection("jdbc:sqlite:" + dbpath));
        if (!this.checkDB()) {
            this.initDB();
        }
    }

    @Override
    public void archive(String account, String folder, Message data) throws SQLException, MessagingException, IOException {
        String query;

        query = "INSERT INTO messages VALUES(?, ?, ?)";

        try (PreparedStatement pstmt = this.getConn().prepareStatement(query)) {
            pstmt.setString(1, account);
            pstmt.setString(2, folder);
            pstmt.setString(3, data.getContent().toString());

            pstmt.executeUpdate();
        }
    }
}
