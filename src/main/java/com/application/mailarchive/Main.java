/*
 Copyright (C) 2018 Enrico Bianchi (enrico.bianchi@gmail.com)
 Project       mailarchiver
 Description   A mail archiver
 License       GPL version 2 (see LICENSE for details)
 */
package com.application.mailarchive;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 *
 * @author Enrico Bianchi <enrico.bianchi@gmail.com>
 */
public class Main {
    
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
        Namespace args;
        
        args = this.initargs().parseArgsOrFail(argv);
    }
    
    public static void main(String[] args) {
        Main m;
        
        m = new Main();
        m.go(args);
    }
}
