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
package org.daveware.passwordmakerapp.cli;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.Database;
import org.daveware.passwordmaker.PasswordMaker;
import org.daveware.passwordmaker.RDFDatabaseReader;
import org.daveware.passwordmaker.SecureCharArray;
import org.daveware.passwordmaker.Utilities;
import org.daveware.passwordmakerapp.CmdLineSettings;

public class CliMain {

    CmdLineSettings config = null;
    
    public CliMain(CmdLineSettings c) {
        config = c;
    }

    /**
     * Copies the password to the clipboard and sleeps for numSeconds seconds. Then erases
     * the clipboard.
     * @param arr The password to copy.
     * @param numSeconds The number of seconds to wait. If this is 0 or less, nothing happens.
     */
    public void clipboardAndWait(SecureCharArray arr, int numSeconds) {
        
        if(numSeconds>0) {
            try {
                Utilities.copyToClipboard(arr);
                Thread.sleep(1000 * numSeconds);
            } catch(Exception e) {}
    
            Utilities.clearClipboard();
        }
    }

    /**
     * Figures out what to do based on what arguments have been supplied.
     */
    public int run() {
        int ret = 1;
        
        // Technically you shouldn't be able to get here without these being set, but in case
        // I change the way it works in the future an forget to update this, I'll just keep
        // the check.
        if(config.inputFilename!=null && config.matchUrl!=null) {
            ret = runUrlSearch();
        }
        else {
            System.err.println("Invalid arguments");
        }
        
        return ret;
    }


    /**
     * Searches the database for an account that matches the URL and generates
     * a password with input from the user.
     */
    public int runUrlSearch() {
        SecureCharArray secret = null;
        SecureCharArray output = null;
        char [] secretArray = null;
        int ret = 1;

        if(config.quiet==true && config.timeout<=0) {
            System.err.println("Quiet mode cannot be used with a clipboard (-c/--clipboard) value of 0");
            return 1;
        }
        
        try {
            RDFDatabaseReader rdfReader = new RDFDatabaseReader();
            PasswordMaker pwm = new PasswordMaker();
            Database db = rdfReader.read(new FileInputStream(new File(config.inputFilename)));
            Account acc = db.findAccountByUrl(config.matchUrl);
            Console console = System.console();

            if(acc!=null) {
                if(console!=null) {
                    console.format("Account: %1s\n", acc.getName());
                    console.format("Desc: %1s\n", acc.getDesc());
                    console.format("Login: %1s\n", acc.getUsername());
                    secretArray = console.readPassword("Enter Master Password: ");
                    if(secretArray!=null) {
                        secret = new SecureCharArray(secretArray);
                        output = pwm.makePassword(secret, acc);
                        
                        if(config.quiet==false) {
                            for(int iChar=0; iChar<output.getData().length; iChar++) {
                                System.out.print(output.getData()[iChar]);
                            }
                            System.out.println("");
                        }
                        
                        
                        int numSeconds = config.timeout < 0 ? 0 : config.timeout;

                        /* Ignore what is in the RDF when using the CLI.
                        try {
                            numSeconds = Integer.parseInt(db.getGlobalSetting(GlobalSettingKey.CLIPBOARD_TIMEOUT));
                        } catch(Exception e) {
                            numSeconds = 10;
                        }
                        */
                        clipboardAndWait(output, numSeconds);
                        ret = 0;
                    }
                }
            }
            else {
                System.err.println("Unable to locate account with URL " + config.matchUrl);
                ret = 1;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            ret = 1;
        }
        finally {
            // destroy any information hanging around
            if(secret!=null)
                secret.erase();
            if(output!=null)
                output.erase();
            if(secretArray!=null) {
                Arrays.fill(secretArray, (char)0x55);
                Arrays.fill(secretArray, (char)0xAA);
            }
        }
        
        return ret;
    }


}
