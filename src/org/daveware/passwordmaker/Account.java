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

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

/**
 * Represents an account.  This object also functions as a parent account for
 * which it can have any number of child accounts.
 * 
 * @author Dave Marotti
 */
public final class Account implements Comparable<Account> {
	
	public enum UrlComponents {
		Protocol, Subdomain, Domain, PortPathAnchorQuery
	}
	
    public static String ROOT_ACCOUNT_URI = "http://passwordmaker.mozdev.org/accounts";
    public static String DEFAULT_ACCOUNT_URI = "http://passwordmaker.mozdev.org/defaults";
    
    public static int DEFAULT_LENGTH = 8;
    
    private String        name              = "";
    private String        desc              = "";
    private String        url               = "";
    private String        username          = "";
    private AlgorithmType algorithm         = AlgorithmType.MD5;
    private boolean       hmac              = false;
    private boolean       trim              = true;
    private int           length            = 8;
    private String        characterSet      = CharacterSets.BASE_93_SET;
    private LeetType      leetType          = LeetType.NONE;
    private LeetLevel     leetLevel         = LeetLevel.LEVEL1;
    private String        modifier          = "";
    private String        prefix            = "";
    private String        suffix            = "";
    private boolean       sha256Bug         = false;
    private String        id                = "";
    private boolean       autoPop           = false;
    private EnumSet<UrlComponents> urlComponents = defaultUrlComponents();
    
    private ArrayList<AccountPatternData> patterns = new ArrayList<AccountPatternData>();
    private ArrayList<Account> children     = new ArrayList<Account>();
    
    private boolean       isAFolder         = false;
    
    public Account() {
        
    }
    
    public Account(String name, boolean isFolder) {
        this.isAFolder = isFolder;
        this.name = name;
    }
    
    public Account(String name, String url, String username) {
        this.name = name;
        this.url = url;
        this.username = username;
    }
    
    /**
     * Constructor which allows all members to be defined except for the ID which
     * will be constructed from a SHA-1 hash of the URL + username. This does not
     * include autopop.
     * 
     * @param name
     * @param desc
     * @param url
     * @param username
     * @param algorithm
     * @param hmac
     * @param trim
     * @param length
     * @param characterSet
     * @param leetType
     * @param leetLevel
     * @param modifier
     * @param prefix
     * @param suffix
     * @param sha256Bug 
     */
    public Account(String name, String desc, String url, String username, AlgorithmType algorithm, boolean hmac,
            boolean trim, int length, String characterSet, LeetType leetType,
            LeetLevel leetLevel, String modifier, String prefix, String suffix,
            boolean sha256Bug) 
        throws Exception
    {
        this.name = name;
        this.desc = desc;
        this.url = url;
        this.username = username;
        this.algorithm = algorithm;
        this.hmac = hmac;
        this.trim = trim;
        this.length = length;
        this.characterSet = characterSet;
        this.leetType = leetType;
        this.leetLevel = leetLevel;
        this.modifier = modifier;
        this.prefix = prefix;
        this.suffix = suffix;
        this.sha256Bug = sha256Bug;
        this.id = createId(this.url + this.username);
    }
    
    /**
     * Copies the settings (not including children or ID) from another account.
     * 
     * LEAVE THIS FUNCTION HERE so it's easy to see if new members are ever added
     * so I don't forget to update it.
     * 
     * @param a The other account to copy from.
     */
    public void copySettings(Account a) {
        this.name         = a.name;
        this.desc         = a.desc;
        this.url          = a.url;
        this.username     = a.username;
        this.algorithm    = a.algorithm;
        this.hmac         = a.hmac;
        this.trim         = a.trim;
        this.length       = a.length;
        this.characterSet = a.characterSet;
        this.leetType     = a.leetType;
        this.leetLevel    = a.leetLevel;
        this.modifier     = a.modifier;
        this.prefix       = a.prefix;
        this.suffix       = a.suffix;
        this.sha256Bug    = a.sha256Bug;
        this.isAFolder    = a.isAFolder;
        this.autoPop      = a.autoPop;
        this.patterns.clear();
        for(AccountPatternData data : a.getPatterns()) {
            this.patterns.add(new AccountPatternData(data));
        }
        
        // The documentation says EnumSet.copyOf() will fail on empty sets.
        if(a.urlComponents.isEmpty()==false)
            this.urlComponents = EnumSet.copyOf(a.urlComponents);
        else
            this.urlComponents = defaultUrlComponents();
    }
    
