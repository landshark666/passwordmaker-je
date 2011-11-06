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
package org.daveware.passwordmakerapp.gui;

import org.daveware.passwordmaker.Account;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AccountPatternModel implements IStructuredContentProvider {
	Account account = null;
	
	public AccountPatternModel() {
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		account = null;
	}

	@Override
	public void inputChanged(Viewer arg0, Object oldInput, Object newInput) {
		if(newInput instanceof Account)
			account = (Account)newInput;
		
	}

	@Override
	public Object[] getElements(Object arg0) {
		if(account==null)
			return null;
		return account.getPatterns().toArray();
	}
}
