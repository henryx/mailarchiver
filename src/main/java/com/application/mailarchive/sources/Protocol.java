/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchive
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.sources;

import com.application.mailarchive.exceptions.UnsupportedProtocolException;
import com.sun.mail.util.MailSSLSocketFactory;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import java.util.Properties;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public abstract class Protocol implements AutoCloseable {

    private int port;
    private String host;
    private String user;
    private String password;
    private String protocol;

    private Store store;
    private Folder defaultFolder;

    public Protocol(String protocol) throws UnsupportedProtocolException, NoSuchProviderException, GeneralSecurityException {
        this.protocol = protocol;

        this.initStore();
    }

    public Protocol(String host, String user, String password, String protocol) throws UnsupportedProtocolException, NoSuchProviderException, GeneralSecurityException {
        this.host = host;
        this.user = user;
        this.password = password;
        this.protocol = protocol;

        this.initStore();
    }

    public Protocol(String host, int port, String user, String password, String protocol) throws UnsupportedProtocolException, NoSuchProviderException, GeneralSecurityException {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.protocol = protocol;

        this.initStore();
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the store
     */
    public Store getStore() {
        return this.store;
    }

    /**
     * @return the default folder
     */
    public Folder getDefaultFolder() {
        return this.defaultFolder;
    }

    /**
     * Check if protocol is supported by children class
     *
     * @param protocol
     * @param protocols
     * @throws UnsupportedProtocolException
     */
    protected void checkProtocol(String protocol, String[] protocols) throws UnsupportedProtocolException {
        for (String valid : protocols) {
            if (!protocol.contains(valid)) {
                throw new UnsupportedProtocolException(protocol);
            }
        }
    }

    /**
     * Initialize the store
     *
     * @throws NoSuchProviderException
     */
    private void initStore() throws NoSuchProviderException, GeneralSecurityException {
        Properties props = new Properties();

        if (this.protocol.equals("imaps")) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.put("mail." + this.protocol + ".ssl.trust", "*");
            props.put("mail." + this.protocol + ".ssl.socketFactory", sf);
        }

        Session session = Session.getDefaultInstance(props, null);

        this.store = session.getStore(this.protocol);
    }

    /**
     * Open the connection to the store
     *
     * @throws MessagingException
     */
    public void connect() throws MessagingException {
        this.getStore().connect(this.host, this.port, this.user, this.password);
        this.defaultFolder = this.getStore().getDefaultFolder();
    }

    /**
     * Close the connection to the store
     *
     * @throws MessagingException
     */
    @Override
    public void close() throws MessagingException {
        this.getStore().close();
    }

    /**
     * Get the folder tree starting from root
     *
     * @param root Root folder to extract tree
     * @return an ArrayList containing folder tree starting from root
     * @throws MessagingException
     */
    public abstract ArrayList<Folder> getFolderTree(Folder root) throws MessagingException;
}
