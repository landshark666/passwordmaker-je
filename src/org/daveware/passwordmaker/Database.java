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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.daveware.passwordmaker.Account.UrlComponents;

/**
 * Represents a database of accounts.
 * 
 * An application can register a DatabaseListener to receive events.
 * 
 * @author Dave Marotti
 */
public class Database {
    private Logger logger = Logger.getLogger(getClass().getName());
    private Account rootAccount = new Account("default", "http://domain.com", "username");
    
    // The settings for the firefox plugin are stored in this file as well. This makes sure they are
    // preserved when saving back to the RDF.
    private HashMap<String, String> globalSettings = new HashMap<String, String>();
    
    // http://stackoverflow.com/questions/1658702/how-do-i-make-a-class-extend-observable-when-it-has-extended-another-class-too
    private final CopyOnWriteArrayList<DatabaseListener> listeners = new CopyOnWriteArrayList<DatabaseListener>();
    
    private boolean dirty = false;
    
    public Database() {
        rootAccount.setId(Account.ROOT_ACCOUNT_URI);
    }
    
    public Account getRootAccount() {
        return rootAccount;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // ACCOUNT MODIFICATION ROUTINES
    //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Adds an account to a parent.  This will first check to see if the account
     * already has a parent and if so remove it from that parent before adding it
     * to the new parent.
     * 
     * @param parent The parent to add the child under.
     * @param child The child to add.
     * @throws Exception On error.
     */
    public void addAccount(Account parent, Account child) 
            throws Exception {
        // Check to see if the account physically already exists - there is something funny with
        // some Firefox RDF exports where an RDF:li node gets duplicated multiple times.
        for(Account dup : parent.getChildren()) {
            if(dup == child) {
                logger.warning("Duplicate RDF:li=" + child.getId() + " detected. Dropping duplicate");
                return;
            }
        }
        
        int iterationCount = 0;
        final int maxIteration = 0x100000; // if we can't find a hash in 1 million iterations, something is wrong
        while(findAccountById(child.getId())!=null && (iterationCount++ < maxIteration)) {
            logger.warning("ID collision detected on '" + child.getId() + "', attempting to regenerate ID. iteration=" + iterationCount);
            child.setId(Account.createId(child));
        }
        if(iterationCount>=maxIteration) {
            throw new Exception("Could not genererate a unique ID in " + iterationCount + " iterations, this is really rare. I know it's lame, but change the description and try again.");
        }
        
        // add to new parent
        parent.getChildren().add(child);
        sendAccountAdded(parent, child);
        
        setDirty(true);
    }
    
    /**
     * This doesn't actually affect the data in any way but it does cause an
     * event to be sent to all registered DatabaseListener objects. If a program changes
     * data in an account in this database it should invoke this method so any listeners
     * get notified of the change.
     * @param account The account that was changed.
     */
    public void changeAccount(Account account) {
        sendAccountChanged(account);
        setDirty(true);
    }
    
    /**
     * Removes an account from a parent account.
     * @param accountToDelete Duh.
     */
    public void removeAccount(Account accountToDelete) {
        // Thou shalt not delete root
        if(accountToDelete.getId().equals(rootAccount.getId()))
            return;

        Account parent = findParent(accountToDelete);
        if(parent!=null) {
            removeAccount(parent, accountToDelete);
            setDirty(true);
        }
    }
    
    /**
     * Internal routine to remove a child from a parent.
     * @param parent
     * @param child 
     */
    private void removeAccount(Account parent, Account child) {
        parent.getChildren().remove(child);
        sendAccountRemoved(parent, child);
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // FIREFOX GLOBAL SETTING ROUTINES
    //
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Sets a firefox global setting. This allows any name and should be avoided. It
     * is used by the RDF reader.
     * 
     * @param name The name of the setting.
     * @param value The value.
     */
    public void setGlobalSetting(String name, String value) {
        String oldValue;
        
        // Avoid redundant setting
        if(globalSettings.containsKey(name)) {
            oldValue = globalSettings.get(name);
            if(value.compareTo(oldValue)==0)
                return;
        }
        globalSettings.put(name, value);
        setDirty(true);
    }

    /**
     * The preferred way of setting a global setting.
     */
    public void setGlobalSetting(GlobalSettingKey key, String value) {
        setGlobalSetting(key.toString(), value);
    }
    
    /**
     * Gets the entire set of global settings.
     * @return The list of global settings.
     */
    public HashMap<String, String> getGlobalSettings() {
        return globalSettings;
    }
    
    /**
     * The preferred way of getting a global setting value.
     * @param key The key to obtain.
     */
    public String getGlobalSetting(GlobalSettingKey key) {
        if(globalSettings.containsKey(key.toString()))
            return globalSettings.get(key.toString());
        return key.getDefault();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //
    // DATABASE LISTENER METHODS
    //
    ////////////////////////////////////////////////////////////////////////////
    public void addDatabaseListener(DatabaseListener l) {
        if(listeners.contains(l)==false)
            listeners.add(l);
    }
    
    public void removeDatabaseListener(DatabaseListener l) {
        listeners.remove(l);
    }

    private void sendAccountAdded(Account parent, Account child) {
        for(DatabaseListener listener : listeners)
            listener.accountAdded(parent, child);
    }
    
    private void sendAccountChanged(Account account) {
        for(DatabaseListener listener : listeners)
            listener.accountChanged(account);
    }
    
    private void sendAccountRemoved(Account parent, Account child) {
       for(DatabaseListener listener : listeners)
            listener.accountRemoved(parent, child);
    }
    
    private void sendDirtyStatusChanged() {
        for(DatabaseListener listener : listeners)
            listener.dirtyStatusChanged(dirty);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //
    // UTILITY FUNCTIONS
    //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Adds the default account (used by Firefox).
     */
    public void addDefaultAccount() 
        throws Exception 
    {
        Account defaultAccount = new Account();
        defaultAccount.setName("Defaults");
        defaultAccount.setId(Account.DEFAULT_ACCOUNT_URI);
        defaultAccount.setDesc("Default settings for URLs not elsewhere in this list");
        defaultAccount.setUrlComponents(EnumSet.of(UrlComponents.Domain));
        
        addAccount(rootAccount, defaultAccount);
    }
    
    /**
     * Checks if the db is dirty (needs to be saved).
     * @return true if dirty.
     */
    public boolean isDirty() {
        return dirty;
    }
    
    /**
     * Sets the dirty status and notifies listeners.
     */
    public void setDirty(boolean status) {
        dirty = status;
        sendDirtyStatusChanged();
    }
    
    /**
     * Debug routine to dump the database.
     */
    public void printDatabase() {
        printDatabase(rootAccount, 0);
    }
    
    /**
     * Gooey recursiveness.
     */
    private void printDatabase(Account acc, int level) {
        String buf = "";
        
        for(int i=0; i<level; i++)
            buf += " ";
        
        buf += "+";
        buf += acc.getName() + "[" + acc.getUrl() + "] (" + acc.getPatterns().size() + " patterns)";
        
        System.out.println(buf);
        
        for(Account account : acc.getChildren())
            printDatabase(account, level + 2);
    }
    
    /**
     * Locates an account, given an id.
     * @param id The account id (unique hash).
     * @return The account if found, else null.
     */
    public Account findAccountById(String id) {
        return findAccountById(rootAccount, id);
    }
    
    /**
     * Internal function to recurse through accounts looking for an id.
     * @param parent The parent to start the search at.
     * @param id The id to look for.
     * @return The Account if found, else null.
     */
    private Account findAccountById(Account parent, String id) {
        if(parent==null)
            return null;
        for(Account child : parent.getChildren()) {
            if(child.getId().equals(id))
                return child;
            if(child.getChildren().size()>0) {
                Account foundAccount = findAccountById(child, id);
                if(foundAccount!=null)
                    return foundAccount;
            }
        }
            
        return null;
    }
    
    /**
     * Searches the database for any account with a matching URL.
     * @param url The regex to search with.
     * @return The account if found, else null.
     */
    public Account findAccountByUrl(String url) {
    	return findAccountByUrl(rootAccount, url);
    }
    
    /**
     * Internal function to aid in searching.
     * @param parent
     * @param url
     * @return
     */
    private Account findAccountByUrl(Account parent, String url) {
    	// First search the parent
    	if(AccountPatternMatcher.matchUrl(parent, url))
    		return parent;
    	for(Account child : parent.getChildren()) {
    		Account foundAccount = findAccountByUrl(child, url);
    		if(foundAccount!=null)
    			return foundAccount;
    	}
    	return null;
    }
    
    /**
     * Locates the parent account for the given account.
     * @param account The account to find the parent of.
     * @return The parent account, else null if not found.
     */
    public Account findParent(Account account) {
        return findParent(rootAccount, account);
    }

    /**
     * Internal routine to locate a parent (recursive, don't loop your trees!).
     * @param parent The parent to start searching with.
     * @param account The account to find the parent of.
     * @return The parent account, else null if not found.
     */
    private Account findParent(Account parent, Account account) {
        if(parent==null || account==null)
            return null;
        
        // Does this parent contain the child account?
        if(parent.getChildren().contains(account))
            return parent;
        
        // Recurse through the children
        for(Account child : parent.getChildren()) {
            Account theOne = findParent(child, account);
            if(theOne!=null)
                return theOne;
        }
        
        return null;
    }
    
    /**
     * Finds the nearest relative of this node.
     * 
     * The nearest relative is either the next sibling, previous sibling, or parent in the case
     * where it is an only child.  If it is not found to be a member of this tree, null is returned.
     * If you pass in the root node, null is returned.
     * @param account The account to find the nearest relative of.
     * @return See description.
     */
    public Account findNearestRelative(Account account) {
        // If it's root, there are no siblings
        if(account.isRoot())
            return null;
        
        // If it has no parent, it's not in this tree
        Account parent = findParent(account);
        if(parent==null)
            return null;
        
        // It's an only child, return the parent
        if(parent.getChildren().size()==1)
            return parent;
        
        // If it doesn't have an index, it's also not in this tree (should not be possible, assume parent)
        int index = parent.getChildren().indexOf(account);
        if(index==-1)
            return parent;
        
        // If it's the last node, use the previous node
        if(index==parent.getChildren().size()-1)
            return parent.getChildren().get(index-1);

        // Otherwise use the next node
        return parent.getChildren().get(index+1);
    }
     
}
