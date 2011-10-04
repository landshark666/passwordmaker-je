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

import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Utility class which tests if an Account's patterns match an URL string.
 * 
 * @author Dave Marotti
 */
public class AccountPatternMatcher {
	/**
	 * Tests all patterns in an account for a match against the url string.
	 * @param account The account to test with.
	 * @param url THe url to test with.
	 * @return true if one of the 
	 */
	public static boolean matchUrl(Account account, String url) {
		for(AccountPatternData pattern : account.getPatterns()) {
			AccountPatternType type = pattern.getType();
			if(type==AccountPatternType.REGEX) {
				if(regexMatch(pattern.getPattern(), url))
					return true;
			}
			else if(type==AccountPatternType.WILDCARD) {
				if(globMatch(pattern.getPattern(), url))
					return true;
			}
			else {
			    Logger logger = Logger.getLogger(AccountPatternMatcher.class.getName());
			    logger.warning("Unknown pattern match type '" + type.toString() + "' for account '" +
			            account.getName() + "' id='" + account.getId() + "'");
				// meh
			}
		}
		return false;
	}
	
	private static boolean regexMatch(String regex, String text) {
		return Pattern.matches(regex, text);
	}
	
	/**
	 * Used in the globMatch method.
	 */
	static private class GlobMatch {
	    private String text;
	    private String pattern;

	    public GlobMatch() {}
	    
	    public boolean match(String text, String pattern) {
	        this.text = text;
	        this.pattern = pattern;

	        return matchCharacter(0, 0);
	    }

	    private boolean matchCharacter(int patternIndex, int textIndex) {
	        if (patternIndex >= pattern.length()) {
	                return false;
	        }

	        switch(pattern.charAt(patternIndex)) {
	                case '?':
	                        // Match any character
	                        if (textIndex >= text.length()) {
	                                return false;
	                        }
	                        break;

	                case '*':
	                        // * at the end of the pattern will match anything
	                        if (patternIndex + 1 >= pattern.length() || textIndex >= text.length()) {
	                                return true;
	                        }

	                        // Probe forward to see if we can get a match
	                        while (textIndex < text.length()) {
	                                if (matchCharacter(patternIndex + 1, textIndex)) {
	                                        return true;
	                                }
	                                textIndex++;
	                        }

	                        return false;

	                default:
	                        if (textIndex >= text.length()) {
	                                return false;
	                        }

	                        String textChar = text.substring(textIndex, textIndex + 1);
	                        String patternChar = pattern.substring(patternIndex, patternIndex + 1);

	                        // Note the match is case insensitive
	                        if (textChar.compareToIgnoreCase(patternChar) != 0) {
	                                return false;
	                        }
	        }

	        // End of pattern and text?
	        if (patternIndex + 1 >= pattern.length() && textIndex + 1 >= text.length()) {
	                return true;
	        }

	        // Go on to match the next character in the pattern
	        return matchCharacter(patternIndex + 1, textIndex + 1);
	    }
	}
	
	/**
	 * Performs a glob match against a string.
	 * 
	 * Taken from: http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
	 * 
	 * @param glob The glob pattern to match with.
	 * @param text The text to match against.
	 * @return true if it matches, else false.
	 */
	private static boolean globMatch(String glob, String text) {
		GlobMatch m = new GlobMatch();
		
		return m.match(text,  glob);
	}
}
