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
package org.daveware.passwordmaker;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Arrays;

public class Utilities {

    public static void copyToClipboard(SecureCharArray chars) {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        StringSelection contents = new StringSelection(new String(chars.getData()));
        cb.setContents(contents, contents);
    }
    
    public static void clearClipboard() {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection yuck = null;
        char [] tmpClear = new char[1024];
        
        // write some garbage to it first
        // TODO: is this really necessary?
        Arrays.fill(tmpClear, (char)0xAA);
        yuck = new StringSelection(new String(tmpClear));
        cb.setContents(yuck, yuck);
        Arrays.fill(tmpClear, (char)0x55);
        yuck = new StringSelection(new String(tmpClear));
        cb.setContents(yuck, yuck);
        yuck = new StringSelection(new String());
        
        // actually clear the clipboard
        try {
            cb.setContents(new Transferable() {
              public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[0];
              }

              public boolean isDataFlavorSupported(DataFlavor flavor) {
                return false;
              }

              public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                throw new UnsupportedFlavorException(flavor);
              }
            }, null);
          } catch (IllegalStateException e) {}    
    }
    
    public static boolean isMac() {
    	return System.getProperty("os.name").toLowerCase().indexOf("mac")>=0;
    }
    
    public static boolean isUnix() {
    	return System.getProperty("os.name").toLowerCase().indexOf("nix")>=0;
    }

    public static boolean isWindows() {
    	return System.getProperty("os.name").toLowerCase().indexOf("win")>=0;
    }
}
