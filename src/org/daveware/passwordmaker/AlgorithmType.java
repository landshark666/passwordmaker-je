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
 * Object representing an algorithm type.
 * 
 * All algorithms should be available in both plain and HMAC variants.
 * 
 * @author Dave Marotti
 */
public class AlgorithmType implements Comparable<AlgorithmType> {
    public static final AlgorithmType MD4           = new AlgorithmType(1, "MD4", "HMAC-MD4", "md4", "hmac-md4", true);
    public static final AlgorithmType MD5           = new AlgorithmType(2, "MD5", "HMAC-MD5", "md5", "hmac-md5", true);
    public static final AlgorithmType SHA1          = new AlgorithmType(3, "SHA1", "HMAC-SHA1", "sha1", "hmac-sha1", true);
    public static final AlgorithmType RIPEMD160     = new AlgorithmType(4, "RIPEMD160", "HMAC-RIPEMD160", "rmd160", "hmac-rmd160", true);
    public static final AlgorithmType SHA256        = new AlgorithmType(5, "SHA256", "HMAC-SHA256", "sha256", "hmac-sha256-fixed", true);

    // TODO: MD6, SHA384, SHA512?
    // Any of the weird ones? 

    private static final AlgorithmType [] TYPES = {
        MD4, MD5, SHA1, RIPEMD160, SHA256
    };
    
    private int type;
    private String name;
    private String hmacName;
    private boolean compatible;
    
    private String rdfName;
    private String rdfHmacName;
    
    private AlgorithmType() {
        type = 2;
        name = "";
        compatible = false;
        rdfName = "";
        rdfHmacName = "";
    }
    
    private AlgorithmType(int i, String n, String hmac, String rdfN, String rdfH, boolean c) {
        type = i;
        name = n;
        hmacName = hmac;
        compatible = c;
        rdfName = rdfN;
        rdfHmacName = rdfH;
    }
    
    public String getName() {
        return this.toString();
    }
    
    public int getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public String toRdfString() {
        return rdfName;
    }
    
    public String toHmacRdfString() {
        return rdfHmacName;
    }
    
    public static AlgorithmType [] getTypes() {
        return TYPES;
    }

    public int compareTo(AlgorithmType o) {
        if(type < o.type)
            return -1;
        if(type > o.type)
            return 1;
        return 0;
    }
    
    public boolean isCompatible() {
        return compatible;
    }
    
    public String getHmacName() {
        return hmacName;
    }
    
    /**
     * Converts a string to an algorithm type.
     * @param str The algorithm type. Valid values are: md4, md5, sha1, sha256,
     *            rmd160. Any of those valid types can be prefixed with "hmac-".
     * @return The algorithm type.
     * @throws Exception upon invalid algorithm string.
     */
    public static AlgorithmType fromRdfString(String str) 
            throws Exception
    {
        // default
        if(str.length()==0)
            return MD5;
        
        // Search the list of registered algorithms
        for(AlgorithmType algoType : TYPES) {
            String name = algoType.rdfName.toLowerCase();
            String hmacName = algoType.rdfHmacName.toLowerCase();
            
            if(str.compareTo(name)==0 || str.compareTo(hmacName)==0)
                return algoType;
        }
        
        // TODO: full support for all invalid  types should be present as well as allowing the account to exist and be modified.
        if(str.compareTo("hmac-sha256")==0)
            throw new IncompatibleException("Original hmac-sha1 implementation has been detected, " + 
                    "this is not compatibile with PasswordMakerJE due to a bug in the original " + 
                    "javascript version. It is recommended that you update this account to use " +
                    "\"HMAC-SHA256\" in the PasswordMaker settings.");
        if(str.compareTo("md5-v0.6")==0)
            throw new IncompatibleException("Original md5-v0.6 implementation has been detected, " + 
                    "this is not compatibile with PasswordMakerJE due to a bug in the original " + 
                    "javascript version. It is recommended that you update this account to use " +
                    "\"MD5\" in the PasswordMaker settings.");
        if(str.compareTo("hmac-md5-v0.6")==0)
            throw new IncompatibleException("Original hmac-md5-v0.6 implementation has been detected, " + 
                    "this is not compatibile with PasswordMakerJE due to a bug in the original " + 
                    "javascript version. It is recommended that you update this account to use " +
                    "\"HMAC-MD5\" in the PasswordMaker settings.");
        
        throw new Exception(String.format("Invalid algorithm type '%1s'", str));
    }
}
