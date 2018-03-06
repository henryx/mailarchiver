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
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
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
    private void setLog(Wini cfg) {
        Appender appender;

        try {
            appender = new FileAppender(new PatternLayout("%d %-5p %c - %m%n"),
                    cfg.get("logging", "file"));
            Main.logger.addAppender(appender);
            Main.logger.setLevel(Level.toLevel(cfg.get("logging", "level")));
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.FATAL, null, ex);
            System.exit(2);
        }
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
        this.setLog(cfg);
        Main.logger.info("Started mail archive process");

        archive = new Archive();
        try {
            archive.execute(cfg);
        } catch ( UnsupportedProtocolException | NumberFormatException | MessagingException ex) {
            Main.logger.fatal(ex.getMessage());
        }

        Main.logger.info("Ended mail archive process");
    }

    public static void main(String[] args) {
        Main m;

        m = new Main();
        m.go(args);
    }
}
