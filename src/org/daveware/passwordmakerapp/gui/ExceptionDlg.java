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

import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.StringWriter;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ExceptionDlg extends Dialog {

	protected Object result;
	protected Shell shell;
	private Label lblAnExceptionHas;
	private Text textException;
	private Button btnClose;
	
	Exception exception;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ExceptionDlg(Shell parent, int style) {
		super(parent, style);
		setText("PasswordMaker-JE Exception");
	}
	
	public ExceptionDlg(Shell parent, Exception e) {
		super(parent);
		setText("PasswordMaker-JE Exception");
		exception = e;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();

		String text = "Please consider filing a bug report at: http://code.google.com/p/passwordmaker-je\n\nException: ";
		if(exception!=null) {
			text += exception.getMessage() + "\n";
			StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw);
	        exception.printStackTrace(pw);
	        text += sw.getBuffer().toString();
		}
		textException.setText(text);

		
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setSize(593, 445);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
		
		lblAnExceptionHas = new Label(shell, SWT.NONE);
		lblAnExceptionHas.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblAnExceptionHas.setText("An exception has occurred in PasswordMaker-JE:");
		
		textException = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textException.setEditable(false);
		textException.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		btnClose = new Button(shell, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.dispose();
			}
		});
		btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnClose.setText("Close");
		shell.setDefaultButton(btnClose);

	}
}
