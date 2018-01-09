/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchive
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.sources;

import com.application.mailarchive.exceptions.UnsupportedProtocolException;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class IMAP extends Protocol {

    private String[] protocols = new String[]{"imap", "imaps"};

    public IMAP(String protocol) throws UnsupportedProtocolException, NoSuchProviderException {
        super(protocol);

        this.checkProtocol(protocol, this.protocols);
        if (protocol.contains("imap")) {
            this.setPort(143);
        } else {
            this.setPort(993);

        }
    }

    public IMAP(String host, String user, String password, String protocol) throws UnsupportedProtocolException, NoSuchProviderException {
        super(host, user, password, protocol);

        this.checkProtocol(protocol, this.protocols);
        if (protocol.contains("imap")) {
            this.setPort(143);
        } else {
            this.setPort(993);
        }
    }

    public IMAP(String host, int port, String user, String password, String protocol) throws UnsupportedProtocolException, NoSuchProviderException {
        super(host, port, user, password, protocol);

        this.checkProtocol(protocol, this.protocols);
    }

    public Folder[] getFolders() throws MessagingException {
        Folder defaultFolder;

        defaultFolder = this.getStore().getDefaultFolder();
        return defaultFolder.list();
    }

    public Folder[] getFolders(String folder) throws MessagingException {
        Folder root;

        root = this.getStore().getFolder(folder);
        return root.list();
    }

    public Message fetchMessage(Folder folder, int id) {
        // TODO: implement method
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
}
