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
package org.daveware.passwordmakerapp.test;

import java.util.ArrayList;
import java.util.Collections;

import org.daveware.passwordmaker.Account;
import org.daveware.passwordmakerapp.AccountComparator;
import org.daveware.passwordmakerapp.SortOptions;
import org.junit.Test;

/**
 * Tests the AccountComparator object.
 * @author Dave Marotti
 */
public class TestAccountComparator {

    @Test
    public void test() {
        ArrayList<Account> list = new ArrayList<Account>();
        SortOptions o = new SortOptions(true, true);
                
        list.add(new Account("a1", false));
        list.add(new Account("a2", false));
        list.add(new Account("a1", true));
        list.add(new Account("a2", true));
        
        Collections.sort(list, new AccountComparator(o));
        assert(list.get(0).getName().compareTo("a1")==0 && list.get(0).isFolder()==true);
        assert(list.get(0).getName().compareTo("a2")==0 && list.get(0).isFolder()==true);
        assert(list.get(0).getName().compareTo("a1")==0 && list.get(0).isFolder()==false);
        assert(list.get(0).getName().compareTo("a2")==0 && list.get(0).isFolder()==false);
        
        o.ascending = false;
        Collections.sort(list, new AccountComparator(o));
        assert(list.get(0).getName().compareTo("a2")==0 && list.get(0).isFolder()==true);
        assert(list.get(0).getName().compareTo("a1")==0 && list.get(0).isFolder()==true);
        assert(list.get(0).getName().compareTo("a2")==0 && list.get(0).isFolder()==false);
        assert(list.get(0).getName().compareTo("a1")==0 && list.get(0).isFolder()==false);
        
        o.ascending = true;
        o.groupsAtTop = false;
        Collections.sort(list, new AccountComparator(o));
        assert(list.get(0).getName().compareTo("a1")==0 && list.get(0).isFolder()==false);
        assert(list.get(0).getName().compareTo("a2")==0 && list.get(0).isFolder()==false);
        assert(list.get(0).getName().compareTo("a1")==0 && list.get(0).isFolder()==true);
        assert(list.get(0).getName().compareTo("a2")==0 && list.get(0).isFolder()==true);
        
        o.ascending = false;
        Collections.sort(list, new AccountComparator(o));
        assert(list.get(0).getName().compareTo("a2")==0 && list.get(0).isFolder()==false);
        assert(list.get(0).getName().compareTo("a1")==0 && list.get(0).isFolder()==false);
        assert(list.get(0).getName().compareTo("a2")==0 && list.get(0).isFolder()==true);
        assert(list.get(0).getName().compareTo("a1")==0 && list.get(0).isFolder()==true);
    }

}
