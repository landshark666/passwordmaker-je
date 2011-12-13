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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.daveware.passwordmaker.Account.UrlComponents;

/**
 * This class is used to generate passwords from a master password and an account.
 * @author Dave Marotti
 */
public class PasswordMaker {
	private static Pattern urlRegex = Pattern.compile("([^:\\/\\/]*:\\/\\/)?([^:\\/]*)([^#]*)");
    /**
     * Maps an array of characters to another character set.
     * 
     * This is the magic which allows an encrypted password to be mapped into a
     * a specific character set.
     * 
     * @param input The array of characters to map.
     * @param encoding The list of characters to map to.
     * @param trim Whether to trim leading zeros ... I think.
     * @return The mapped string.
     * @throws Exception On odd length!?
     */
    public SecureCharArray rstr2any(char[] input, String encoding, boolean trim) 
            throws Exception {
        int length = input.length;
        int divisor;
        int full_length;
        int[] dividend;
        int[] remainders;
        int remainders_count = 0;
        int dividend_length;
        int i, j;
        
        int outputPosition = 0;
        SecureCharArray output = new SecureCharArray();

        // can't handle odd lengths
        if ((length % 2) != 0) {
            return output;
        }

        divisor = encoding.length();
        dividend_length = (int) Math.ceil((double) length / 2.0);
        dividend = new int[dividend_length];
        for (i = 0; i < dividend_length; i++) {
            dividend[i] = (((int) input[i * 2]) << 8) | ((int) input[i * 2 + 1]);
        }

        full_length = (int) Math.ceil((double) length * 8 / (Math.log((double) encoding.length()) / Math.log((double) 2)));
        remainders = new int[full_length];

        if (trim) {
            while (dividend_length > 0) {
                // TODO: zero-out the quotient array each iteration at the end
                int[] quotient;
                int quotient_length = 0;
                int qCounter = 0;
                int x = 0;

                quotient = new int[dividend_length];
                for (i = 0; i < dividend_length; i++) {
                    int q;
                    x = (x << 16) + dividend[i];
                    q = (int) Math.floor((double) x / divisor);
                    x -= q * divisor;
                    if (quotient_length > 0 || q > 0) {
                        quotient[qCounter++] = q;
                        quotient_length++;
                    }
                }
                remainders[remainders_count++] = x;
                dividend_length = quotient_length;
                dividend = quotient;
            }
            full_length = remainders_count;
        } else {
            for (j = 0; j < full_length; j++) {
                int[] quotient;
                int quotient_length = 0;
                int qCounter = 0;
                int x = 0;

                quotient = new int[dividend_length];
                for (i = 0; i < dividend_length; i++) {
                    int q;
                    x = (x << 16) + dividend[i];
                    q = (int) Math.floor((double) x / divisor);
                    x -= q * divisor;
                    if (quotient_length > 0 || q > 0) {
                        quotient[qCounter++] = q;
                        quotient_length++;
                    }
                }
                remainders[j] = x;
                dividend_length = quotient_length;
                dividend = quotient;
            }
        }

        if(output.size() < full_length)
            output.resize(full_length, false);
        
        for (i = full_length - 1; i >= 0; i--) {
            output.setCharAt(outputPosition++, encoding.charAt(remainders[i]));
        }

        return output;
    }

