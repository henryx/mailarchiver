/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchive
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive;

import java.io.IOException;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class MailUtils {

    public static String getRecipient(Address[] address) {
        String recipient;
        recipient = "";
        try {
            for (Address addr : address) {
                recipient = addr.toString() + ", ";
            }
            recipient = recipient.substring(0, (recipient.length() - 2));
        } catch (NullPointerException ex) {
        }

        return recipient;
    }

    public static String getBodyPart(Part p) throws MessagingException, IOException {
        // NOTE: This code is not mine, please refer https://javaee.github.io/javamail/FAQ#readattach

        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = MailUtils.getBodyPart(bp);
                    }
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = MailUtils.getBodyPart(bp);
                    if (s != null) {
                        return s;
                    }
                } else {
                    return MailUtils.getBodyPart(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = MailUtils.getBodyPart(mp.getBodyPart(i));
                if (s != null) {
                    return s;
                }
            }
        }
        return null;
    }
}
