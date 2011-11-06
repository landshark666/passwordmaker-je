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


/**
 * Represents the commandline settings passed into this program.
 * 
 * I know it's bad, but I hate storage classes that are full of getters and setters. They
 * are a pain in the ass to work with as well as maintain.  I could make this class have
 * all the logic to validate the parameters at which point there would be a reason to
 * have those getters/setters but I'm not doing that right now. If I do in the future, maybe
 * I'll convert it all.
 * 
 * @author Dave Marotti
 */
public class CmdLineSettings {
    public String progName = null;
    public String inputFilename = null;
    public String matchUrl = null;
    public boolean quiet = false;
    public boolean nogui = false;
    public int timeout = -1;
    
    public CmdLineSettings() {
    }
    
}
