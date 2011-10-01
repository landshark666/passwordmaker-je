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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Represents the configuration of this program.
 * 
 * I know it's bad, but I hate config classes that are full of getters and setters. They
 * are a pain in the ass to work with as well as maintain.  I could make this class have
 * all the logic to validate the parameters at which point there would be a reason to
 * have those getters/setters but I'm not doing that right now. If I do in the future, maybe
 * I'll convert it all.
 * 
 * @author Dave Marotti
 */
public class Config {
    public String progName = null;
    public String inputFilename = null;
    public String matchUrl = null;
    public boolean quiet = false;
    public boolean nogui = false;
    
    private ArrayList<String> filenames = new ArrayList<String>();
    
    private final static int MAX_FILENAMES = 10;
    private final static int MIN_CLOSE_SECONDS = 1;
    private final static int MAX_CLOSE_SECONDS = 300;
    
    private Properties properties = new Properties();
    private final static String PWM_DIRNAME = ".pwmje";
    private final static String PROP_FILE = "pwmje.properties";    
    private final static String PROP_FILENAME = "fn";
    private final static String PROP_CLIPBOARD_TIMEOUT = "cbTimeout";
    
    private File settingsDir = null;
    
    public Config() {
        loadDefaults();
    }
    
    /**
     * This function must be called before the Config object can be used. It establishes the
     * settings directory based off the home directory. The home dir can be anywhere but it
     * must be an existing writable directory.
     * @param homedir The path to use as the user's home directory.
     * @throws Exception Upon failure to create PWMJE's settings directory.
     */
    public void setHomeDir(String homedir)
    	throws Exception {
    	// get rid of any BS input
    	if(homedir==null || homedir.trim().length()==0)
    		throw new Exception("Invalid home directory detected, cannot continue.");
    	
    	
    	File homeDir = new File(homedir);
    	if(homeDir.exists()==false || homeDir.isDirectory()==false)
    		throw new Exception("Home directory '" + homeDir.getAbsolutePath() + "' must be a writable directory.");
    	
    	// TODO: should I attempt to make the home directory? Seems kind of dangerous
    	
    	try {
	    	settingsDir = new File(homeDir, PWM_DIRNAME);
	    	if(settingsDir.exists()==true && settingsDir.isDirectory()==false)
	    		throw new Exception("Settings directory '" + settingsDir.getAbsolutePath() + "' must be a writable directory.");
	    	
	    	if(settingsDir.exists()==false && settingsDir.mkdir()==false)
	    		throw new Exception("Unable to create directory " + settingsDir.getAbsolutePath() + "'.");
    	} catch(Exception e) {
    		// Make sure settingsDir is reset to null before the exception escapes this scope
    		settingsDir = null;
    		throw e;
    	}
    }
    
    /**
     * Loads saved properties from file if it exists.
     * @throws Exception on I/O errors.
     */
    public void load()
        throws Exception {
        loadDefaults();
        
        // No home directory set, can't load anything
        if(settingsDir==null)
        	return;
        
        // No prior config file exists, don't load anything
        File homeFile = new File(settingsDir, PROP_FILE);
        if(homeFile.exists()==false)
        	return;
        
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(homeFile);
            properties.load(fin);
        }
        finally {
            if(fin!=null)
                fin.close();
        }
    }
    
    /**
     * Saves the properties to file.
     * @throws Exception
     */
    public void save()
            throws Exception {
        File outputFile = new File(settingsDir, PROP_FILE);
        FileOutputStream fout = null;
        
        try {
            fout = new FileOutputStream(outputFile);
            properties.store(fout, "Configuration file for PasswordMakerJE");
        }
        finally {
            if(fout!=null)
                fout.close();
        }
    }
    
    public void loadDefaults() {
        setClipboardTimeout(5);
    }
    
    private int getDefaultInt(String property, String defaultStr, int defaultInt) {
        try {
            return Integer.parseInt(properties.getProperty(property, defaultStr));
        } 
        catch(Exception e) {
            return defaultInt;
        }
    }
    
    public boolean setClipboardTimeout(int val) {
    	if(val >= MIN_CLOSE_SECONDS && val <= MAX_CLOSE_SECONDS) {
    		properties.setProperty(PROP_CLIPBOARD_TIMEOUT, Integer.toString(val));
    		return true;
    	}
    	
    	return false;
    }
    
    public int getClipboardTimeout() {
    	int val = getDefaultInt(PROP_CLIPBOARD_TIMEOUT, "5", 5);
    	
    	if(val >= MIN_CLOSE_SECONDS && val <= MAX_CLOSE_SECONDS)
    		return val;
    	
    	properties.setProperty(PROP_CLIPBOARD_TIMEOUT, "5");
    	return 5;
    }
    
    public List<String> getFilenames() {
    	return filenames;
    }
    
    public void removeFilenameAt(int index) {
    	if(index>=0 && index<filenames.size())
    		filenames.remove(index);
    }
    
    public void removeFilename(String name) {
    	filenames.remove(name);
    }
    
    public void addFilename(String name) {
    	filenames.add(0, name);
    }
    
    /**
     * Returns the current settings directory.  If this is set, then it is valid.  If it is
     * null then nothing has been set.
     * @return
     */
    public File getSettingsDir() {
    	return settingsDir;
    }
}
