/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchive
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.operations;

import com.application.mailarchive.exceptions.UnsupportedProtocolException;
import com.application.mailarchive.sources.IMAP;
import com.application.mailarchive.sources.Protocol;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class Archive {

    private void startIMAP(Section section) throws UnsupportedProtocolException, NoSuchProviderException, MessagingException {
        try (Protocol proto = new IMAP(section.get("protocol"));) {

            proto.setUser(section.get("user"));
            proto.setPassword(section.get("password"));
            proto.setHost(section.get("host"));
            proto.setPort(Integer.parseInt(section.get(section, "port")));

            proto.connect();
            // TODO: fetch and save messages
        }
    }

    public void execute(Wini cfg) throws NoSuchProviderException, UnsupportedProtocolException, MessagingException {

        for (String section : cfg.keySet()) {
            if (!(section.equals("general") || section.equals("logging"))) {
                if (cfg.get(section, "protocol").startsWith("imap")) {
                    this.startIMAP(cfg.get(section));
                }
            }
        }
    }
}
