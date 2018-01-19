/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store.database;

import com.application.mailarchive.store.Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    private void initDB() {
        String[] tables;
        
        tables = new String[]{
            // TODO: add SQL commands for create table
        };
        
        for (String table : tables) {
            // TODO: create tables
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
}
