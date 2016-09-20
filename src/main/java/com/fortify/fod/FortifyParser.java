package com.fortify.fod;

import org.apache.commons.cli.*;

/**
 * Created by petebeegle on 9/20/2016.
 */
public class FortifyParser {
    private Options options = new Options();
    private CommandLineParser parser = new DefaultParser();
    private CommandLine cmd = null;

    public FortifyParser() {
        Option help = new       Option("help", "print this message");
        Option version = new    Option("version", "print the version information and exit");

        options.addOption(help);
        options.addOption(version);
    }

    public void parse(String[] args) {
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                help();
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.out.println();
            help();
        }
    }

    public void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "FodUpload-5.3.jar", options, true );
    }

    public Options getOptions() {
        return options;
    }
}
