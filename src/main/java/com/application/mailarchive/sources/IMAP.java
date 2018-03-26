/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchive
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.sources;

import com.application.mailarchive.exceptions.UnsupportedProtocolException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class IMAP extends Protocol {

    private String[] protocols = new String[]{"imap", "imaps"};

    /**
     * Initialize the class using default IMAP or IMAPS port
     * @param protocol set the IMAP protocol used for the operations
     * @throws UnsupportedProtocolException
     * @throws NoSuchProviderException
     * @throws GeneralSecurityException
     */
    public IMAP(String protocol) throws UnsupportedProtocolException, NoSuchProviderException, GeneralSecurityException {
        super(protocol);

        this.checkProtocol(protocol, this.protocols);
        if (protocol.equals("imap")) {
            this.setPort(143);
        } else {
            this.setPort(993);
        }
    }

    /**
     * Initialize the class using default IMAP or IMAPS port
     *
     * @param host set the IMAP host
     * @param user set the IMAP user
     * @param password set the IMAP password
     * @param protocol set the IMAP protocol used for the operations
     * @throws UnsupportedProtocolException
     * @throws NoSuchProviderException
     * @throws GeneralSecurityException
     */
    public IMAP(String host, String user, String password, String protocol) throws UnsupportedProtocolException, NoSuchProviderException, GeneralSecurityException {
        super(host, user, password, protocol);

        this.checkProtocol(protocol, this.protocols);
        if (protocol.contains("imap")) {
            this.setPort(143);
        } else {
            this.setPort(993);
        }
    }

    /**
     * Initialize the class
     *
     * @param host set the IMAP host
     * @param port set tge IMAP port
     * @param user set the IMAP user
     * @param password set the IMAP password
     * @param protocol set the IMAP protocol used for the operations
     * @throws UnsupportedProtocolException
     * @throws NoSuchProviderException
     * @throws GeneralSecurityException
     */
    public IMAP(String host, int port, String user, String password, String protocol) throws UnsupportedProtocolException, NoSuchProviderException, GeneralSecurityException {
        super(host, port, user, password, protocol);

        this.checkProtocol(protocol, this.protocols);
    }

    @Override
    public ArrayList<Folder> getFolderTree(Folder root) throws MessagingException {
        ArrayList<Folder> result;

        result = new ArrayList<>();
        for (Folder folder : root.list()) {

            result.add(folder);

            result.addAll(this.getFolderTree(folder));
        }

        return result;
    }

    public Message fetchMessage(Folder folder, int id) {
        // TODO: implement method
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
}
