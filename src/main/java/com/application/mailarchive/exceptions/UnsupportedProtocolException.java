/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchive
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive.exceptions;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class UnsupportedProtocolException extends Exception {
    public UnsupportedProtocolException() {
        super("Protocol not supported");
    }
    
    public UnsupportedProtocolException(String protocol) {
        super("Protocol " + protocol + " not supported");
    }
}