    /**
     * Calculates the strength of a password.
     * @return An integer value from 0 to 100.
     */
    public static double calcPasswordStrength(SecureCharArray pw) {
        ArrayList<Character> uniques = new ArrayList<Character>();
        int pwLength = pw.size();
        int i, j;
        
        if(pwLength<=2)
            return 0;
        
        // Find character frequency
        for(i=0; i<pwLength; i++) {
            for(j=0; j<uniques.size(); j++) {
                if(i==j)
                    continue;
                if(pw.getCharAt(i) == uniques.get(j).charValue())
                    break;
            }
            if(j==uniques.size())
                uniques.add(pw.getCharAt(i));
        }
        
        double r0 = ((double)uniques.size()) / ((double)pwLength);
        if(uniques.size()==1)
            r0 = 0;
        
        // length of the password - 1pt per char over 5, up to 15 for 10 pts total
        double r1 = (double)pwLength;
        if(r1 >= 15)
            r1 = 10;
        else if(r1 < 5)
            r1 = -5;
        else
            r1 -= 5;
        
        double quarterLen = Math.round(((double)pwLength) / 4.0);
        
        /*
        // Ratio of numbers in the password
        float [] ratios = {0, 0, 0, 0};
        float [] cc = {0, 0, 0, 0};
        for(i=0; i<pwLength;i++) {
            if(!Character.isDigit(pw.getCharAt(i)))
                ratios[0] += 1.0f;
            if(Character.isLetterOrDigit(pw.getCharAt(i)))
                ratios[1] += 1.0f;
            if(!Character.isUpperCase(pw.getCharAt(i)))
                ratios[2] += 1.0f;
            if(!Character.isLowerCase(pw.getCharAt(i)))
                ratios[3] += 1.0f;
        }
        for(i=0; i<ratios.length; i++) {
            cc[i] = ratios[i] > quarterLen*2.0f ? quarterLen : Math.abs(quarterLen - ratios[i]);
            ratios[i] = 1.0f - (cc[i] / quarterLen);
        }
        
        float pwStrength = (((ratios[0] + ratios[1] + ratios[2] + ratios[3] + r0) / 5.0f) * 100.0f ) + r1;
*/
        
        double num = 0;
        for(i=0; i<pwLength; i++)
            if(!Character.isDigit(pw.getCharAt(i)))
                num++;
        num = pwLength - num;
        double c = num > quarterLen*2.0 ? quarterLen : Math.abs(quarterLen - num);
        double r2 = 1.0 - (c / quarterLen);
        
        // ratio of symbols in the password
        num = 0;
        for(i=0; i<pwLength; i++)
            if(Character.isLetterOrDigit(pw.getCharAt(i)) || pw.getCharAt(i)=='_')
                num++;
        num = pwLength - num;
        c = num > quarterLen*2 ? quarterLen : Math.abs(quarterLen - num);
        double r3 = 1.0 - (c / quarterLen);

        // ratio of uppercase in the password
        num = 0;
        for(i=0; i<pwLength; i++)
            if(!Character.isUpperCase(pw.getCharAt(i)))
                num++;
        num = pwLength - num;
        c = num > quarterLen*2 ? quarterLen : Math.abs(quarterLen - num);
        double r4 = 1.0 - (c / quarterLen);

        // ratio of lower case in the password
        num = 0;
        for(i=0; i<pwLength; i++)
            if(!Character.isLowerCase(pw.getCharAt(i)))
                num++;
        num = pwLength - num;
        c = num > quarterLen*2 ? quarterLen : Math.abs(quarterLen - num);
        double r5 = 1.0 - (c / quarterLen);

        //System.out.println("debug1 = " + r2 + "," + r3 + "," + r4 + "," + r5);

        double pwStrength = (((r0+r2+r3+r4+r5) / 5.0f) * 100.0f) + r1;
        
        if(pwStrength < 0.0f)
            pwStrength = 0.0f;
        if(pwStrength > 100.0f)
            pwStrength = 100.0f;
        
        return pwStrength;
    }
    
    
	public final String getModifiedInputText(final String inputText, final Account account) {
		final Set<UrlComponents> uriComponents = account.getUrlComponents();
		if (uriComponents.isEmpty()) {
		    if(account.isDefault())
		        return "";
		    else
		        return account.getUrl();
		}
		Matcher matcher = urlRegex.matcher(inputText);
		if (!matcher.matches())
			return inputText;
		String protocol = matcher.group(1);
		String domainText = matcher.group(2);
		String portPath = matcher.group(3);
		if ( protocol == null ) protocol = "";
		if ( domainText == null ) domainText = "";
		if ( portPath == null ) portPath = "";
		
		StringBuilder retVal = new StringBuilder(inputText.length());
		if (uriComponents.contains(UrlComponents.Protocol) && protocol.length() > 0) {
			retVal.append(protocol);
		}
		if (domainText != null) {
			final String subDomain;
			int dnDot = domainText.lastIndexOf('.');
			dnDot = domainText.lastIndexOf('.', dnDot - 1);
			if (dnDot != -1) {
				subDomain = domainText.substring(0, dnDot);
				domainText = domainText.substring(dnDot + 1);
			} else {
				subDomain = "";
			}
			final boolean hasSubDomain = uriComponents
					.contains(UrlComponents.Subdomain) && dnDot != -1;
			if (hasSubDomain) {
				retVal.append(subDomain);
			}
			if (uriComponents.contains(UrlComponents.Domain)) {
				if (hasSubDomain)
					retVal.append('.');
				retVal.append(domainText);
			}
		}
		if (uriComponents.contains(UrlComponents.PortPathAnchorQuery)
				&& portPath.length() > 0) {
			retVal.append(portPath);
		}
		return retVal.toString();
	}
    
