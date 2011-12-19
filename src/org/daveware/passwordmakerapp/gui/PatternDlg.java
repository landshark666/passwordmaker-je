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

import org.daveware.passwordmaker.AccountPatternData;
import org.daveware.passwordmaker.AccountPatternType;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;

public class PatternDlg extends Dialog {

    AccountPatternData patternData = null;
    boolean okClicked = false;
    
    protected Object result;
    protected Shell shlUrlPatternMatching;
    private Label lblName;
    private Text textName;
    private Text textPattern;
    private Button chkEnabled;
    private Group grpPatternContains;
    private Button btnWildcards;
    private Button btnRegularExpression;
    private Label lblExamplemailyahoocom;
    private Label lblExampleHttpsmailyahoo;
    private Button button;
    private Button button_1;
    private Label lblUrlOrUrl;
    private ControlDecoration nameDecoration;
    private ControlDecoration patternDecoration;

    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public PatternDlg(Shell parent, int style, AccountPatternData data) {
        super(parent, style);
        setText("URL Patterns");
        patternData = new AccountPatternData(data);
    }
    
    /**
     * Open the dialog.
     * @return the result
     */
    public AccountPatternData open() {
        createContents();
        
        populateGuiFromData();
        setupDecorators();
        textName.setFocus();

        shlUrlPatternMatching.pack();
        shlUrlPatternMatching.open();
        shlUrlPatternMatching.layout();        
        
        Display display = getParent().getDisplay();
        while (!shlUrlPatternMatching.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        
        if(okClicked)
            return patternData;
        
        return null;
    }
    
    private boolean populateDataFromGui() {
        if(textName.getText().length()<=0) {
            nameDecoration.show();
            textName.setFocus();
            return false;
        }
        
        nameDecoration.hide();
        patternData.setDesc(textName.getText());
        
        if(textPattern.getText().length()<=0) {
            patternDecoration.show();
            textPattern.setFocus();
            return false;
        }
        patternDecoration.hide();
        patternData.setPattern(textPattern.getText());
        
        patternData.setEnabled(chkEnabled.getSelection());
        
        if(btnWildcards.getSelection())
            patternData.setType(AccountPatternType.WILDCARD);
        else
            patternData.setType(AccountPatternType.REGEX);
            
        return true;
    }

    private void populateGuiFromData() {
        textName.setText(patternData.getDesc());
        textPattern.setText(patternData.getPattern());
        chkEnabled.setSelection(patternData.isEnabled());
        
        if(patternData.getType()==AccountPatternType.WILDCARD)
            btnWildcards.setSelection(true);
        else
            btnRegularExpression.setSelection(true);
    }

    private void setupDecorators() {
        ControlDecoration [] decors = { nameDecoration, patternDecoration };
        Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
        for(ControlDecoration dec : decors) {
            dec.setImage(image);
            dec.hide();
        }
    }
    
    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shlUrlPatternMatching = new Shell(getParent(), getStyle());
        shlUrlPatternMatching.setMinimumSize(new Point(450, 100));
        shlUrlPatternMatching.setSize(450, 135);
        shlUrlPatternMatching.setText("URL Pattern Matching Data");
        shlUrlPatternMatching.setLayout(new FormLayout());
        
        lblName = new Label(shlUrlPatternMatching, SWT.NONE);
        FormData fd_lblName = new FormData();
        fd_lblName.top = new FormAttachment(0, 10);
        fd_lblName.left = new FormAttachment(0, 10);
        lblName.setLayoutData(fd_lblName);
        lblName.setAlignment(SWT.RIGHT);
        lblName.setText("Pattern Name:");
        
        textName = new Text(shlUrlPatternMatching, SWT.BORDER);
        fd_lblName.right = new FormAttachment(textName, -7);
        FormData fd_textName = new FormData();
        fd_textName.left = new FormAttachment(0, 125);
        fd_textName.right = new FormAttachment(100, -10);
        fd_textName.top = new FormAttachment(0, 7);
        textName.setLayoutData(fd_textName);
        
        nameDecoration = new ControlDecoration(textName, SWT.LEFT | SWT.TOP);
        nameDecoration.setDescriptionText("A pattern name must be specified.");
        
        textPattern = new Text(shlUrlPatternMatching, SWT.BORDER);
        FormData fd_textPattern = new FormData();
        fd_textPattern.top = new FormAttachment(0, 34);
        fd_textPattern.left = new FormAttachment(0, 125);
        fd_textPattern.right = new FormAttachment(100, -10);
        textPattern.setLayoutData(fd_textPattern);
        
        chkEnabled = new Button(shlUrlPatternMatching, SWT.CHECK);
        FormData fd_chkEnabled = new FormData();
        fd_chkEnabled.left = new FormAttachment(lblName, 0, SWT.LEFT);
        chkEnabled.setLayoutData(fd_chkEnabled);
        chkEnabled.setText("&Enabled");
        
        grpPatternContains = new Group(shlUrlPatternMatching, SWT.NONE);
        grpPatternContains.setText("Pattern Contains");
        grpPatternContains.setLayout(new GridLayout(2, false));
        FormData fd_grpPatternContains = new FormData();
        //fd_grpPatternContains.bottom = new FormAttachment(100, -50);
        fd_grpPatternContains.top = new FormAttachment(textPattern, 6);
        fd_grpPatternContains.left = new FormAttachment(lblName, 0, SWT.LEFT);
        fd_grpPatternContains.right = new FormAttachment(100, -10);
        grpPatternContains.setLayoutData(fd_grpPatternContains);
        
        btnWildcards = new Button(grpPatternContains, SWT.RADIO);
        btnWildcards.setSelection(true);
        btnWildcards.setText("Wildcards");
        
        lblExamplemailyahoocom = new Label(grpPatternContains, SWT.NONE);
        lblExamplemailyahoocom.setText("Example: *://mail.yahoo.com/*");
        
        btnRegularExpression = new Button(grpPatternContains, SWT.RADIO);
        btnRegularExpression.setText("Regular Expression");
        
        lblExampleHttpsmailyahoo = new Label(grpPatternContains, SWT.NONE);
        lblExampleHttpsmailyahoo.setText("Example: https?://mail\\.yahoo\\.com\\/.*");
        
        button = new Button(shlUrlPatternMatching, SWT.NONE);
        fd_chkEnabled.top = new FormAttachment(button, 5, SWT.TOP);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onOkSelected();
            }
        });
        button.setText("OK");
        button.setImage(SWTResourceManager.getImage(PatternDlg.class, "/org/daveware/passwordmakerapp/icons/check.png"));
        FormData fd_button = new FormData();
        fd_button.bottom = new FormAttachment(100, -8);
        fd_button.top = new FormAttachment(grpPatternContains, 8);
        fd_button.right = new FormAttachment(100, -10);
        fd_button.width = 90;
        button.setLayoutData(fd_button);
        
        button_1 = new Button(shlUrlPatternMatching, SWT.NONE);
        button_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onCancelSelected();
            }
        });
        button_1.setText("Cancel");
        button_1.setImage(SWTResourceManager.getImage(PatternDlg.class, "/org/daveware/passwordmakerapp/icons/cancel.png"));
        FormData fd_button_1 = new FormData();
        fd_button_1.bottom = new FormAttachment(100, -8);
        fd_button_1.top = new FormAttachment(grpPatternContains, 8);
        fd_button_1.right = new FormAttachment(100, -106);
        fd_button_1.width = 90;
        button_1.setLayoutData(fd_button_1);
        
        lblUrlOrUrl = new Label(shlUrlPatternMatching, SWT.NONE);
        lblUrlOrUrl.setAlignment(SWT.RIGHT);
        FormData fd_lblUrlOrUrl = new FormData();
        fd_lblUrlOrUrl.left = new FormAttachment(lblName, 0, SWT.LEFT);
        fd_lblUrlOrUrl.top = new FormAttachment(textPattern, 3, SWT.TOP);
        
        patternDecoration = new ControlDecoration(textPattern, SWT.LEFT | SWT.TOP);
        patternDecoration.setDescriptionText("An url pattern must be specified.");
        fd_lblUrlOrUrl.right = new FormAttachment(lblName, 0, SWT.RIGHT);
        lblUrlOrUrl.setLayoutData(fd_lblUrlOrUrl);
        lblUrlOrUrl.setText("Url or Url Pattern:");

    }
    
    private void onOkSelected() {
        if(populateDataFromGui()) {
            okClicked = true;
            shlUrlPatternMatching.dispose();
        }
    }
    
    private void onCancelSelected() {
        okClicked = false;
        shlUrlPatternMatching.dispose();
    }
}
