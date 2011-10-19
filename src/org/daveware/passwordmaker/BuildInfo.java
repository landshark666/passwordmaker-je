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

import java.io.InputStream;
import java.util.Properties;

/**
 * Class to load and store the build information from build-info.properties.
 * 
 * @author Dave Marotti
 */
public class BuildInfo {
    String version = "Internal";
    String buildDate = "Internal";
    String buildTime = "Internal";

    public BuildInfo() {
        InputStream in = null;
        try {
            Properties prop = new Properties();
            in = getClass().getResourceAsStream("/build-info.properties");
            prop.load(in);
            version = prop.getProperty("Implementation-Version");
            buildDate = prop.getProperty("Built-On");
            buildTime = prop.getProperty("Built-At");
        } catch(Exception ee) {}
        finally {
            if(in!=null) {
                try { in.close(); } catch(Exception eee) {}
            }
        }
    }
    
    public String getVersion() { return version; }
    public String getBuildDate() { return buildDate; }
    public String getBuildTime() { return buildTime; }
}
