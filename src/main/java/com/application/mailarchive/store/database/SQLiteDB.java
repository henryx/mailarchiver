/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store.database;

import com.application.mailarchive.store.Database;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.ini4j.Wini;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class SQLiteDB extends Database {

    private Connection conn;

    public SQLiteDB(Wini cfg) {
        super(cfg);
    }

    private void initDB(Boolean init) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void open() throws SQLException {
        Boolean init;
        String dbpath;

        dbpath = this.cfg.get("general", "database");

        if (Files.exists(Paths.get(dbpath))) {
            init = Boolean.TRUE;
        } else {
            init = Boolean.FALSE;
        }

        this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbpath);
        this.initDB(init);
    }

    @Override
    public void close() throws SQLException {
        this.conn.close();
    }
}
