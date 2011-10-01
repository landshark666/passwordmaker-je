package org.daveware.passwordmaker.gui;

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
