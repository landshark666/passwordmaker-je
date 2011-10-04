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
