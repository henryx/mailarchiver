/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive;

import com.application.mailarchive.exceptions.UnsupportedProtocolException;
import com.application.mailarchive.operations.Archive;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.*;
import org.ini4j.Wini;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class Main {

    public static Logger logger = org.apache.log4j.Logger.getLogger("Mail Archiver");

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

    /**
     * Create log via log4j
     */
    private void setLog(String file, String level) {
        Appender appender;

        try {
            if (file == null || file.equals("")) {
                appender = new ConsoleAppender(new PatternLayout("%d %-5p %c - %m%n"));
            } else {
                appender = new FileAppender(new PatternLayout("%d %-5p %c - %m%n"), file);
            }

            Main.logger.addAppender(appender);
            Main.logger.setLevel(Level.toLevel(level));
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.FATAL, null, ex);
            System.exit(2);
        }
    }

    private ArgumentParser initargs() {
        ArgumentParser parser;

        String defaultcfg = System.getProperty("user.home") + File.separator + "mailarchive.cfg";

        parser = ArgumentParsers.newFor("Mailarchiver").build()
                .defaultHelp(true)
                .description("A mail archiver");

        parser.addArgument("-c", "--cfg")
                .metavar("<file>")
                .required(true)
                .setDefault(defaultcfg)
                .help("Set the configuration file");

        return parser;
    }

    public void go(String[] argv) {
        Archive archive;
        Namespace args;
        Wini cfg;
        int status;

        status = 0;
        args = this.initargs().parseArgsOrFail(argv);
        cfg = this.setCfg(args.getString("cfg"));
        this.setLog(cfg.get("general", "logfile"),
                cfg.get("general", "loglevel"));

        archive = new Archive(cfg);
        try {
            Main.logger.info("Started mail archive process");

            archive.execute();

            Main.logger.info("Ended mail archive process");
        } catch (GeneralSecurityException | UnsupportedProtocolException | NumberFormatException | MessagingException ex) {
            Main.logger.fatal(ex.getMessage());
            status = 1;
        }

        System.exit(status);
    }

    public static void main(String[] args) {
        Main m;

        m = new Main();
        m.go(args);
    }
}
