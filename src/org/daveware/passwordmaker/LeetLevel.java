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
 * Object representing a level of leet encoding.
 *
 * @author Dave Marotti
 */
public class LeetLevel implements Comparable<LeetLevel> {
    public static final LeetLevel LEVEL1 = new LeetLevel(1);
    public static final LeetLevel LEVEL2 = new LeetLevel(2);
    public static final LeetLevel LEVEL3 = new LeetLevel(3);
    public static final LeetLevel LEVEL4 = new LeetLevel(4);
    public static final LeetLevel LEVEL5 = new LeetLevel(5);
    public static final LeetLevel LEVEL6 = new LeetLevel(6);
    public static final LeetLevel LEVEL7 = new LeetLevel(7);
    public static final LeetLevel LEVEL8 = new LeetLevel(8);
    public static final LeetLevel LEVEL9 = new LeetLevel(9);
    
    private static final LeetLevel [] LEVELS = {
        LEVEL1, LEVEL2, LEVEL3, LEVEL4, LEVEL5, LEVEL6, LEVEL7, LEVEL8, LEVEL9
    };
    
    private int level = 1;
    
    private LeetLevel() {
    }
    
    private LeetLevel(int l) {
        level = l;
    }
    
    @Override
    public String toString() {
        return Integer.toString(level);
    }
    
    public int getLevel() {
        return level;
    }
    
    public static LeetLevel [] getLevels() {
        return LEVELS;
    }
    
    public static LeetLevel fromInt(int i) {
        if(i>=LEVEL1.getLevel() && i<=LEVEL9.getLevel())
            return LEVELS[i-1];
        return LEVELS[0];
    }

    public int compareTo(LeetLevel o) {
        if(level < o.level)
            return -1;
        if(level > o.level)
            return 1;
        return 0;
    }
    
    /**
     * Converts a string to a leet level.
     * @param str The string to parse the leet level from.
     * @return The leet level if valid.
     * @throws Exception upon invalid level.
     */
    public static LeetLevel fromString(String str) 
        throws Exception 
    {
        if(str.length()==0)
            return LEVEL1;
        
        try {
            int i = Integer.parseInt(str);
            if(i>=1 && i<=LEVELS.length)
                return LEVELS[i-1];
        } catch(Exception e) {
        }

        String exceptionStr = String.format("Invalid LeetLevel '%1s', valid values are '1' to '9'", str);
        throw new Exception(exceptionStr);
    }
}
