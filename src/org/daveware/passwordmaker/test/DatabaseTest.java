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

import static org.junit.Assert.assertEquals;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.Database;
import org.daveware.passwordmaker.DatabaseListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Runs test against the Database class.
 * 
 * @author Dave Marotti
 */
public class DatabaseTest {
    
    public DatabaseTest() {
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

    int numAdd = 0;
    int numRemove = 0;
    int numChange = 0;
    int numDirty = 0;
    
    class MyDBListener implements DatabaseListener {
        @Override
        public void accountAdded(Account parent, Account account) {
            numAdd++;
        }

        @Override
        public void accountRemoved(Account parent, Account account) {
            numRemove++;
        }

        @Override
        public void accountChanged(Account account) {
            numChange++;
        }

        @Override
        public void dirtyStatusChanged(boolean status) {
            numDirty++;
        }
    }
    
    /**
     * Test of addAccount method, of class Database.
     */
    @Test
    public void testAddAccount() throws Exception {
        System.out.println("addAccount");
        Database instance = new Database();
        instance.addDatabaseListener(new MyDBListener());
        
        numChange = 0;
        
        Account account = new Account("name1", "http://url.org", "username1");
        instance.addAccount(instance.getRootAccount(), account);
        Account subAccount = new Account("name2", "http://url2.org", "username2");
        instance.addAccount(account, subAccount);
        Account account2 = new Account("name3", "http://url3.org", "username3");
        instance.addAccount(instance.getRootAccount(), account2);
        
        assertEquals(numAdd, 3);
        
        assert(instance.getRootAccount().getChild(0).getName().equals(account.getName()));
        assert(instance.getRootAccount().getChild(0).getChildren().get(0).getName().equals(subAccount.getName()));
        assert(instance.getRootAccount().getChild(1).getName().equals(account2.getName()));
        
        assertEquals(numDirty, 3);
    }

    /**
     * Test of changeAccount method, of class Database.
     */
    @Test
    public void testChangeAccount() throws Exception {
        System.out.println("changeAccount");
        Database instance = new Database();
        instance.addDatabaseListener(new MyDBListener());
        
        numChange = 0;

        Account account = new Account("name1", "http://url.org", "username1");
        instance.addAccount(instance.getRootAccount(), account);
        Account subAccount = new Account("name2", "http://url2.org", "username2");
        instance.addAccount(account, subAccount);
        Account account2 = new Account("name3", "http://url3.org", "username3");
        instance.addAccount(instance.getRootAccount(), account2);
        
        account2.setName("name4");
        instance.changeAccount(account2);
        assertEquals(numChange, 1);
        assert(instance.getRootAccount().getChild(1).getName().equals(account2.getName()));
        assertEquals(numDirty, 4);
    }

    /**
     * Test of removeAccount method, of class Database.
     */
    @Test
    public void testRemoveAccount() throws Exception {
        System.out.println("removeAccount");
        Database instance = new Database();
        instance.addDatabaseListener(new MyDBListener());
        
        numChange = 0;

        Account account = new Account("name1", "http://url.org", "username1");
        instance.addAccount(instance.getRootAccount(), account);
        Account subAccount = new Account("name2", "http://url2.org", "username2");
        instance.addAccount(account, subAccount);
        Account account2 = new Account("name3", "http://url3.org", "username3");
        instance.addAccount(instance.getRootAccount(), account2);
        
        instance.removeAccount(account);
        assertEquals(numRemove, 1);
        assert(instance.getRootAccount().getChild(0).getName().equals(account2.getName()));
        assertEquals(numDirty, 4);
    }


    /**
     * Test of printDatabase method, of class Database.
     */
    @Test
    public void testPrintDatabase() throws Exception {
        System.out.println("printDatabase");
        Database instance = new Database();
        instance.addDatabaseListener(new MyDBListener());
        
        Account account = new Account("name1", "http://url.org", "username1");
        instance.addAccount(instance.getRootAccount(), account);
        Account subAccount = new Account("name2", "http://url2.org", "username2");
        instance.addAccount(account, subAccount);
        Account account2 = new Account("name3", "http://url3.org", "username3");
        instance.addAccount(instance.getRootAccount(), account2);
        
        instance.printDatabase();
    }

    /**
     * Test of findParent method, of class Database.
     */
    @Test
    public void testFindParent() throws Exception {
        System.out.println("findParent");
        Database instance = new Database();
        instance.addDatabaseListener(new MyDBListener());
        
        Account account = new Account("name1", "http://url.org", "username1");
        instance.addAccount(instance.getRootAccount(), account);
        Account subAccount = new Account("name2", "http://url2.org", "username2");
        instance.addAccount(account, subAccount);
        Account account2 = new Account("name3", "http://url3.org", "username3");
        instance.addAccount(instance.getRootAccount(), account2);
        
        assertEquals(account, instance.findParent(subAccount));
        assertEquals(instance.getRootAccount(), instance.findParent(account));
        assertEquals(instance.getRootAccount(), instance.findParent(account2));
    }

    @Test
    public void testFindAccountById() throws Exception {
        System.out.println("findAccountById");
        Database instance = new Database();
        instance.addDatabaseListener(new MyDBListener());
        
        Account account = new Account("name1", "http://url.org", "username1");
        account.setId(Account.createId("name1"));
        instance.addAccount(instance.getRootAccount(), account);
        Account subAccount = new Account("name2", "http://url2.org", "username2");
        subAccount.setId(Account.createId("name2"));
        instance.addAccount(account, subAccount);
        Account account2 = new Account("name3", "http://url3.org", "username3");
        account2.setId(Account.createId("name3"));
        instance.addAccount(instance.getRootAccount(), account2);

        assertEquals(account, instance.findAccountById(account.getId()));
        assertEquals(account2, instance.findAccountById(account2.getId()));
        assertEquals(subAccount, instance.findAccountById(subAccount.getId()));
    }
}
