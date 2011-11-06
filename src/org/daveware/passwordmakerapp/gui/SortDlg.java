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

import org.daveware.passwordmakerapp.SortOptions;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.graphics.Point;

/**
 * Implements a dialog where the user can choose sorting options.
 * 
 * @author Dave Marotti
 */
public class SortDlg extends Dialog {
    protected Shell shlSortOptions;
    private Button btnCancel;
    private Button btnOk;
    private Label lblSortStyle;
    private Label lblGroupLocation;
    private Combo comboStyle;
    private Combo comboGroupLocation;
    private Label separator;
    
    private SortOptions sortOptions = new SortOptions();
    private boolean okSelected = false;

    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public SortDlg(Shell parent, int style, SortOptions options) {
        super(parent, style);
        setText("SWT Dialog");
        sortOptions.ascending = options.ascending;
        sortOptions.groupsAtTop = options.groupsAtTop;
    }

    /**
     * Open the dialog.
     * @return The modified sort options when OK is selected, else null.
     */
    public SortOptions open() {
        createContents();
        
        comboStyle.select(sortOptions.ascending ? 0 : 1);
        comboGroupLocation.select(sortOptions.groupsAtTop ? 0 : 1);
        
        shlSortOptions.pack();
        shlSortOptions.open();
        shlSortOptions.layout();

        Display display = getParent().getDisplay();
        while (!shlSortOptions.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        
        return okSelected ? sortOptions : null;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shlSortOptions = new Shell(getParent(), getStyle());
        shlSortOptions.setMinimumSize(new Point(50, 28));
        shlSortOptions.setSize(333, 150);
        shlSortOptions.setText("Sort Options");
        shlSortOptions.setLayout(new FormLayout());
        
        btnCancel = new Button(shlSortOptions, SWT.NONE);
        FormData fd_btnCancel = new FormData();
        fd_btnCancel.bottom = new FormAttachment(100, -8);
        fd_btnCancel.width = 90;
        btnCancel.setLayoutData(fd_btnCancel);
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onCancelSelected();
            }
        });
        btnCancel.setText("Cancel");
        btnCancel.setImage(SWTResourceManager.getImage(SortDlg.class, "/org/daveware/passwordmaker/icons/cancel.png"));
        
        btnOk = new Button(shlSortOptions, SWT.NONE);
        fd_btnCancel.right = new FormAttachment(100, -105);
        FormData fd_btnOk = new FormData();
        fd_btnOk.bottom = new FormAttachment(100, -8);
        fd_btnOk.width = 90;
        fd_btnOk.top = new FormAttachment(0, 81);
        btnOk.setLayoutData(fd_btnOk);
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onOkSelected();
            }
        });
        btnOk.setText("OK");
        btnOk.setImage(SWTResourceManager.getImage(SortDlg.class, "/org/daveware/passwordmaker/icons/check.png"));
        
        lblSortStyle = new Label(shlSortOptions, SWT.NONE);
        FormData fd_lblSortStyle = new FormData();
        fd_lblSortStyle.right = new FormAttachment(0, 106);
        fd_lblSortStyle.top = new FormAttachment(0, 10);
        fd_lblSortStyle.left = new FormAttachment(0, 16);
        lblSortStyle.setLayoutData(fd_lblSortStyle);
        lblSortStyle.setAlignment(SWT.RIGHT);
        lblSortStyle.setText("Sort Style:");
        
        lblGroupLocation = new Label(shlSortOptions, SWT.NONE);
        FormData fd_lblGroupLocation = new FormData();
        fd_lblGroupLocation.right = new FormAttachment(0, 106);
        fd_lblGroupLocation.top = new FormAttachment(0, 42);
        fd_lblGroupLocation.left = new FormAttachment(0, 10);
        lblGroupLocation.setLayoutData(fd_lblGroupLocation);
        lblGroupLocation.setAlignment(SWT.RIGHT);
        lblGroupLocation.setText("Group Location:");
        
        comboStyle = new Combo(shlSortOptions, SWT.READ_ONLY);
        fd_btnOk.right = new FormAttachment(100, -9);
        FormData fd_comboStyle = new FormData();
        fd_comboStyle.left = new FormAttachment(lblSortStyle, 9);
        fd_comboStyle.right = new FormAttachment(100, -9);
        fd_comboStyle.top = new FormAttachment(0, 7);
        comboStyle.setLayoutData(fd_comboStyle);
        comboStyle.setItems(new String[] {"Ascending (A-Z)", "Descending (Z-A)"});
        comboStyle.select(0);
        
        comboGroupLocation = new Combo(shlSortOptions, SWT.READ_ONLY);
        FormData fd_comboGroupLocation = new FormData();
        fd_comboGroupLocation.left = new FormAttachment(lblGroupLocation, 9);
        fd_comboGroupLocation.right = new FormAttachment(100, -9);
        fd_comboGroupLocation.top = new FormAttachment(0, 39);
        comboGroupLocation.setLayoutData(fd_comboGroupLocation);
        comboGroupLocation.setItems(new String[] {"Groups at the top", "Groups on the bottom"});
        comboGroupLocation.select(0);
        
        separator = new Label(shlSortOptions, SWT.SEPARATOR | SWT.HORIZONTAL);
        fd_btnCancel.top = new FormAttachment(0, 81);
        FormData fd_separator = new FormData();
        fd_separator.top = new FormAttachment(comboGroupLocation, 10);
        fd_separator.left = new FormAttachment(0, 3);
        fd_separator.right = new FormAttachment(100, -3);
        fd_separator.bottom = new FormAttachment(100, -47);
        separator.setLayoutData(fd_separator);

    }
    
    private void onCancelSelected() {
        okSelected = false;
        shlSortOptions.dispose();
    }
    
    private void onOkSelected() {
        sortOptions.ascending = comboStyle.getSelectionIndex()==0;
        sortOptions.groupsAtTop = comboGroupLocation.getSelectionIndex()==0;
        okSelected = true;
        shlSortOptions.dispose();
    }
}