    /**
     * Generates a hash of the master password with settings from the account.
     * @param masterPassword The password to use as a key for the various algorithms.
     * @param account The account with the specific settings for the hash.
     * @param inputText The text to use as the input into the password maker algorithm
     * @return A SecureCharArray with the hashed data.
     * @throws Exception if something bad happened.
     */
    public SecureCharArray makePassword(SecureCharArray masterPassword, Account account, final String inputText)
            throws Exception
    {
        LeetLevel leetLevel = account.getLeetLevel();
        //int count = 0;
        int length = account.getLength();
        SecureCharArray output = null;
        SecureCharArray data = null;

        try {
            if(account.getCharacterSet().length() < 2)
                throw new Exception("Account contains a character set that is too short");

            data = new SecureCharArray(getModifiedInputText(inputText, account) + account.getUsername() + account.getModifier() );

            // Use leet before hashing
            if(account.getLeetType()==LeetType.BEFORE || account.getLeetType()==LeetType.BOTH) {
                LeetEncoder.leetConvert(leetLevel, masterPassword);
                LeetEncoder.leetConvert(leetLevel, data);
            }

            // Perform the actual hashing
            output = hashTheData(masterPassword, data, account);

            // Use leet after hashing
            if(account.getLeetType()==LeetType.AFTER || account.getLeetType()==LeetType.BOTH) {
                LeetEncoder.leetConvert(leetLevel, output);
            }

            // Apply the prefix
            if(account.getPrefix().length() > 0) {
                SecureCharArray prefix = new SecureCharArray(account.getPrefix());
                output.prepend(prefix);
                prefix.erase();
            }

            // Handle the suffix
            output.resize(length, true);
            if(account.getSuffix().length() > 0) {
                SecureCharArray suffix = new SecureCharArray(account.getSuffix());

                // If the suffix is larger than the entire password (not smart), then
                // just replace the output with a section of the suffix that fits
                if(length < suffix.size()) {
                    output.replace(suffix);
                    output.resize(length, true);
                }
                // Otherwise insert the prefix where it fits
                else {
                    output.resize(length - suffix.size(), true);
                    output.append(suffix);
                }

                suffix.erase();
            }
        }
        catch(Exception e) {
            if(output!=null)
                output.erase();
            throw e;
        }
        finally {
            // not really needed... but here for completeness
            if(data!=null)
                data.erase();
        }
        
        return output;
    }
    
