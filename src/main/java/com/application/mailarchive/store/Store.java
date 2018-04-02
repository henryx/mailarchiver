/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.store;

import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.ini4j.Wini;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public interface Store extends AutoCloseable {

    public void setCfg(Wini cfg);

    public Wini getCfg();

    public void open();

    public boolean isOpened();

    public void archive(String account, String folder, Message data) throws MessagingException, IOException;

    public boolean headerExists(String account, String folder, String msgid);

    public boolean messageExists(String msgid);
}
