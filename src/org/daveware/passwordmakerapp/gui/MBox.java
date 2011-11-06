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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MBox {
    
    public static int showError(Shell shell, String msg) {
        MessageBox m = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
        m.setMessage(msg);
        return m.open();
    }

    public static int showInfo(Shell shell, String msg) {
        MessageBox m = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
        m.setMessage(msg);
        return m.open();
    }

    public static int showWarning(Shell shell, String msg) {
        MessageBox m = new MessageBox(shell, SWT.OK | SWT.ICON_WARNING);
        m.setMessage(msg);
        return m.open();
    }
    
    public static int showYesNo(Shell shell, String msg) {
        MessageBox m = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
        m.setMessage(msg);
        return m.open();
    }

    public static int showYesNoCancel(Shell shell, String msg) {
        MessageBox m = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
        m.setMessage(msg);
        return m.open();
    }
}
