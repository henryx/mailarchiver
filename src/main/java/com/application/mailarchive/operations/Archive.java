/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchive
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.operations;

import com.application.mailarchive.Main;
import com.application.mailarchive.exceptions.UnsupportedProtocolException;
import com.application.mailarchive.sources.IMAP;
import com.application.mailarchive.store.Database;
import com.application.mailarchive.store.Store;
import com.application.mailarchive.store.database.SQLiteDB;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import org.apache.log4j.Level;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class Archive {

    private final Wini cfg;

    public Archive(Wini cfg) {
        this.cfg = cfg;
    }

    private void archiveIMAP(Section section, Store db) throws UnsupportedProtocolException, NoSuchProviderException, MessagingException, NumberFormatException, GeneralSecurityException {

        try (IMAP proto = new IMAP(section.get("protocol"));) {

            proto.setUser(section.get("user"));
            proto.setPassword(section.get("password"));
            proto.setHost(section.get("host"));
            try {
                proto.setPort(Integer.parseInt(section.get("port")));
            } catch (NumberFormatException ex) {
                throw new NumberFormatException("Invalid port number for IMAP protocol: " + section.get(section, "port"));
            }

            proto.connect();
            for (Folder folder : proto.getFolderTree(proto.getDefaultFolder())) {
                Main.logger.debug("Message in folder " + folder.getFullName() + ": " + folder.getMessageCount());

                folder.open(Folder.READ_ONLY);
                try {
                    for (Message message : folder.getMessages()) {
                        db.archive(section.getName(), folder.getFullName(), message);
                    }
                    db.commit();
                } catch (MessagingException | IOException ex) {
                    Main.logger.log(Level.ERROR, ex);
                }
                folder.close();
            }
        }
    }

    private void archive(Store db) throws UnsupportedProtocolException, MessagingException, NoSuchProviderException, NumberFormatException, GeneralSecurityException {
        for (String section : this.cfg.keySet()) {
            if (!(section.equals("general"))) {
                if (this.cfg.get(section, "protocol").startsWith("imap")) {
                    this.archiveIMAP(this.cfg.get(section), db);
                }
            }
        }
    }

    public void execute() throws NoSuchProviderException, UnsupportedProtocolException, MessagingException, NumberFormatException, GeneralSecurityException {
        try (Database db = new SQLiteDB(this.cfg);) {
            db.open();
            if (db.isOpened()) {
                this.archive(db);
            } else {
                Main.logger.fatal("Database connection is not opened");
            }
        }
    }
}
