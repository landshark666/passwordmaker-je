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

import java.util.Set;

import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.AccountPatternData;
import org.daveware.passwordmaker.AlgorithmType;
import org.daveware.passwordmaker.CharacterSets;
import org.daveware.passwordmaker.LeetLevel;
import org.daveware.passwordmaker.LeetType;
import org.daveware.passwordmaker.PasswordMaker;
import org.daveware.passwordmaker.SecureCharArray;
import org.daveware.passwordmaker.Utilities;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Spinner;

public class AccountDlg {
    static final String INVALID_ACCOUNT_STRING = "Invalid account";
    static final String INVALID_MPW_STRING = "Invalid master password";
    
	Account account = null;
	AccountPatternData selectedPattern = null;
	SecureCharArray mpw = null;
	PasswordMaker pwm = null;
	
	boolean showPassword = true;
	boolean okClicked = false;
	int lastPasswordStrength = 0;
	
	protected Shell shlAccountSettings;
	private Text textName;
	private Text textNotes;
	private Text textUseUrl;
	private Text textUsername;
	private Text textPrefix;
	private Text textSuffix;
	private Text textModifier;
	private Spinner textPasswordLength;
	private Table tablePatterns;
	private TableViewer tableViewer;
	private Button checkAutoPop;
	private Button btnOk;
	private Button btnCancel;
	private Combo comboUseLeet;
	private Combo comboLeetLevel;
	private CTabFolder tabFolder;
	private Combo comboHashAlgorithm;
	private Combo comboCharacters;
	private CTabItem tbtmUrl;
	private CTabItem tbtmExtended;
	private Composite compositeUrls;
	private Button btnAddPattern;
	private Button btnEditPattern;
	private Button btnCopyPattern;
	private Button btnDeletePattern;
    private ControlDecoration nameDecoration;
    private ControlDecoration lengthDecoration;
    private ControlDecoration charactersDecoration;
    private Canvas canvasOutput;
    private Font pwFont;
    private ProgressBar passwordStrengthMeter;
    
    private Image eyeImage = null;
    private Image eyeClosedImage = null;
    
    //-------- BEGIN RESOURCES THAT MUST BE DISPOSED OF ---------------------------------
    private Image passwordImage = null;
    private Composite composite_1;
    private Label lblGeneratedPassword;
    private Button btnShowPassword;
    private CTabItem tbtmDefaultAccountBehavior;
    private Group grpUrlComponents;
    private Button btnProtocol;
    private Button btnSubdomains;
    private Composite composite_3;
    private Button btnDomain;
    private Button btnPort;
    //-------- End RESOURCES THAT MUST BE DISPOSED OF -----------------------------------

