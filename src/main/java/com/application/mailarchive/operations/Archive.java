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
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class Archive {

    private void archiveIMAP(Section section) throws UnsupportedProtocolException, NoSuchProviderException, MessagingException, NumberFormatException {

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
                // TODO: extract messages from folder and archive
            }
        }
    }

    public void execute(Wini cfg) throws NoSuchProviderException, UnsupportedProtocolException, MessagingException, NumberFormatException {

        for (String section : cfg.keySet()) {
            if (!(section.equals("general") || section.equals("logging"))) {
                if (cfg.get(section, "protocol").startsWith("imap")) {
                    this.archiveIMAP(cfg.get(section));
                }
            }
        }
    }
}
