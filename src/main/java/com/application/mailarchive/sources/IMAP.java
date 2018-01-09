/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchive
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.sources;

import com.application.mailarchive.exceptions.UnsupportedProtocolException;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class IMAP extends Protocol {
    private String[] protocols = new String[]{"imap", "imaps"};

    public IMAP(String protocol) throws UnsupportedProtocolException {
        super(protocol);

        this.checkProtocol(protocol, this.protocols);
        if (protocol.contains("imap")) {
            this.setPort(143);
        } else {
            this.setPort(993);

        }
    }

    public IMAP(String host, String user, String password, String protocol) throws UnsupportedProtocolException {
        super(host, user, password, protocol);

        this.checkProtocol(protocol, this.protocols);
        if (protocol.contains("imap")) {
            this.setPort(143);
        } else {
            this.setPort(993);
        }
    }

    public IMAP(String host, int port, String user, String password, String protocol) throws UnsupportedProtocolException {
        super(host, port, user, password, protocol);

        this.checkProtocol(protocol, this.protocols);
    }
}
