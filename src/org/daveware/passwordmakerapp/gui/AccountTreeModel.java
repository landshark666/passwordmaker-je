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
import org.daveware.passwordmaker.Database;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AccountTreeModel implements ITreeContentProvider {
	Database db = null;
	
	public AccountTreeModel() {
	}
	
	public Database getDatabase() {
		return db;
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		db = (Database)newInput;
	}

	@Override
	/**
	 * Gets the children of the passed in object.
	 */
	public Object[] getChildren(Object o) {
		if(o instanceof Account) {
			Account parent = (Account)o;
			return parent.getChildren().toArray();
		}
		return new Object[0];
	}

	@Override
	/**
	 * Gets the root level nodes.
	 */
	public Object[] getElements(Object o) {
		Account parent = db.getRootAccount();
		if(parent!=null) {
			return parent.getChildren().toArray();
		}
		
		return new Object[0];
	}

	@Override
	/**
	 * Locates the parent of a node.
	 */
	public Object getParent(Object o) {
		if(o instanceof Account) {
			Account child = (Account)o;
			
			return db.findParent(child);
		}
		return new Object[0];
	}

	@Override
	/**
	 * Tests if a node has children.
	 */
	public boolean hasChildren(Object o) {
		if(o instanceof Account) {
			Account account = (Account)o;
			return account.getChildren().size()>0;
		}
		return false;
	}
	
	
}
