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
 * Leet class used for converting SecureCharArray objects to leet-speak.
 * 
 * This was converted from leet.cpp of the Password Maker CLI version.
 * 
 * @author Dave Marotti
 */
public class LeetEncoder {
    static String [][] LEVELS = {
        {"4", "b", "c", "d", "3", "f", "g", "h", "i", "j", "k", "1",
	"m", "n", "0", "p", "9", "r", "s", "7", "u", "v", "w", "x",
	"y", "z"},
	{"4", "b", "c", "d", "3", "f", "g", "h", "1", "j", "k", "1",
	"m", "n", "0", "p", "9", "r", "5", "7", "u", "v", "w", "x",
	"y", "2"},
	{"4", "8", "c", "d", "3", "f", "6", "h", "'", "j", "k", "1",
	"m", "n", "0", "p", "9", "r", "5", "7", "u", "v", "w", "x",
	"'/", "2"},
	{"@", "8", "c", "d", "3", "f", "6", "h", "'", "j", "k", "1",
	"m", "n", "0", "p", "9", "r", "5", "7", "u", "v", "w", "x",
	"'/", "2"},
	{"@", "|3", "c", "d", "3", "f", "6", "#", "!", "7", "|<", "1",
	"m", "n", "0", "|>", "9", "|2", "$", "7", "u", "\\/", "w",
	"x", "'/", "2"},
	{"@", "|3", "c", "|)", "&", "|=", "6", "#", "!", ",|", "|<",
	"1", "m", "n", "0", "|>", "9", "|2", "$", "7", "u", "\\/",
	"w", "x", "'/", "2"},
	{"@", "|3", "[", "|)", "&", "|=", "6", "#", "!", ",|", "|<",
	"1", "^^", "^/", "0", "|*", "9", "|2", "5", "7", "(_)", "\\/",
	"\\/\\/", "><", "'/", "2"},
	{"@", "8", "(", "|)", "&", "|=", "6", "|-|", "!", "_|", "|(",
	"1", "|\\/|", "|\\|", "()", "|>", "(,)", "|2", "$", "|", "|_|",
	"\\/", "\\^/", ")(", "'/", "\"/_"},
	{"@", "8", "(", "|)", "&", "|=", "6", "|-|", "!", "_|", "|{",
	"|_", "/\\/\\", "|\\|", "()", "|>", "(,)", "|2", "$", "|",
	"|_|", "\\/", "\\^/", ")(", "'/", "\"/_"} 
    };
    
    /**
     * Converts a SecureCharArray into a new SecureCharArray with any applicable
     * characters converted to leet-speak.
     * 
     * @param level What level of leet to use. Each leet corresponds to a different
     *              leet lookup table.
     * @param message The array to convert.
     * @return A new SecureCharArray object, converted from message.
     * @throws Exception upon sizing error.
     */
    public static void leetConvert(LeetLevel level, SecureCharArray message)
            throws Exception 
    {
        // pre-allocate an array that is 4 times the size of the message.  I don't
        // see anything in the leet-table that is larger than 3 characters, but I'm
        // using 4-characters to calcualte the size just in case. This is to avoid
        // a bunch of array resizes.
        SecureCharArray ret = new SecureCharArray(message.size() * 4);
        char [] messageBytes = message.getData();     // Reference to message's data
        char [] retBytes = ret.getData();             // Reference to ret's data
        int currentRetByte = 0;                       // Index of current ret byte

        if (level.compareTo(LeetLevel.LEVEL1) >= 0  && level.compareTo(LeetLevel.LEVEL9) <=0 ) {
            for(int i=0; i<messageBytes.length; i++) {
                char b = Character.toLowerCase(messageBytes[i]);
                if(b >= 'a' &&  b <= 'z') {
                    for(int j=0; j<LEVELS[level.getLevel()-1][b - 'a'].length(); j++)
                        retBytes[currentRetByte++] = LEVELS[level.getLevel()-1][b - 'a'].charAt(j);
                }
                else {
                    retBytes[currentRetByte++] = b;
                }
            }
        }

        // Resize the array to the length that we actually filled it, then replace
        // the original message and erase the buffer we built up.
        ret.resize(currentRetByte, true);
        message.replace(ret);
        ret.erase();
    }
}
