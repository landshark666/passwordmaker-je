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
 *
 * @author Dave Marotti
 */
public class AccountPatternData {
    private String pattern = "";
    private AccountPatternType type = AccountPatternType.WILDCARD;
    private boolean enabled = true;
    private String desc = "";
    
    public AccountPatternData() {
    }
    
    public AccountPatternData(AccountPatternData d) {
        pattern = d.pattern;
        type = d.type;
        enabled = d.enabled;
        desc = d.desc;
    }
    
    public void copyFrom(AccountPatternData d) {
        pattern = d.pattern;
        type = d.type;
        enabled = d.enabled;
        desc = d.desc;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * @return the type
     */
    public AccountPatternType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(AccountPatternType type) {
        this.type = type;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
