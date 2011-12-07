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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

public class AccountTreeLabelProvider implements ILabelProvider {
	Image iconFolder = null;
	Image iconKey = null;
	Image iconKeyNext = null;
	
	public AccountTreeLabelProvider() {
	}
	
	public void loadImages() {
		if(iconFolder==null)
			iconFolder = SWTResourceManager.getImage(AccountTreeLabelProvider.class, "/org/daveware/passwordmakerapp/icons/folder.png");
		if(iconKey==null)
			iconKey = SWTResourceManager.getImage(AccountTreeLabelProvider.class, "/org/daveware/passwordmakerapp/icons/key.png");
		if(iconKeyNext==null)
			iconKeyNext = SWTResourceManager.getImage(AccountTreeLabelProvider.class, "/org/daveware/passwordmakerapp/icons/key_next.png");
	}

	@Override
	public void addListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		if(iconFolder!=null)
			iconFolder.dispose();
		if(iconKey!=null)
			iconKey.dispose();
		if(iconKeyNext!=null)
			iconKeyNext.dispose();
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getImage(Object o) {
		if(o instanceof Account) {
			Account account = (Account)o;
			if(account.isFolder())
				return iconFolder;
			if(account.getPatterns()!=null && account.getPatterns().size()>0)
				return iconKeyNext;
			return iconKey;
		}
		return null;
	}

	@Override
	public String getText(Object o) {
		if(o instanceof Account) {
			Account account = (Account)o;
			return account.toString();
		}
		return o.toString();
	}
}
