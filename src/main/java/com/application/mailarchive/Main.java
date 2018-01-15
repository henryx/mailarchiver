/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive;

import java.io.FileInputStream;
import java.io.IOException;

import com.application.mailarchive.exceptions.UnsupportedProtocolException;
import com.application.mailarchive.operations.Archive;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.ini4j.Wini;

import javax.mail.NoSuchProviderException;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class Main {

    /**
     * Open the configuration file
     *
     * @param cfgFile a configuration file
     */
    private Wini setCfg(String cfgFile) {
        Wini result;

        result = null;
        try {
            result = new Wini();
            result.load(new FileInputStream(cfgFile));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(2);
        }

        return result;
    }

    private ArgumentParser initargs() {
        ArgumentParser parser;

        parser = ArgumentParsers.newFor("Mailarchiver").build()
                .defaultHelp(true)
                .description("A mail archiver");

        parser.addArgument("-c", "--cfg")
                .metavar("<file>")
                .required(true)
                .help("Set the configuration file");

        return parser;
    }

    public void go(String[] argv) {
        Archive archive;
        Namespace args;
        Wini cfg;

        args = this.initargs().parseArgsOrFail(argv);
        cfg = this.setCfg(args.getString("cfg"));

        archive =new Archive();
        try {
            archive.execute(cfg);
        } catch (NoSuchProviderException | UnsupportedProtocolException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Main m;

        m = new Main();
        m.go(args);
    }
}
