/*
 * PasswordMaker Java Edition - One Password To Rule Them All
 * Copyright (C) 2011 Dave Marotti
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.daveware.passwordmakerapp;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.daveware.passwordmakerapp.cli.CliMain;
import org.daveware.passwordmakerapp.gui.GuiMain;

/**
 * Main entry point for the program. This will parse the command-line options and launch either
 * the CLI or GUI version.
 * 
 * @author Dave Marotti
 */
public class Main {

    /**
     * PasswordMakerJE entry point.
     * 
     * @param args The argv of java.
     */
    public static void main(String [] args) {
        Security.addProvider(new BouncyCastleProvider());
        Main m = new Main();
        int ret = 0;
        
        try {
            ret = m.run(args);
        } catch(Exception e) {
            e.printStackTrace();
            ret = 1;
        }
        
        System.exit(ret);
    }

    private final String helpStr = 
            "Usage: %1s args\n" +
                    "\n" +
                    "Searching For Matching Account Via URL\n" +
                    "Usage: -f file -u url [-n -q -c numsecs]\n" +
                    "\t-f, --file=file             Specify the RDF or XML file to search\n" +
                    "\t-u, --url=url               Specify the URL text to use when searching\n" +
                    "\t-c, --clipboard=numsecs     Copy the resulting password to the clipboard,\n" +
                    "\t                            wait for numsecs seconds then clear the clipboard\n" +
                    "\t                            and exit\n" +
                    "\t-n, --nogui                 Use the console instead of the GUI\n" +
                    "\t-q, --quiet                 Quiet mode. Do not print the final password to the\n" +
                    "\t                            screen. This is only valid with -c.\n" +
                    "\n";

    private CmdLineSettings cmdLineSettings = null;

    /**
     * Main constructor.
     * 
     * Creates a config file and initializes it to needed defaults.
     */
    public Main() {
        cmdLineSettings = new CmdLineSettings();
        cmdLineSettings.progName = this.getClass().toString();
        
        // The config file is only loaded by the GUI for now
    }

    /**
     * Parses the command line and fills various variables with the options.
     * @param args The argv input from main.
     * @return 0 on success, else an error code and the program should exit.
     */
    private void parseCmdLine(String [] args) 
    	throws Exception {     
        LongOpt [] longopts = new LongOpt[] {
                new LongOpt("clipboard", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
                new LongOpt("help",      LongOpt.NO_ARGUMENT,       null, 'h'),
                new LongOpt("file",      LongOpt.REQUIRED_ARGUMENT, null, 'f'),
                new LongOpt("nogui",     LongOpt.NO_ARGUMENT,       null, 'n'),
                new LongOpt("quiet",     LongOpt.NO_ARGUMENT,       null, 'q'),
                new LongOpt("url",       LongOpt.REQUIRED_ARGUMENT, null, 'u'),
        };
        int c;
        Getopt g = new Getopt("pwmje", args, "-:hf:nc:u:qd:", longopts);
        g.setOpterr(false);
        
        while((c = g.getopt())!=-1) {
            switch(c) {
                case 'c': // Set the number of seconds to hold the password on the clipboard before erasing and exiting
                    // TODO: option 'c' is broken
                    try {
                        cmdLineSettings.timeout = Integer.parseInt(g.getOptarg());
                        if(cmdLineSettings.timeout < 0)
                            throw new Exception();
                    } catch(Exception e) {
                        throw new Exception("Invalid -c/--clipboard value, must be numeric value of 0 or greater: " + g.getOptarg());
                    }
                    break;
                    
                case 'h': // print help
                    showHelpAndExit(0);
                    break;

                case 'f': // set the input filename
                    cmdLineSettings.inputFilename = g.getOptarg();
                    break;
                    
                case 'n': // no gui
                    cmdLineSettings.nogui = true;
                    break;
                
                case 'q': // quiet mode
                    cmdLineSettings.quiet = true;
                    break;
                
                case 'u': // set the url to search with
                    cmdLineSettings.matchUrl = g.getOptarg();
                    break;
            }
        }
    }

    public int run(String [] args) throws Exception {
        int ret = 0;
        
        // Dumps exception upon error
        parseCmdLine(args);
        
        // Look for invalid configurations...
        if(cmdLineSettings.nogui==true) {
            if(cmdLineSettings.inputFilename==null || cmdLineSettings.matchUrl==null) {
                System.err.println("When using -n/--nogui, you must use -f and -u");
                return 1;
            }
            
            CliMain cli = new CliMain(cmdLineSettings);
            ret = cli.run();
        }
        else {
            GuiMain gui = new GuiMain(cmdLineSettings);
            ret = gui.run();
        }
        
        return ret;
    }

    /**
     * Displays the help string and exits.
     */
    private void showHelpAndExit(int exitCode) {
        System.err.format(helpStr, cmdLineSettings.progName);
        System.exit(exitCode);
    }
}
