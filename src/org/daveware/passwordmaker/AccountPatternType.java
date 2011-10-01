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
 * Represents the type of pattern to use when auto-matching accounts.
 * @author Dave Marotti
 */
public class AccountPatternType implements Comparable<AccountPatternType> {
    
    private int type;
    
    public static AccountPatternType WILDCARD = new AccountPatternType(0);
    public static AccountPatternType REGEX = new AccountPatternType(1);
    
    private final static String NAMES[] = {
        "Wildcard", "Regular Expression"
    };
    
    private AccountPatternType() {
        type = 0;
    }
    
    private AccountPatternType(int i) {
        type = i;
    }
    
    public int compareTo(AccountPatternType o) {
        if(type < o.type)
            return -1;
        if(type > o.type)
            return 1;
        return 0;
    }
    
    public static AccountPatternType fromString(String str) 
            throws Exception
    {
        if(str.length()==0)
            return WILDCARD;
        if(str.equals("wildcard"))
            return WILDCARD;
        if(str.equals("regex"))
            return REGEX;
        
        throw new Exception(String.format("Invalid AccountPatternType '%1s'", str));
    }
    
    @Override
    public String toString() {
    	return NAMES[type];
    }
}
