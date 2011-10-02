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
    public static LeetType NONE     = new LeetType(0, "None", "off");
    public static LeetType BEFORE   = new LeetType(1, "Before", "before-hashing");
    public static LeetType AFTER    = new LeetType(2, "After", "after-hashing");
    public static LeetType BOTH     = new LeetType(3, "Both", "both");
    
    public static LeetType [] TYPES = { NONE, BEFORE, AFTER, BOTH };  
    
    int type = 0;
    String name = "";
    String rdfName = "";
    
    private LeetType()
    {
    }
    
    private LeetType(int t, String n, String rdfN)
    {
        type = t;
        name = n;
        rdfName = rdfN;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
    
    public String toRdfString() {
        return rdfName;
    }

    public int compareTo(LeetType o) {
        if(type < o.type)
            return -1;
        if(type > o.type)
            return 1;
        return 0;
    }
    
    public static LeetType fromRdfString(String str) 
    {
        for(LeetType type : TYPES) {
            if(str.compareTo(type.rdfName)==0)
                return type;
        }
        return NONE;
    }
}
