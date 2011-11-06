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

import java.util.Comparator;

import org.daveware.passwordmaker.Account;

/**
 * Implements a comparator that can compare accounts based on sorting options.
 * 
 * @author Dave Marotti
 */
public class AccountComparator implements Comparator<Account> {
    SortOptions options = null;
    
    public AccountComparator(SortOptions o) {
        options = o;
    }
    
    public int compare(Account o1, Account o2) {
        int result;
        
        if(options.groupsAtTop) {
            if(o1.isFolder() && !o2.isFolder())
                return -1;
            else if(!o1.isFolder() && o2.isFolder())
                return 1;
        }
        else {
            if(o1.isFolder() && !o2.isFolder())
                return 1;
            else if(!o1.isFolder() && o2.isFolder())
                return -1;
        }

        result = o1.getName().compareToIgnoreCase(o2.getName());
        if(result!=0) {
            if(options.ascending) {
                return result;
            }
            
            return (result<0) ? 1 : -1;
        }
        else {
            result = o1.getName().compareTo(o2.getName());
            if(options.ascending) {
                return (result<0) ? 1 : -1;
            }
            
            return result;
        }
    }
}
