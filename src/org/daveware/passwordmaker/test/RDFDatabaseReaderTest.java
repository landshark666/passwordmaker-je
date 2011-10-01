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

import java.io.InputStream;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.daveware.passwordmaker.Database;
import org.daveware.passwordmaker.RDFDatabaseReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Runs tests against the RDFDatabaseReader class.
 * 
 * @author Dave Marotti
 */
public class RDFDatabaseReaderTest {
    
    public RDFDatabaseReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of read method, of class RDFDatabaseReader.
     */
    @Test
    public void testRead() throws Exception {
        RDFDatabaseReader reader = new RDFDatabaseReader();
        InputStream is = getClass().getResourceAsStream("sample.rdf");
        Database db = reader.read(is);
        db.printDatabase();
    }

    /**
     * Test of getExtension method, of class RDFDatabaseReader.
     */
    @Test
    public void testGetExtension() {
    }
}
