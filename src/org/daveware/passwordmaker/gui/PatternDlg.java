package org.daveware.passwordmaker.gui;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

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
        shlUrlPatternMatching.open();
        shlUrlPatternMatching.layout();
        
        populateGuiFromData();
        
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
            textName.setFocus();
            return false;
        }
        patternData.setDesc(textName.getText());
        
        if(textPattern.getText().length()<=0) {
            textPattern.setFocus();
            return false;
        }
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

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shlUrlPatternMatching = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE);
        shlUrlPatternMatching.setMinimumSize(new Point(450, 234));
        shlUrlPatternMatching.setSize(450, 234);
        shlUrlPatternMatching.setText("URL Pattern Matching Data");
        shlUrlPatternMatching.setLayout(new FormLayout());
        
        lblName = new Label(shlUrlPatternMatching, SWT.NONE);
        lblName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        FormData fd_lblName = new FormData();
        fd_lblName.top = new FormAttachment(0, 10);
        fd_lblName.left = new FormAttachment(0, 10);
        lblName.setLayoutData(fd_lblName);
        lblName.setAlignment(SWT.RIGHT);
        lblName.setText("Pattern Name:");
        
        textName = new Text(shlUrlPatternMatching, SWT.BORDER);
        fd_lblName.right = new FormAttachment(textName, -6);
        FormData fd_textName = new FormData();
        fd_textName.left = new FormAttachment(0, 125);
        fd_textName.right = new FormAttachment(100, -10);
        fd_textName.top = new FormAttachment(0, 7);
        textName.setLayoutData(fd_textName);
        
        textPattern = new Text(shlUrlPatternMatching, SWT.BORDER);
        FormData fd_textPattern = new FormData();
        fd_textPattern.top = new FormAttachment(0, 34);
        fd_textPattern.left = new FormAttachment(0, 125);
        fd_textPattern.right = new FormAttachment(100, -10);
        textPattern.setLayoutData(fd_textPattern);
        
        chkEnabled = new Button(shlUrlPatternMatching, SWT.CHECK);
        chkEnabled.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        FormData fd_chkEnabled = new FormData();
        fd_chkEnabled.top = new FormAttachment(textPattern, 13);
        fd_chkEnabled.left = new FormAttachment(textName, 0, SWT.LEFT);
        chkEnabled.setLayoutData(fd_chkEnabled);
        chkEnabled.setText("&Enabled");
        
        grpPatternContains = new Group(shlUrlPatternMatching, SWT.NONE);
        grpPatternContains.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        grpPatternContains.setText("Pattern Contains");
        grpPatternContains.setLayout(new GridLayout(2, false));
        FormData fd_grpPatternContains = new FormData();
        fd_grpPatternContains.top = new FormAttachment(0, 90);
        fd_grpPatternContains.right = new FormAttachment(100, -10);
        fd_grpPatternContains.left = new FormAttachment(0, 10);
        grpPatternContains.setLayoutData(fd_grpPatternContains);
        
        btnWildcards = new Button(grpPatternContains, SWT.RADIO);
        btnWildcards.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        btnWildcards.setText("Wildcards");
        
        lblExamplemailyahoocom = new Label(grpPatternContains, SWT.NONE);
        lblExamplemailyahoocom.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblExamplemailyahoocom.setText("Example: *://mail.yahoo.com/*");
        
        btnRegularExpression = new Button(grpPatternContains, SWT.RADIO);
        btnRegularExpression.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        btnRegularExpression.setText("Regular Expression");
        
        lblExampleHttpsmailyahoo = new Label(grpPatternContains, SWT.NONE);
        lblExampleHttpsmailyahoo.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblExampleHttpsmailyahoo.setText("Example: https?://mail\\.yahoo\\.com\\/.*");
        
        button = new Button(shlUrlPatternMatching, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onOkSelected();
            }
        });
        button.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        button.setText("OK");
        button.setImage(SWTResourceManager.getImage(PatternDlg.class, "/org/daveware/passwordmaker/icons/check.png"));
        FormData fd_button = new FormData();
        fd_button.bottom = new FormAttachment(100, -5);
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
        button_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        button_1.setText("Cancel");
        button_1.setImage(SWTResourceManager.getImage(PatternDlg.class, "/org/daveware/passwordmaker/icons/cancel.png"));
        FormData fd_button_1 = new FormData();
        fd_button_1.bottom = new FormAttachment(100, -5);
        fd_button_1.right = new FormAttachment(100, -106);
        fd_button_1.width = 90;
        button_1.setLayoutData(fd_button_1);
        
        lblUrlOrUrl = new Label(shlUrlPatternMatching, SWT.NONE);
        lblUrlOrUrl.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblUrlOrUrl.setAlignment(SWT.RIGHT);
        FormData fd_lblUrlOrUrl = new FormData();
        fd_lblUrlOrUrl.left = new FormAttachment(lblName, 0, SWT.LEFT);
        fd_lblUrlOrUrl.top = new FormAttachment(textPattern, 3, SWT.TOP);
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