	    /**
	     * Alternate constructor that allows the id to also be supplied. (Still no autopop).
	     */
	    public Account(String name, String desc, String url, String username, AlgorithmType algorithm, boolean hmac,
	            boolean trim, int length, String characterSet, LeetType leetType,
	            LeetLevel leetLevel, String modifier, String prefix, String suffix,
	            boolean sha256Bug, String id) {
	        this.name = name;
	        this.desc = desc;
	        this.url = url;
	        this.username = username;
	        this.algorithm = algorithm;
	        this.hmac = hmac;
	        this.trim = trim;
	        this.length = length;
	        this.characterSet = characterSet;
	        this.leetType = leetType;
	        this.leetLevel = leetLevel;
	        this.modifier = modifier;
	        this.prefix = prefix;
	        this.suffix = suffix;
	        this.sha256Bug = sha256Bug;
	        this.id = id;
	    }
    
    /**
     * Creates an ID from a string. 
     * 
     * This is used to create the ID of the account which should be unique. 
     * There's no way for this object to know if it is truly unique so the database
     * or GUI should do a check for uniqueness and re-hash if needed.
     * 
     * @param str The string to hash.
     * @return A hashed (SHA1) version of the string.
     * @throws Exception Upon hashing error.
     */
    public static String createId(String str) 
        throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA1", "BC");
        return new String("rdf:#$" + digest.digest(str.getBytes()));
    }
    
    /**
     * Creates and _returns_ an ID from data in an account.
     * @param acc The account to base the data off.
     * @return The new ID.
     * @throws Exception on error.
     */
    public static String createId(Account acc) 
        throws Exception {
        return Account.createId(acc.getName() + acc.getDesc() + (new Random()).nextLong() + Runtime.getRuntime().freeMemory());
    }

    /**
     * Gets the default set of UrlComponents (empty set).
     * @return
     */
	private static EnumSet<UrlComponents> defaultUrlComponents() {
	    return EnumSet.noneOf(UrlComponents.class);
	}
	
    public String getName() {
        return name;
    }
    
    public void setName(String s) {
        name = s;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String s) {
        desc = s;
    }
    
    public boolean isFolder() {
        return isAFolder;
    }
    
    public boolean isAutoPop() {
        return autoPop;
    }
    
    public void setAutoPop(boolean b) {
        autoPop = b;
    }
    
    /**
     * Determines if this is the default account. The default account has a special ID.
     * @return true if it is.
     */
    public boolean isDefault() {
        return id.compareTo(DEFAULT_ACCOUNT_URI)==0;
    }
    
    /**
     * Determines if this is the root account. The root account has a special ID.
     * @return true if it is.
     */
    public boolean isRoot() {
        return id.compareTo(ROOT_ACCOUNT_URI)==0;
    }
    
    public void setIsFolder(boolean b) {
        isAFolder = b;
    }
    
    /**
     * @return the algorithm
     */
    public AlgorithmType getAlgorithm() {
        return algorithm;
    }

    /**
     * @param algorithm the algorithm to set
     */
    public void setAlgorithm(AlgorithmType algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * @return the hmac
     */
    public boolean isHmac() {
        return hmac;
    }

    /**
     * @param hmac the hmac to set
     */
    public void setHmac(boolean hmac) {
        this.hmac = hmac;
    }

    /**
     * @return the trim
     */
    public boolean isTrim() {
        return trim;
    }

    /**
     * @param trim the trim to set
     */
    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the characterSet
     */
    public String getCharacterSet() {
        return characterSet;
    }

    /**
     * @param characterSet the characterSet to set
     */
    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    /**
     * @return the leetType
     */
    public LeetType getLeetType() {
        return leetType;
    }

    /**
     * @param leetType the leetType to set
     */
    public void setLeetType(LeetType leetType) {
        this.leetType = leetType;
    }

    /**
     * @return the leetLevel
     */
    public LeetLevel getLeetLevel() {
        return leetLevel;
    }

    /**
     * @param leetLevel the leetLevel to set
     */
    public void setLeetLevel(LeetLevel leetLevel) {
        this.leetLevel = leetLevel;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the modifier
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * @param modifier the modifier to set
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @param suffix the suffix to set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * @return the sha256Bug
     */
    public boolean isSha256Bug() {
        return sha256Bug;
    }

    /**
     * @param sha256Bug the sha256Bug to set
     */
    public void setSha256Bug(boolean sha256Bug) {
        this.sha256Bug = sha256Bug;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Clears the UrlComponents used with this account
     */
    public final void clearUrlComponents() {
    	this.urlComponents.clear();
    }
    
    /**
     * @param urlComponent - Add a component of the url to be used as the input text for the generated password
     */
    public final void addUrlComponent(UrlComponents urlComponent) {
    	this.urlComponents.add(urlComponent);
    }
    
    /**
     * @param urlComponents - the Components to use of the url as the input text for the generated password
     */
    public final void setUrlComponents(Set<UrlComponents> urlComponents) {
    	this.urlComponents.clear();
    	this.urlComponents.addAll(urlComponents);
    }
    
    /**
     * If the urlComponents field is empty then the entire getUrl field will be used. 
     * This set is unmodifiable.  Use the helper functions to set or modify the set.
     * @return the url components specified for this account (may be empty)
     */
	public final Set<UrlComponents> getUrlComponents() {
		return Collections.unmodifiableSet(urlComponents);
	}
    
    /**
     * Gets a list of the child accounts. This list is modifiable.
     * @return The list of accounts (may be empty).
     */
    public ArrayList<Account> getChildren() {
        return children;
    }
    
    /**
     * Gets the count of all children (of children of children...).
     * @return The total number of all descendants.
     */
    public int getNestedChildCount() {
        ArrayList<Account> stack = new ArrayList<Account>();
        int size = 0;
        
        stack.add(this);
        while(stack.size()>0) {
            Account current = stack.get(0);
            stack.remove(0);
            for(Account child : current.getChildren()) {
                size++;
                if(child.hasChildren())
                    stack.add(child);
            }
        }
        
        return size;
    }
    
    
    
    /**
     * Gets a specifically indexed child.
     * @param index The index of the child to get.
     * @return The child at the index.
     * @throws IndexOutOfBoundsException upon invalid index.
     */
    public Account getChild(int index) throws IndexOutOfBoundsException {
        if(index<0 || index >= children.size())
            throw new IndexOutOfBoundsException("Illegal child index, " + index);
        return children.get(index);
    }
    
    public boolean hasChildren() {
        return children.size() > 0;
    }
    
    /**
     * Tests if an account is a direct child of this account.
     * @param account
     * @return
     */
    public boolean hasChild(Account account) {
        for(Account child : children) {
            if(child.equals(account))
                return true;
        }
        return false;
    }

    /**
     * Implements the Comparable<Account> interface, this is based first on if the
     * account is a folder or not. This is so that during sorting, all folders are
     * first in the list.  Finally, it is based on the name.
     * 
     * @param o The other account to compare to.
     * @return this.name.compareTo(otherAccount.name);
     */
    public int compareTo(Account o) {
        if(this.isFolder() && !o.isFolder())
            return -1;
        else if(!this.isFolder() && o.isFolder())
            return 1;
        
        // First ignore case, if they equate, use case.
        int result = name.compareToIgnoreCase(o.name);
        if(result==0)
            return name.compareTo(o.name);
        else
            return result;
    }
    
    /**
     * Determines if these are the same object or not. "Same objects" are those that have
     * identical IDs.
     */
    public boolean equals(Object o) {
        if(this == o)
            return true;
        
        if(!(o instanceof Account))
            return false;
        
        Account thatAccount = (Account)o;
        
        return thatAccount.id.compareTo(this.id)==0;
    }

    /**
     * @return the patterns
     */
    public ArrayList<AccountPatternData> getPatterns() {
        return patterns;
    }

    /**
     * @param patterns the patterns to set
     */
    public void setPatterns(ArrayList<AccountPatternData> patterns) {
        this.patterns = patterns;
    }
    
    @Override
    public String toString() {
    	return this.name;
    }
}
