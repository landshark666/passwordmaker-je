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

/**
 * Represents a setting key in the Firefox RDF file. Only a few of these are defined as we
 * reuse them to store settings for this program.  A default value is also available.
 * 
 * @author Dave Marotti
 */
public class GlobalSettingKey {
    public static GlobalSettingKey CLIPBOARD_TIMEOUT = new GlobalSettingKey("NS1:autoClearClipboardSeconds", "10");
    public static GlobalSettingKey SHOW_GEN_PW = new GlobalSettingKey("NS1:maskMasterPassword", "true");
    
    String key;
    String defaultValue;
    
    private GlobalSettingKey(String k, String def) {
        key = k;
        defaultValue = def;
    }
    
    public String toString() { return key; }
    public String getDefault() { return defaultValue; }
}
