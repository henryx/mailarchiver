/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchive
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive;

import javax.mail.Address;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class MailUtils {
    
    public static String getRecipient(Address[] address) {
        String recipient;
        recipient = "";
        
        for (Address addr : address) {
            recipient = addr.toString() + ", ";
        }
        recipient = recipient.substring(0, (recipient.length() - 2));
        
        return recipient;
    }
}