    /**
     * Generates a hash of the master password with settings from the account.
     * @param masterPassword The password to use as a key for the various algorithms.
     * @param account The account with the specific settings for the hash. Uses account.getUrl() as the inputText
     * @return A SecureCharArray with the hashed data.
     * @throws Exception if something bad happened.
     */
    public SecureCharArray makePassword(SecureCharArray masterPassword, Account account)
            throws Exception
    {
    	return makePassword(masterPassword, account, account.getUrl());
    }
    /**
     * Intermediate step of generating a password. Performs constant hashing until
     * the resulting hash is long enough.
     * 
     * @param masterPassword You should know by now.
     * @param data Not much has changed.
     * @param account A donut?
     * @return A suitable hash.
     * @throws Exception if we ran out of donuts.
     */
    private SecureCharArray hashTheData(SecureCharArray masterPassword, SecureCharArray data, Account account)
            throws Exception
    {
        SecureCharArray output                  = new SecureCharArray();
        SecureCharArray secureIteration         = new SecureCharArray();
        SecureCharArray intermediateOutput      = null;
        SecureCharArray interIntermediateOutput = null;
        int count   = 0;
        int length  = account.getLength();
        
        try {
            while(output.size() < length) {
                if(count==0) {
                    intermediateOutput = runAlgorithm(masterPassword, data, account);
                }
                else {
                    // add ye bit'o chaos
                    secureIteration.replace(masterPassword);
                    secureIteration.append(new SecureCharArray("\n"));
                    secureIteration.append(new SecureCharArray(Integer.toString(count)));
                    
                    interIntermediateOutput = runAlgorithm(secureIteration, data, account);
                    intermediateOutput.append(interIntermediateOutput);
                    interIntermediateOutput.erase();

                    secureIteration.erase();
                }
                output.append(intermediateOutput);
                intermediateOutput.erase();

                count++;
            }
        } catch(Exception e) {
            if(output!=null)
                output.erase();
            throw e;
        } finally {
            if(intermediateOutput!=null)
                intermediateOutput.erase();
            if(interIntermediateOutput!=null)
                interIntermediateOutput.erase();
            if(secureIteration!=null)
                secureIteration.erase();
        }
        
        return output;
    }
    
    /**
     * This performs the actual hashing. It obtains an instance of the hashing algorithm
     * and feeds in the necessary data.
     * 
     * @param masterPassword The master password to use as a key.
     * @param data The data to be hashed.
     * @param account The account with the hash settings to use.
     * @return A SecureCharArray of the hash.
     * @throws Exception if something bad happened.
     */
    private SecureCharArray runAlgorithm(SecureCharArray masterPassword, SecureCharArray data, Account account)
            throws Exception
    {
        SecureCharArray output = null;
        SecureCharArray digestChars = null;
        SecureByteArray masterPasswordBytes = null;
        SecureByteArray dataBytes = null;

        try {
            masterPasswordBytes = new SecureByteArray(masterPassword.getData());
            dataBytes = new SecureByteArray(data.getData());

            if (account.isHmac() == false) {
                dataBytes.prepend(masterPasswordBytes);
            }

            if (account.isHmac()) {
                Mac mac;
                String algoName = "HMAC" + account.getAlgorithm().getName();
                mac = Mac.getInstance(algoName, "BC");
                mac.init(new SecretKeySpec(masterPasswordBytes.getData(), algoName));
                mac.reset();
                mac.update(dataBytes.getData());
                digestChars = new SecureCharArray(mac.doFinal());
            } else {
                MessageDigest md = MessageDigest.getInstance(account.getAlgorithm().getName(), "BC");
                digestChars = new SecureCharArray(md.digest(dataBytes.getData()));
            }


            output = rstr2any(digestChars.getData(), account.getCharacterSet(), account.isTrim());
        } catch(Exception e) {
            if(output!=null)
                output.erase();
            throw e;
        } finally {
            if(masterPasswordBytes!=null)
                masterPasswordBytes.erase();
            if(dataBytes!=null)
                dataBytes.erase();
            if(digestChars!=null)
                digestChars.erase();
        }
        
        return output;
    }
}
