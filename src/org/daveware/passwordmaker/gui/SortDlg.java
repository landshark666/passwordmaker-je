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
package org.daveware.passwordmaker.gui;

import org.daveware.passwordmaker.SortOptions;
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
    private Label label;
    
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
        shlSortOptions.setSize(317, 149);
        shlSortOptions.setText("Sort Options");
        
        btnCancel = new Button(shlSortOptions, SWT.NONE);
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onCancelSelected();
            }
        });
        btnCancel.setText("Cancel");
        btnCancel.setImage(SWTResourceManager.getImage(SortDlg.class, "/org/daveware/passwordmaker/icons/cancel.png"));
        btnCancel.setBounds(115, 87, 90, 32);
        
        btnOk = new Button(shlSortOptions, SWT.NONE);
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onOkSelected();
            }
        });
        btnOk.setText("OK");
        btnOk.setImage(SWTResourceManager.getImage(SortDlg.class, "/org/daveware/passwordmaker/icons/check.png"));
        btnOk.setBounds(211, 87, 90, 32);
        
        lblSortStyle = new Label(shlSortOptions, SWT.NONE);
        lblSortStyle.setAlignment(SWT.RIGHT);
        lblSortStyle.setBounds(16, 10, 90, 15);
        lblSortStyle.setText("Sort Style:");
        
        lblGroupLocation = new Label(shlSortOptions, SWT.NONE);
        lblGroupLocation.setAlignment(SWT.RIGHT);
        lblGroupLocation.setBounds(10, 42, 96, 15);
        lblGroupLocation.setText("Group Location:");
        
        comboStyle = new Combo(shlSortOptions, SWT.READ_ONLY);
        comboStyle.setItems(new String[] {"Ascending (A-Z)", "Descending (Z-A)"});
        comboStyle.setBounds(116, 7, 185, 23);
        comboStyle.select(0);
        
        comboGroupLocation = new Combo(shlSortOptions, SWT.READ_ONLY);
        comboGroupLocation.setItems(new String[] {"Groups at the top", "Groups on the bottom"});
        comboGroupLocation.setBounds(116, 39, 185, 23);
        comboGroupLocation.select(0);
        
        label = new Label(shlSortOptions, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setBounds(10, 76, 291, 2);

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