	/**
	 * @wbp.parser.constructor
	 * 
	 * This is used purely for WindowBuilder.
	 */
	private AccountDlg() {
		try {
			AccountDlg window = new AccountDlg();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public AccountDlg(Account acc, SecureCharArray mpw, Font pwFont, PasswordMaker pwm, Image eyeImage, Image eyeClosedImage, boolean showPassword) {
	    this.mpw = mpw;
	    this.showPassword = showPassword;
	    this.pwFont = pwFont;
	    this.pwm = pwm;
	    this.eyeImage = eyeImage;
	    this.eyeClosedImage = eyeClosedImage;
	    
	    account = new Account();
		// edit mode
		if(acc!=null) {
    		account.copySettings(acc);
    		account.setId(acc.getId());
		}
	}

	/**
	 */
	public void wbEntryPoint(String[] args) {
		try {
			AccountDlg window = new AccountDlg();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setupPatternTable() {
		tableViewer.setContentProvider(new AccountPatternModel());
		tableViewer.setLabelProvider(new ITableLabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isLabelProperty(Object arg0, String arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addListener(ILabelProviderListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getColumnText(Object arg0, int col) {
				if(arg0 instanceof AccountPatternData) {
					AccountPatternData data = (AccountPatternData)arg0;
					
					switch(col) {
					case 0:
						return data.isEnabled() ? "Yes" : "No";
					case 1:
						return data.getPattern();
					case 2:
						return data.getDesc();
					case 3:
						return data.getType().toString();
					}
				}
				return "WTF?";
			}
			
			@Override
			public Image getColumnImage(Object arg0, int arg1) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent e) {
                if(e.getSelection().isEmpty()) {
                    selectPattern(null);
                }
                else if(e.getSelection() instanceof IStructuredSelection ){
                    IStructuredSelection selection = (IStructuredSelection)e.getSelection();
                    AccountPatternData data = (AccountPatternData)selection.iterator().next();
                    selectPattern(data);
                }
            }
        });
		tableViewer.setInput(account);
		if(account.getPatterns().size()>0)
		    tableViewer.setSelection(new StructuredSelection(account.getPatterns().get(0)));
		else
		    tableViewer.setSelection(null);
		
		tableViewer.refresh();
	}
	
	private void setupDecorators() {
	    ControlDecoration [] decors = { lengthDecoration, charactersDecoration, nameDecoration };
	    Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
	    for(ControlDecoration dec : decors) {
	        dec.setImage(image);
	        dec.hide();
	    }
	}
	
	/**
	 * Open the window.
	 */
	public Account open() {
		Display display = Display.getDefault();
		createContents();
		
		tabFolder.setSelection(0);
		
		tbtmDefaultAccountBehavior = new CTabItem(tabFolder, SWT.NONE);
		tbtmDefaultAccountBehavior.setText("Default Account Behavior");
		
		grpUrlComponents = new Group(tabFolder, SWT.NONE);
		grpUrlComponents.setText("URL Components");
		tbtmDefaultAccountBehavior.setControl(grpUrlComponents);
		grpUrlComponents.setLayout(new GridLayout(1, false));
		
		composite_3 = new Composite(grpUrlComponents, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		RowLayout rl_composite_3 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_3.spacing = 10;
		composite_3.setLayout(rl_composite_3);
		
		btnProtocol = new Button(composite_3, SWT.CHECK);
		btnProtocol.setText("Protocol");
		
		btnSubdomains = new Button(composite_3, SWT.CHECK);
		btnSubdomains.setText("Subdomain(s)");
		
		btnDomain = new Button(composite_3, SWT.CHECK);
		btnDomain.setText("Domain");
		
		btnPort = new Button(composite_3, SWT.CHECK);
		btnPort.setText("Port, path, anchor, query parameters");
		populateGuiFromAccount();
		setupPatternTable();
		setupDecorators();
		
		if(showPassword) {
		    btnShowPassword.setImage(eyeImage);
		    btnShowPassword.setSelection(true);
		}
		else {
            btnShowPassword.setImage(eyeClosedImage);
            btnShowPassword.setSelection(false);
		}
		
		if(account.isFolder()) {
		    tbtmUrl.dispose();
		    tbtmUrl = null;
		    
		    tbtmExtended.dispose();
		    tbtmExtended = null;
		}
		
		if(account.isDefault()==false) {
		    tbtmDefaultAccountBehavior.dispose();
		    tbtmDefaultAccountBehavior = null;
		}
		else {
            textName.setEditable(false);
            textNotes.setEditable(false);
		}
		
		shlAccountSettings.pack();
		shlAccountSettings.open();
		shlAccountSettings.layout();
		
        textName.setFocus();
        
	
		while (!shlAccountSettings.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		if(okClicked)
		    return account;
		return null;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		// "SHEET" is working for now, not gonna mess with it. I've heard this makes it arrive on the
		// screen in a mac-way on OSX. On windows this seems to properly make it modal.
	    int style = SWT.SHELL_TRIM | SWT.APPLICATION_MODAL;
	    
	    if(Utilities.isMac())
	        style = SWT.SHEET;
	    
		shlAccountSettings = new Shell(Display.getDefault(), style);
		shlAccountSettings.addDisposeListener(new DisposeListener() {
		    public void widgetDisposed(DisposeEvent arg0) {
		        onDisposing();
		    }
		});
		shlAccountSettings.setSize(728, 501);
		shlAccountSettings.setText("Account Settings");
		shlAccountSettings.setLayout(new FormLayout());
		
		btnOk = new Button(shlAccountSettings, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        onOkSelected();
		    }
		});
		btnOk.setImage(SWTResourceManager.getImage(AccountDlg.class, "/org/daveware/passwordmakerapp/icons/check.png"));
		FormData fd_btnOk = new FormData();
		fd_btnOk.height = 32;
		//fd_btnok.top = new FormAttachment(0, 417);
		fd_btnOk.right = new FormAttachment(100, -10);
		fd_btnOk.width = 90;
		fd_btnOk.bottom = new FormAttachment(100, -10);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("OK");
        shlAccountSettings.setDefaultButton(btnOk);
		
		btnCancel = new Button(shlAccountSettings, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        onCancelSelected();
		    }
		});
		btnCancel.setImage(SWTResourceManager.getImage(AccountDlg.class, "/org/daveware/passwordmakerapp/icons/cancel.png"));
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.height = 32;
		fd_btnCancel.top = new FormAttachment(btnOk, 0, SWT.TOP);
		fd_btnCancel.right = new FormAttachment(btnOk, -6);
		fd_btnCancel.width = 90;
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		
		tabFolder = new CTabFolder(shlAccountSettings, SWT.BORDER);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(btnOk, -100);
		fd_tabFolder.top = new FormAttachment(0, 10);
		fd_tabFolder.right = new FormAttachment(btnOk, 0, SWT.RIGHT);
		fd_tabFolder.left = new FormAttachment(0, 10);
		tabFolder.setLayoutData(fd_tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmGeneral = new CTabItem(tabFolder, SWT.NONE);
		tbtmGeneral.setText("General");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmGeneral.setControl(composite);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.horizontalSpacing = 8;
		composite.setLayout(gl_composite);
		
		Label lblName = new Label(composite, SWT.NONE);
		lblName.setAlignment(SWT.RIGHT);
		lblName.setText("Name:");
		
		textName = new Text(composite, SWT.BORDER);
		textName.addModifyListener(new ModifyListener() {
		    public void modifyText(ModifyEvent arg0) {
                updatePasswordStrengthMeter();
		    }
		});

		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		nameDecoration = new ControlDecoration(textName, SWT.LEFT | SWT.TOP);
		nameDecoration.setDescriptionText("You must specify a name for this account.");
		
		Label lblNotes = new Label(composite, SWT.NONE);
		lblNotes.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNotes.setAlignment(SWT.RIGHT);
		lblNotes.setText("Notes:");
		
		textNotes = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		textNotes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tbtmUrl = new CTabItem(tabFolder, SWT.NONE);
		tbtmUrl.setImage(SWTResourceManager.getImage(AccountDlg.class, "/org/daveware/passwordmakerapp/icons/world_link.png"));
		tbtmUrl.setText("URLs");
		
		compositeUrls = new Composite(tabFolder, SWT.NONE);
		tbtmUrl.setControl(compositeUrls);
		compositeUrls.setLayout(new GridLayout(2, false));
		
		Label lblUseTheFollowing = new Label(compositeUrls, SWT.NONE);
		lblUseTheFollowing.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblUseTheFollowing.setText("Use the following URL/text to calculate the generated password:");
		
		textUseUrl = new Text(compositeUrls, SWT.BORDER);
		textUseUrl.addModifyListener(new ModifyListener() {
		    public void modifyText(ModifyEvent arg0) {
		        updatePasswordStrengthMeter();
		    }
		});
		textUseUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		checkAutoPop = new Button(compositeUrls, SWT.CHECK);
		checkAutoPop.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		checkAutoPop.setText("Automatically populate username and password fields for sites that match this URL (Firefox Only)");
		new Label(compositeUrls, SWT.NONE);
		new Label(compositeUrls, SWT.NONE);
		
		Group grpUrlPatterns = new Group(compositeUrls, SWT.NONE);
		grpUrlPatterns.setText("URL Patterns");
		grpUrlPatterns.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpUrlPatterns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		tableViewer = new TableViewer(grpUrlPatterns, SWT.BORDER | SWT.FULL_SELECTION);
		tablePatterns = tableViewer.getTable();
		tablePatterns.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnEnabled = tableViewerColumn.getColumn();
		tblclmnEnabled.setWidth(100);
		tblclmnEnabled.setText("Enabled");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnPattern = tableViewerColumn_1.getColumn();
		tblclmnPattern.setWidth(300);
		tblclmnPattern.setText("Pattern");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnPatternName = tableViewerColumn_2.getColumn();
		tblclmnPatternName.setWidth(100);
		tblclmnPatternName.setText("Pattern Name");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnPatternType = tableViewerColumn_3.getColumn();
		tblclmnPatternType.setWidth(150);
		tblclmnPatternType.setText("Pattern Type");

		Composite compositePatternButtons = new Composite(compositeUrls, SWT.NONE);
		RowLayout rl_compositePatternButtons = new RowLayout(SWT.HORIZONTAL);
		compositePatternButtons.setLayout(rl_compositePatternButtons);
		compositePatternButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		btnAddPattern = new Button(compositePatternButtons, SWT.NONE);
		btnAddPattern.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        onAddPatternSelected();
		    }
		});
		btnAddPattern.setText("&Add Pattern");
		
		btnEditPattern = new Button(compositePatternButtons, SWT.NONE);
		btnEditPattern.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        onEditPatternSelected();
		    }
		});
		btnEditPattern.setText("&Edit Pattern");
		
		btnCopyPattern = new Button(compositePatternButtons, SWT.NONE);
		btnCopyPattern.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        onCopyPatternSelected();
		    }
		});
		btnCopyPattern.setText("&Copy Pattern");
		
		btnDeletePattern = new Button(compositePatternButtons, SWT.NONE);
		btnDeletePattern.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        onDeletePatternSelected();
		    }
		});
		btnDeletePattern.setText("&Delete Pattern");
		
		tbtmExtended = new CTabItem(tabFolder, SWT.NONE);
		tbtmExtended.setImage(SWTResourceManager.getImage(AccountDlg.class, "/org/daveware/passwordmakerapp/icons/small_lock.png"));
		tbtmExtended.setText("Extended");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmExtended.setControl(composite_2);
		GridLayout gl_composite_2 = new GridLayout(5, false);
		gl_composite_2.horizontalSpacing = 7;
		composite_2.setLayout(gl_composite_2);
		
		Label lblUsername = new Label(composite_2, SWT.NONE);
		lblUsername.setAlignment(SWT.RIGHT);
		lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUsername.setText("Username:");
		
		textUsername = new Text(composite_2, SWT.BORDER);
		textUsername.addModifyListener(new ModifyListener() {
		    public void modifyText(ModifyEvent arg0) {
		        updatePasswordStrengthMeter();
		    }
		});
		textUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		Label lblUseLt = new Label(composite_2, SWT.NONE);
		lblUseLt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUseLt.setAlignment(SWT.RIGHT);
		lblUseLt.setText("Use l33t:");
		
		comboUseLeet = new Combo(composite_2, SWT.READ_ONLY);
		comboUseLeet.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        checkLeetLevel();
		        updatePasswordStrengthMeter();
		    }
		});
		comboUseLeet.setItems(new String[] {"not at all", "before generating password", "after generating password", "before and after generating password"});
		comboUseLeet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboUseLeet.select(0);
		new Label(composite_2, SWT.NONE);
		
		Label lblLtLevel = new Label(composite_2, SWT.NONE);
		lblLtLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLtLevel.setText("l33t Level:");
		
		comboLeetLevel = new Combo(composite_2, SWT.READ_ONLY);
		comboLeetLevel.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        updatePasswordStrengthMeter();
		    }
		});
		comboLeetLevel.setEnabled(false);
		comboLeetLevel.setItems(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9"});
		comboLeetLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboLeetLevel.select(0);
		
		Label lblHashAlgorithm = new Label(composite_2, SWT.NONE);
		lblHashAlgorithm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHashAlgorithm.setAlignment(SWT.RIGHT);
		lblHashAlgorithm.setText("Hash Algorithm:");
		
		comboHashAlgorithm = new Combo(composite_2, SWT.READ_ONLY);
		comboHashAlgorithm.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        updatePasswordStrengthMeter();
		    }
		});
		comboHashAlgorithm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblPasswordLength = new Label(composite_2, SWT.NONE);
		lblPasswordLength.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPasswordLength.setAlignment(SWT.RIGHT);
		lblPasswordLength.setText("Password Length:");
		
		textPasswordLength = new Spinner(composite_2, SWT.BORDER);
		textPasswordLength.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textPasswordLength.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                updatePasswordStrengthMeter();
            }
        });
		
		lengthDecoration = new ControlDecoration(textPasswordLength, SWT.LEFT | SWT.TOP);
		lengthDecoration.setDescriptionText("The password length must be at least 1.");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblCharacters = new Label(composite_2, SWT.NONE);
		lblCharacters.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCharacters.setAlignment(SWT.RIGHT);
		lblCharacters.setText("Characters:");
		
		comboCharacters = new Combo(composite_2, SWT.NONE);
		comboCharacters.addModifyListener(new ModifyListener() {
		    public void modifyText(ModifyEvent arg0) {
		        updatePasswordStrengthMeter();
		    }
		});
		comboCharacters.setItems(CharacterSets.CHARSETS);
		GridData gd_comboCharacters = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_comboCharacters.widthHint = 200;
		comboCharacters.setLayoutData(gd_comboCharacters);
		
		charactersDecoration = new ControlDecoration(comboCharacters, SWT.LEFT | SWT.TOP);
		charactersDecoration.setDescriptionText("There must be at least 2 characters.");
		comboCharacters.select(0);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblModifier = new Label(composite_2, SWT.NONE);
		lblModifier.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblModifier.setAlignment(SWT.RIGHT);
		lblModifier.setText("Modifier:");
		
		textModifier = new Text(composite_2, SWT.BORDER);
		textModifier.addModifyListener(new ModifyListener() {
		    public void modifyText(ModifyEvent arg0) {
		        updatePasswordStrengthMeter();
		    }
		});
		textModifier.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblPasswordPrefix = new Label(composite_2, SWT.NONE);
		lblPasswordPrefix.setAlignment(SWT.RIGHT);
		lblPasswordPrefix.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPasswordPrefix.setText("Password Prefix:");
		
		textPrefix = new Text(composite_2, SWT.BORDER);
		textPrefix.addModifyListener(new ModifyListener() {
		    public void modifyText(ModifyEvent arg0) {
		        updatePasswordStrengthMeter();
		    }
		});
		textPrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblPasswordSuffix = new Label(composite_2, SWT.NONE);
		lblPasswordSuffix.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPasswordSuffix.setText("Password Suffix:");
		
		textSuffix = new Text(composite_2, SWT.BORDER);
		textSuffix.addModifyListener(new ModifyListener() {
		    public void modifyText(ModifyEvent arg0) {
		        updatePasswordStrengthMeter();
		    }
		});
		textSuffix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Group grpPasswordDetails = new Group(shlAccountSettings, SWT.NONE);
		grpPasswordDetails.setText("Password Details");
		grpPasswordDetails.setLayout(new GridLayout(2, false));
		FormData fd_grpPasswordDetails = new FormData();
		fd_grpPasswordDetails.bottom = new FormAttachment(btnOk, -6);
		fd_grpPasswordDetails.top = new FormAttachment(tabFolder, 6);
		fd_grpPasswordDetails.right = new FormAttachment(btnOk, 0, SWT.RIGHT);
		fd_grpPasswordDetails.left = new FormAttachment(0, 10);
		grpPasswordDetails.setLayoutData(fd_grpPasswordDetails);
		
		Label lblPasswordStrength = new Label(grpPasswordDetails, SWT.NONE);
		lblPasswordStrength.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPasswordStrength.setText("Password Strength:");
		
		passwordStrengthMeter = new ProgressBar(grpPasswordDetails, SWT.NONE);
		passwordStrengthMeter.setState(SWT.ERROR);
		passwordStrengthMeter.addControlListener(new ControlAdapter() {
		    @Override
		    public void controlResized(ControlEvent arg0) {
		        passwordStrengthMeter.setSelection(lastPasswordStrength);
		    }
		});
		passwordStrengthMeter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		composite_1 = new Composite(grpPasswordDetails, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);
		
		lblGeneratedPassword = new Label(composite_1, SWT.RIGHT);
		lblGeneratedPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGeneratedPassword.setText("Generated Password:");
		
		btnShowPassword = new Button(composite_1, SWT.FLAT | SWT.TOGGLE);
		btnShowPassword.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent arg0) {
		        onShowPasswordClicked();
		    }
		});
		GridData gd_btnShowPassword = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnShowPassword.widthHint = 20;
		gd_btnShowPassword.heightHint = 17;
		btnShowPassword.setLayoutData(gd_btnShowPassword);
		btnShowPassword.setToolTipText("Toggles the visibility of the generated password.");
		btnShowPassword.setImage(null);
		
		canvasOutput = new Canvas(grpPasswordDetails, SWT.BORDER);
		canvasOutput.addPaintListener(new PaintListener() {
		    public void paintControl(PaintEvent arg0) {
                if(passwordImage!=null) {
                    arg0.gc.drawImage(passwordImage, 0, 0);
                }
		    }
		});
		canvasOutput.addControlListener(new ControlAdapter() {
		    @Override
		    public void controlResized(ControlEvent arg0) {
                if(passwordImage!=null)
                    passwordImage.dispose();
                passwordImage = new Image(Display.getCurrent(), canvasOutput.getClientArea());
                updatePasswordStrengthMeter();
		    }
		});
		GridData gd_canvas = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_canvas.heightHint = 33;
		canvasOutput.setLayoutData(gd_canvas);

	}
	
    SecureCharArray generateOutput() {
        SecureCharArray output = null;
        
        try {
            if(account!=null) {
                output = pwm.makePassword(mpw, account);
            }
            else {
                output = new SecureCharArray();
            }
        }
        catch(Exception e) {}
        finally {
        }
        
        return output;
    }
    
	void onAddPatternSelected() {
	    int style = Utilities.isMac() ? SWT.SHEET : SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL;
        PatternDlg dlg = new PatternDlg(shlAccountSettings, style, new AccountPatternData());
        AccountPatternData newData = dlg.open();
        
        if(newData!=null) {
            account.getPatterns().add(newData);
            tableViewer.refresh();
        }
    }

    void onCancelSelected() {
	    okClicked = false;
	    shlAccountSettings.dispose();
	}
	
	void onCopyPatternSelected() {
	    if(selectedPattern==null)
	        return;
	    
	    AccountPatternData newData = new AccountPatternData(selectedPattern);
	    account.getPatterns().add(newData);
	    tableViewer.refresh();
	    tableViewer.setSelection(new StructuredSelection(newData));
	}
	
	void onDeletePatternSelected() {
	    if(selectedPattern==null)
	        return;
	    
	    if(MBox.showYesNo(shlAccountSettings, "Are you sure you want to delete pattern '" + selectedPattern.getDesc() + "'?")==SWT.YES) {
	        int index = account.getPatterns().indexOf(selectedPattern);
	        account.getPatterns().remove(selectedPattern);
	        
	        int size = account.getPatterns().size();
	        if(size>0) {
	            if(index>=size)
	                tableViewer.setSelection(new StructuredSelection(account.getPatterns().get(size-1)));
	            else
	                tableViewer.setSelection(new StructuredSelection(account.getPatterns().get(index)));
	        }
	        else
	            tableViewer.setSelection(null);

	        tableViewer.refresh();
	    }
	}
	
	void onDisposing() {
	    // Don't erase as it belongs to the calling object
	    mpw = null;
	    
	    if(passwordImage!=null) {
	        passwordImage.dispose();
	        passwordImage = null;
	    }
	}
	
	void onEditPatternSelected() {
	    if(selectedPattern==null)
	        return;
	    
	    AccountPatternData newData;
	    int style = Utilities.isMac() ? SWT.SHEET : SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL;
	    PatternDlg dlg = new PatternDlg(shlAccountSettings, style, selectedPattern);
	    
	    newData = dlg.open();
	    if(newData!=null) {
	        selectedPattern.copyFrom(newData);
	        tableViewer.refresh(selectedPattern);
	    }
	}
	
	void onOkSelected() {
	    if(populateAccountFromGui()) {
	        okClicked = true;
	        shlAccountSettings.dispose();
	    }
	}
	
    private void onShowPasswordClicked() {
        showPassword = btnShowPassword.getSelection();
        if(showPassword)
            btnShowPassword.setImage(eyeImage);
        else
            btnShowPassword.setImage(eyeClosedImage);
        
        updatePasswordStrengthMeter();
    }
	
	private boolean populateAccountFromGui() {
        // General page
        if(textName.getText().trim().length()>0) {
            account.setName(textName.getText());
            nameDecoration.hide();
        }
        else {
            nameDecoration.show();
            textName.setFocus();
            return false;
        }
        
        account.setDesc(textNotes.getText());
        
        // For folders, the 2 tabs which contain the rest of the items have already been
        // disposed of.  Use of their widgets will cause SWT exceptions.
        if(account.isFolder())
            return true;
        
        // URLs page
        account.setUrl(textUseUrl.getText());
        account.setAutoPop(checkAutoPop.getSelection());
        
        // Extended page
        account.setUsername(textUsername.getText());
        switch(comboUseLeet.getSelectionIndex()) {
            case 0: account.setLeetType(LeetType.NONE); break;
            case 1: account.setLeetType(LeetType.BEFORE); break;
            case 2: account.setLeetType(LeetType.AFTER); break;
            case 3: account.setLeetType(LeetType.BOTH); break;
            default: account.setLeetType(LeetType.NONE); break;
        }
        // LeetLevel objects are from 1-9. Since the index is 0-based, add 1.
        account.setLeetLevel(LeetLevel.fromInt(comboLeetLevel.getSelectionIndex() + 1));
        int selectedAlgo = comboHashAlgorithm.getSelectionIndex();
        account.setAlgorithm(AlgorithmType.getTypes()[selectedAlgo/2]);
        account.setHmac((selectedAlgo & 1)!=0);
        
        if(textPasswordLength.getSelection()>0) {
            lengthDecoration.hide();
            account.setLength(textPasswordLength.getSelection());
        }
        else {
            lengthDecoration.show();
            return false;
        }
        
        if(comboCharacters.getText().length()>2) {
            account.setCharacterSet(comboCharacters.getText());
            charactersDecoration.hide();
        } else {
            charactersDecoration.show();
            comboCharacters.setFocus();
            return false;
        }
           
        account.setModifier(textModifier.getText());
        account.setPrefix(textPrefix.getText());
        account.setSuffix(textSuffix.getText());
        
        if(account.isDefault()) {
            account.clearUrlComponents();
            if(btnProtocol.getSelection())
                account.addUrlComponent(Account.UrlComponents.Protocol);
            if(btnSubdomains.getSelection())
                account.addUrlComponent(Account.UrlComponents.Subdomain);
            if(btnDomain.getSelection())
                account.addUrlComponent(Account.UrlComponents.Domain);
            if(btnPort.getSelection())
                account.addUrlComponent(Account.UrlComponents.PortPathAnchorQuery);
        }
        
        return true;
    }

    private void populateGuiFromAccount() {
    	if(account==null)
    		return;
    	
    	// General page
    	textName.setText(account.getName());
    	textNotes.setText(account.getDesc());
    	
    	if(account.isFolder()) {
    	    shlAccountSettings.setText("Folder Settings");
    	    return;
    	}
    	
        // URLs page
        textUseUrl.setText(account.getUrl());
        checkAutoPop.setSelection(account.isAutoPop());
        
    	// Extended page
    	textUsername.setText(account.getUsername());
    	if(account.getLeetType()==LeetType.NONE) {
    		comboUseLeet.select(0);
    		comboLeetLevel.setEnabled(false);
    	}
    	else {
    		if(account.getLeetType()==LeetType.BEFORE)
    			comboUseLeet.select(1);
    		else if(account.getLeetType()==LeetType.AFTER)
    			comboUseLeet.select(2);
    		else if(account.getLeetType()==LeetType.BOTH)
    			comboUseLeet.select(3);
    		
    		comboLeetLevel.setEnabled(true);
    		comboLeetLevel.select(account.getLeetLevel().getLevel()-1);
    	}
    	
    	// Populate the algorithms
    	for(AlgorithmType type : AlgorithmType.getTypes()) {
    		comboHashAlgorithm.add(type.getName());
    		comboHashAlgorithm.add("HMAC-" + type.getName());
    	}
    	comboHashAlgorithm.select((account.getAlgorithm().getType()-1) * 2 +(account.isHmac()?1:0));
    	textPasswordLength.setMaximum(10000);
    	textPasswordLength.setMinimum(1);
    	
    	textPasswordLength.setSelection(8);
    
    	comboCharacters.add(account.getCharacterSet(), 0);
    	comboCharacters.select(0);
    
    	textModifier.setText(account.getModifier());
    	textPrefix.setText(account.getPrefix());
    	textSuffix.setText(account.getSuffix());
    	
    	if(account.isDefault()) {
    	    Set<Account.UrlComponents> urlComponents = account.getUrlComponents();
    	    btnProtocol.setSelection(urlComponents.contains(Account.UrlComponents.Protocol));
    	    btnSubdomains.setSelection(urlComponents.contains(Account.UrlComponents.Subdomain));
    	    btnDomain.setSelection(urlComponents.contains(Account.UrlComponents.Domain));
    	    btnPort.setSelection(urlComponents.contains(Account.UrlComponents.PortPathAnchorQuery));
    	}
    }

    void selectPattern(AccountPatternData data) {
	 // Store the newly selected account
        selectedPattern = data;
        if(data!=null) {
            btnEditPattern.setEnabled(true);
            btnDeletePattern.setEnabled(true);
            btnCopyPattern.setEnabled(true);
        }
        else {
            btnEditPattern.setEnabled(false);
            btnDeletePattern.setEnabled(false);
            btnCopyPattern.setEnabled(false);
        }
	}
    
    void updatePasswordStrengthMeter() {
        SecureCharArray output = null;
        GC gc = null;

        try {
            gc = new GC(passwordImage);
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
            gc.fillRectangle(canvasOutput.getClientArea());
            gc.setFont(pwFont);
            
            if(mpw.size()==0) {
                gc.drawText(INVALID_MPW_STRING, 5, 5);
            }
            else if(populateAccountFromGui()) {
                output = generateOutput();
                if(output!=null) {
                    lastPasswordStrength = (int)PasswordMaker.calcPasswordStrength(output);

                    if(showPassword==true) {
                        int x = 0;
                        int xPos = 5;
                        int yPos = 2;
                        
                        if(Utilities.isMac())
                        	yPos = 7;
                        for(x=0; x<output.getData().length; x++) {
                            char strBytes [] = { output.getData()[x] };
                            String str = new String(strBytes);
                            gc.drawText(str, xPos, yPos);
                            xPos += gc.stringExtent(str).x + 2;
                        }
                    }
                }
                else {
                    gc.drawText(INVALID_ACCOUNT_STRING, 5, 5);
                }
            }
            else {
                gc.drawText(INVALID_ACCOUNT_STRING, 5, 5);
            }
            
            canvasOutput.redraw();
        }
        catch(Exception e) {}
        finally {
            if(output!=null)
                output.erase();
            if(gc!=null)
                gc.dispose();
        }

        // This can be invoked prioer to the password strength meter getting created
        if(passwordStrengthMeter!=null)
            passwordStrengthMeter.setSelection(lastPasswordStrength);
    }
    
    /**
     * Warns the user if LeetType::BOTH or LeetType::AFTER are selected since this 
     * severely limits the number of characters used to generate the password.
     */
    private void checkLeetLevel() {
        int leetTypeIndex = comboUseLeet.getSelectionIndex();
        comboLeetLevel.setEnabled(leetTypeIndex>0);
        if(leetTypeIndex>0) {
            if(leetTypeIndex==2 || leetTypeIndex==3) {
                MBox.showWarning(shlAccountSettings, "Warning: this type of l33t may place special\n" +
                        "characters in the generated password which are\n" +
                        "not in the list of characters you've defined in\n" +
                        "the characters field. It also limits the number of\n" +
                        "characters available in the password\n\n" +
                        "Its use is not recommended!");
            }
        }
    }
}
