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
 * Object representing type of leet encoding.
 * 
 * @author Dave Marotti
 */
public class LeetType implements Comparable<LeetType> {
    public static LeetType NONE     = new LeetType(0);
    public static LeetType BEFORE   = new LeetType(1);
    public static LeetType AFTER    = new LeetType(2);
    public static LeetType BOTH     = new LeetType(3);
    
    private final static String [] NAMES = {
        "None", "Before", "After", "Before and After"
    };
    
    int type = 0;
    
    private LeetType()
    {
    }
    
    private LeetType(int t)
    {
        type = t;
    }
    
    @Override
    public String toString()
    {
        if(type>=NONE.type && type<=BOTH.type)
            return NAMES[type];
        return "Unknown";
    }

    public int compareTo(LeetType o) {
        if(type < o.type)
            return -1;
        if(type > o.type)
            return 1;
        return 0;
    }
    
    public static LeetType fromString(String str) 
            throws Exception
    {
        if(str.length()==0)
            return NONE;
        if(str.equals("off"))
            return NONE;
        if(str.equals("before-hashing"))
            return BEFORE;
        if(str.equals("after-hashing"))
            return AFTER;
        if(str.equals("both"))
            return BOTH;
        
        throw new Exception(String.format("Invalid LeetType '%1s'", str));
    }
}
