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
package org.daveware.passwordmaker.test;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.daveware.passwordmaker.LeetEncoder;
import org.daveware.passwordmaker.LeetLevel;
import org.daveware.passwordmaker.SecureCharArray;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Runs test against the Leet-classes.
 * 
 * @author Dave Marotti
 */
public class LeetTest {
    
    public static class LeetData
    {
        public SecureCharArray orig;
        public SecureCharArray conv;
        
        public LeetData(SecureCharArray sOrig, SecureCharArray sConv)
        {
            orig = sOrig;
            conv = sConv;
        }
    };
    
    static String[][][] LEET_STRINGS = {
        // Level 1
        {{"abcdefghijklmnopqrstuvwxyz", "4bcd3fghijk1mn0p9rs7uvwxyz"},
            {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "4bcd3fghijk1mn0p9rs7uvwxyz"},
            {"0123456789", "0123456789"},
            {"!@#$%^&*()", "!@#$%^&*()"},
            {"'\";:,./<>?`~\\|", "'\";:,./<>?`~\\|"},},
        // Level 2
        {{"abcdefghijklmnopqrstuvwxyz", "4bcd3fgh1jk1mn0p9r57uvwxy2"},
            {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "4bcd3fgh1jk1mn0p9r57uvwxy2"},
            {"0123456789", "0123456789"},
            {"!@#$%^&*()", "!@#$%^&*()"},
            {"'\";:,./<>?`~\\|", "'\";:,./<>?`~\\|"},},
        // Level 3
        {{"abcdefghijklmnopqrstuvwxyz", "48cd3f6h'jk1mn0p9r57uvwx'/2"},
            {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "48cd3f6h'jk1mn0p9r57uvwx'/2"},
            {"0123456789", "0123456789"},
            {"!@#$%^&*()", "!@#$%^&*()"},
            {"'\";:,./<>?`~\\|", "'\";:,./<>?`~\\|"},},
        // Level 4
        {{"abcdefghijklmnopqrstuvwxyz", "@8cd3f6h'jk1mn0p9r57uvwx'/2"},
            {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "@8cd3f6h'jk1mn0p9r57uvwx'/2"},
            {"0123456789", "0123456789"},
            {"!@#$%^&*()", "!@#$%^&*()"},
            {"'\";:,./<>?`~\\|", "'\";:,./<>?`~\\|"},},
        // Level 5
        {{"abcdefghijklmnopqrstuvwxyz", "@|3cd3f6#!7|<1mn0|>9|2$7u\\/wx'/2"},
            {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "@|3cd3f6#!7|<1mn0|>9|2$7u\\/wx'/2"},
            {"0123456789", "0123456789"},
            {"!@#$%^&*()", "!@#$%^&*()"},
            {"'\";:,./<>?`~\\|", "'\";:,./<>?`~\\|"},},
        // Level 6
        {{"abcdefghijklmnopqrstuvwxyz", "@|3c|)&|=6#!,||<1mn0|>9|2$7u\\/wx'/2"},
            {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "@|3c|)&|=6#!,||<1mn0|>9|2$7u\\/wx'/2"},
            {"0123456789", "0123456789"},
            {"!@#$%^&*()", "!@#$%^&*()"},
            {"'\";:,./<>?`~\\|", "'\";:,./<>?`~\\|"},},
        // Level 7
        {{"abcdefghijklmnopqrstuvwxyz", "@|3[|)&|=6#!,||<1^^^/0|*9|257(_)\\/\\/\\/><'/2"},
            {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "@|3[|)&|=6#!,||<1^^^/0|*9|257(_)\\/\\/\\/><'/2"},
            {"0123456789", "0123456789"},
            {"!@#$%^&*()", "!@#$%^&*()"},
            {"'\";:,./<>?`~\\|", "'\";:,./<>?`~\\|"},},
        // Level 8
        {{"abcdefghijklmnopqrstuvwxyz", "@8(|)&|=6|-|!_||(1|\\/||\\|()|>(,)|2$||_|\\/\\^/)('/\"/_"},
            {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "@8(|)&|=6|-|!_||(1|\\/||\\|()|>(,)|2$||_|\\/\\^/)('/\"/_"},
            {"0123456789", "0123456789"},
            {"!@#$%^&*()", "!@#$%^&*()"},
            {"'\";:,./<>?`~\\|", "'\";:,./<>?`~\\|"},},
        // Level 9
        {{"abcdefghijklmnopqrstuvwxyz", "@8(|)&|=6|-|!_||{|_/\\/\\|\\|()|>(,)|2$||_|\\/\\^/)('/\"/_"},
            {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "@8(|)&|=6|-|!_||{|_/\\/\\|\\|()|>(,)|2$||_|\\/\\^/)('/\"/_"},
            {"0123456789", "0123456789"},
            {"!@#$%^&*()", "!@#$%^&*()"},
            {"'\";:,./<>?`~\\|", "'\";:,./<>?`~\\|"},},};
    
    static LeetData [][] DATA;
    
    public LeetTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // build up the secure char arrays from the string table
        DATA = new LeetData[LEET_STRINGS.length][];
        
        for(int level = 0; level<LEET_STRINGS.length; level++)
        {
            DATA[level] = new LeetData[LEET_STRINGS[level].length];
            for(int test = 0; test < LEET_STRINGS[level].length; test++)
            {
                SecureCharArray orig = new SecureCharArray(LEET_STRINGS[level][test][0]);
                SecureCharArray conv = new SecureCharArray(LEET_STRINGS[level][test][1]);

                DATA[level][test] = new LeetData(orig, conv);
            }
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of leetConvert method, of class Leet.
     */
    @Test
    public void testLeetConvert() throws Exception {
        System.out.println("Testing Leet.leetConvert");
        for(LeetLevel level : LeetLevel.getLevels()) {
            int iLevel = level.getLevel() - 1;
            for(int test=0; test<DATA[iLevel].length; test++)
            {
                LeetEncoder.leetConvert(level, DATA[iLevel][test].orig);
                if(Arrays.equals(DATA[iLevel][test].conv.getData(), DATA[iLevel][test].orig.getData())==false) {
                    System.out.println("Leet[" + iLevel + "][" + test + "]");
                    System.out.println("Expected: " + new String(DATA[iLevel][test].orig.getData()));
                    System.out.println("Received: " + new String(DATA[iLevel][test].conv.getData()));
                    fail("LeetLevel[" + iLevel + "] test " + test + " failed");
                }
            }
        }
    }
}
