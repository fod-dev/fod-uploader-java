package com.fortify.fod;

import org.apache.commons.cli.*;

import java.util.Comparator;
import java.util.regex.Pattern;

public class FortifyParser {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ZIP_LOCATION = "ziplocation";
    public static final String BSI_URL = "bsiUrl";
    public static final String HELP = "help";
    public static final String VERSION = "version";

    private Options options = new Options();
    private CommandLineParser parser = new DefaultParser();
    private CommandLine cmd = null;

    /**
     * Argument paring wrapper for the Fod Uploader.
     */
    public FortifyParser() {
        // creates 2 arguments which aren't required
        Option help = new       Option(HELP, "print this message");
        Option version = new    Option(VERSION, "print the version information and exit");

        // Creates the username argument ( -u, --username <user> required=true username/api key )
        Option username = Option.builder("u")
                .hasArg(true)
                .required(true)
                .longOpt(USERNAME)
                .argName("user")
                .desc("username/api key")
                .build();

        // Creates the username argument ( -p, --password <pass> required=true password/api secret )
        Option password = Option.builder("p")
                .hasArg(true)
                .required(true)
                .longOpt(PASSWORD)
                .argName("pass")
                .desc("password/api secret")
                .build();

        // Creates the username argument ( -url, --bsiUrl <url> required=true build server url )
        Option bsiUrl = Option.builder("b")
                .hasArg(true)
                .required(true)
                .longOpt(BSI_URL)
                .argName("url")
                .desc("build server url")
                .build();

        // Creates the username argument ( -loc, --zipLocation <file> required=true location of scan )
        Option zipLocation = Option.builder("z")
                .hasArg(true)
                .required(true)
                .longOpt(ZIP_LOCATION)
                .argName("file")
                .desc("location of scan")
                .build();

        // Add the options to the options list
        options.addOption(help);
        options.addOption(version);
        options.addOption(username);
        options.addOption(password);
        options.addOption(bsiUrl);
        options.addOption(zipLocation);
    }

    /**
     * Gets the various arguments and handles them accordingly.
     * @param args arguments to parse
     * @throws ParseException
     * @throws Exception
     */
    public void parse(String[] args) {
        try {
            cmd = parser.parse(options, args);

            if(cmd.hasOption(BSI_URL)) {
                BsiUrl url = new BsiUrl(cmd.getOptionValue(BSI_URL));
            }

            if (cmd.hasOption(USERNAME) && cmd.hasOption(PASSWORD) && cmd.hasOption(ZIP_LOCATION) && cmd.hasOption(BSI_URL)) {

            }
        // Throws if username, password, zip location and bsi url aren't all present.
        } catch (ParseException e) {
            // If the user types just -help or just -version, then it will handle that command.
            // Regex is used here since cmd isn't accessible.
            Pattern p = Pattern.compile("(-{0,2})" + HELP + "|" + VERSION);
            if (p.matcher(args[0]).matches()) {
                help();
            } else if (p.matcher(args[0]).matches()) {
                System.out.println("upload version FodUploader 5.3.0");
            } else {
                // I can no longer hope to imagine the command you intended.
                System.err.println(e.getMessage());
                System.err.println("try \"-" + HELP + "\" for info");
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Displays help dialog.
     */
    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(HelpComparator);

        formatter.printHelp( "FodUpload-5.3.jar", options, true );
    }

    /**
     * Compares options so that they are ordered:
     * 1.) by required, then by
     * 2.) short operator.
     * Used for sorting the results of the Help command.
     */
    private static Comparator<Option> HelpComparator = new Comparator<Option>() {
        @Override
        public int compare(Option o1, Option o2) {
            String required1 = o1.isRequired() ? "1" : "0";
            String required2 = o2.isRequired() ? "1" : "0";

            int result = required2.compareTo(required1);
            if (result == 0) {
                result = o1.getOpt().compareToIgnoreCase(o2.getOpt());
            }
            return result;
        }
    };
}
