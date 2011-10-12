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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.CmdLineSettings;
import org.daveware.passwordmaker.Database;
import org.daveware.passwordmaker.DatabaseListener;
import org.daveware.passwordmaker.GlobalSettingKey;
import org.daveware.passwordmaker.PasswordMaker;
import org.daveware.passwordmaker.RDFDatabaseReader;
import org.daveware.passwordmaker.RDFDatabaseWriter;
import org.daveware.passwordmaker.SecureCharArray;
import org.daveware.passwordmaker.Utilities;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * Implements the main window for PasswordMakerJE.
 * 
 * TODO: this should really be using validation, but I'm yet to figure out how to make it
 * work properly.  I can't find the damned fieldassist package of jface.
 * 
 * @author Dave Marotti
 */
public class GuiMain implements DatabaseListener {
	// Text used in the account-filter box by default
    private final static String ACCOUNT_FILTER_DESC = "type filter text";
    private final static String TITLE_STRING = "PasswordMaker Java Edition";
    
    private final static String EXIT_PROMPT = "Your current passwords and/or settings have been modified, would you like to save?";

    protected Shell shlPasswordMaker;
    private Text editAccount;
    private Text editDesc;
    private Text editUsername;
    private Text editMP;
    private Text editCopySeconds;
    private Text editUrl;
    private Canvas canvasOutput;
    private Display display;
    private Combo comboCopyBehavior;
    private CLabel filterIcon;
    private Tree accountTree;
    private TreeViewer accountTreeViewer;
    private Button btnCopyToClipboard;
    private MenuItem menuItemNewAccount;
    private MenuItem menuItemEditAccount;
    private MenuItem menuItemDeleteAccount;
    private Text accountFilterText;
    private Button btnShowPassword;
    private Menu menu_1;
    private MenuItem mntmhelp;
    private Menu menu_2;
    private MenuItem mntmabout;
    private MenuItem mntmFile;
    private Menu menu_3;
    private MenuItem menuItemNew;
    private MenuItem menuItemOpen;
    private MenuItem menuItemExit;
    private MenuItem menuItemNewGroup;
    private MenuItem menuItemEditGroup;
    private MenuItem menuItemDeleteGroup;
    private MenuItem menuItemSave;
    private MenuItem menuItemSaveAs;
    private Text textFilename;
    private ControlDecoration secondsDecoration;
    
    
    //-------- BEGIN RESOURCES THAT MUST BE DISPOSED OF ---------------------------------
    private Image searchImage = null;
    private Image cancelImage = null;
    private Image passwordImage = null;
    private Image eyeImage = null;
    private Image eyeClosedImage = null;
    
    private Font passwordFont = null;
    
    // This one does need to be disposed of
    private Font regularSearchFont = null;

    private AccountTreeModel accountTreeModel = new AccountTreeModel();
    private AccountTreeLabelProvider accountTreeLabelProvider = new AccountTreeLabelProvider();
    //-------- End RESOURCES THAT MUST BE DISPOSED OF -----------------------------------

    // Do NOT dispose of this font, this is the original obtained from the widget
    private Font italicsSearchFont = null;
    
    
    private CmdLineSettings cmdLineSettings;
    private Account selectedAccount = null;
    private PasswordMaker pwm = null;
    private Database db = null;
    
    private boolean isFiltering = false;
    private boolean showPassword = true;

    private boolean closeAfterTimer = false;

    
    public GuiMain(CmdLineSettings c) {
        cmdLineSettings = c;
    }
    
    /**
     * Causes everything to happen. This is called by the calling class.
     * @return
     */
    public int run() {
        open();
        return 0;
    }
    
    /**
     * Open the window, builds the UI, and runs the event loop.
     * @wbp.parser.entryPoint
     * 
     */
    private void open() {
        display = Display.getDefault();
        createContents();
        
        setupFonts();
        setupTree();
        setupDecorators();
        
        shlPasswordMaker.open();
        shlPasswordMaker.layout();

        pwm = new PasswordMaker();
        loadFromCmdLineSettings();
        
        regeneratePasswordAndDraw();

        while (!shlPasswordMaker.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
    
    protected void setupDecorators() {
        ControlDecoration [] decors = { secondsDecoration };
        Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
        for(ControlDecoration dec : decors) {
            dec.setImage(image);
            dec.hide();
        }
    }
    
    protected void setupFonts() {
        // Clone the font of the accountFilterText widget
        italicsSearchFont = accountFilterText.getFont();
        FontData [] fd = italicsSearchFont.getFontData();
        for(FontData d : fd) {
        	d.setStyle(d.getStyle() & ~SWT.ITALIC);
        }
        regularSearchFont = new Font(display, fd);
        
        passwordFont = new Font(Display.getCurrent(), "Segoe UI", 12, SWT.BOLD);
    }
    
    protected void setupTree() {
        // The tree must have a brain!
        accountTreeLabelProvider.loadImages();
        accountTreeViewer.setContentProvider(accountTreeModel);
        accountTreeViewer.setLabelProvider(accountTreeLabelProvider);
        
        accountTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				if(e.getSelection().isEmpty()) {
					selectAccount(null);
				}
				else if(e.getSelection() instanceof IStructuredSelection ){
					IStructuredSelection selection = (IStructuredSelection)e.getSelection();
					Account account = (Account)selection.iterator().next();
					selectAccount(account);
				}
			}
		});
        
        accountTreeViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if(isFiltering==false)
					return true;
				if(element==null)
					return false;
				Account account = (Account)element;
				// Never filter out parents
				if(account.getChildren().size()>0)
					return true;
				
				String name = account.getName().toLowerCase();
				String text = accountFilterText.getText().toLowerCase();
				return name.contains(text);
			}
		});
    }
    
    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shlPasswordMaker = new Shell();
        shlPasswordMaker.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent arg0) {
                onDisposing(arg0);
            }
        });
        shlPasswordMaker.addListener(SWT.Close, new Listener() {

            @Override
            public void handleEvent(Event arg0) {
                onCloseWindow(arg0);
            }
            
        });
        
        searchImage = SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/magglass.png");
        cancelImage = SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/cancel.png");
        eyeImage = SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/eye.png");
        eyeClosedImage = SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/eye_closed.png");

        shlPasswordMaker.setMinimumSize(new Point(795, 345));
        shlPasswordMaker.setSize(795, 345);
        shlPasswordMaker.setText(TITLE_STRING);
        shlPasswordMaker.setLayout(new FormLayout());
        
        Sash sash = new Sash(shlPasswordMaker, SWT.VERTICAL);
        Group grpAccounts = new Group(shlPasswordMaker, SWT.NONE);
        grpAccounts.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        FormData fd_grpAccounts = new FormData();
        fd_grpAccounts.bottom = new FormAttachment(100, -10);
        fd_grpAccounts.top = new FormAttachment(0, 5);
        fd_grpAccounts.left = new FormAttachment(0, 5);
        fd_grpAccounts.right = new FormAttachment(sash);
        grpAccounts.setLayoutData(fd_grpAccounts);
        grpAccounts.setText("Accounts");
        grpAccounts.setLayout(new GridLayout(1, false));
        
        Composite composite = new Composite(grpAccounts, SWT.NONE);
        composite.addControlListener(new ControlAdapter() {
        	@Override
        	public void controlResized(ControlEvent arg0) {
        		onFilterCompositeResized(arg0);
        	}
        });
        composite.setLayout(null);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        
        filterIcon = new CLabel(composite, SWT.NONE);
        filterIcon.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseUp(MouseEvent arg0) {
        		onFilterIconClicked();
        	}
        });
        filterIcon.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
        filterIcon.setImage(SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/magglass.png"));
        filterIcon.setBounds(187, 1, 22, 20);
        filterIcon.setText("");
        
        accountFilterText = new Text(composite, SWT.BORDER);
        accountFilterText.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent arg0) {
        		onFilterModified(arg0);
        	}
        });
        accountFilterText.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
        accountFilterText.setText("type filter text");
        accountFilterText.setBounds(0, 0, 209, 23);
        accountFilterText.addFocusListener(new FocusAdapter() {
        	@Override
        	public void focusGained(FocusEvent arg0) {
        		onAccountFilterTextFocusGained();
        	}
        	@Override
        	public void focusLost(FocusEvent arg0) {
        		onAccountFilterTextFocusLost();
        	}
        });
        
        final FormData sashData = new FormData();
        sashData.left = new FormAttachment(30);
        sashData.top = new FormAttachment(0);
        sashData.bottom = new FormAttachment(100);
        sash.setLayoutData(sashData);
        sash.addListener(SWT.Selection, new Listener() {
        	public void handleEvent(Event event) {
        		if(event.detail!=SWT.DRAG){
        			sashData.left = new FormAttachment(0, event.x);
        			shlPasswordMaker.layout();
        		}
        	}
        });
        Group grpInput = new Group(shlPasswordMaker, SWT.NONE);
        grpInput.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        grpInput.setText("Password Input && Search");
        FormData fd_grpInput = new FormData();
        fd_grpInput.bottom = new FormAttachment(grpAccounts, 0, SWT.BOTTOM);
        fd_grpInput.top = new FormAttachment(0, 5);
        fd_grpInput.right = new FormAttachment(100, -5);
        fd_grpInput.left = new FormAttachment(sash);
        grpInput.setLayoutData(fd_grpInput);
        GridLayout gl_grpInput = new GridLayout(2, false);
        grpInput.setLayout(gl_grpInput);
        
        Label lblDatabase = new Label(grpInput, SWT.NONE);
        lblDatabase.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblDatabase.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDatabase.setText("File:");
        
        textFilename = new Text(grpInput, SWT.BORDER);
        textFilename.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        textFilename.setEditable(false);
        textFilename.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        
        Label lblUrl = new Label(grpInput, SWT.NONE);
        lblUrl.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblUrl.setText("URL Search:");
        
        editUrl = new Text(grpInput, SWT.BORDER);
        editUrl.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        editUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        editUrl.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent arg0) {
        		onUrlModified(arg0);
        	}
        });
        
        Label lblAccount = new Label(grpInput, SWT.RIGHT);
        lblAccount.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblAccount.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblAccount.setText("Account:");
        
        editAccount = new Text(grpInput, SWT.BORDER);
        editAccount.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        editAccount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        editAccount.setEnabled(false);
        editAccount.setEditable(false);
        
        Label lblDescription = new Label(grpInput, SWT.RIGHT);
        lblDescription.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        lblDescription.setText("Description:");
        
        editDesc = new Text(grpInput, SWT.BORDER | SWT.V_SCROLL);
        editDesc.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        editDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        editDesc.setEnabled(false);
        editDesc.setEditable(false);
        
        Label lblUsername = new Label(grpInput, SWT.RIGHT);
        lblUsername.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblUsername.setText("Username:");
        
        editUsername = new Text(grpInput, SWT.BORDER);
        editUsername.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        editUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        Label lblMasterPw = new Label(grpInput, SWT.RIGHT);
        lblMasterPw.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblMasterPw.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblMasterPw.setText("Master PW:");
        
        editMP = new Text(grpInput, SWT.BORDER | SWT.PASSWORD);
        editMP.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        editMP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        editMP.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                regeneratePasswordAndDraw();
            }
        });
        
        Composite composite_1 = new Composite(grpInput, SWT.NONE);
        GridLayout gl_composite_1 = new GridLayout(1, false);
        gl_composite_1.marginHeight = 0;
        gl_composite_1.marginWidth = 0;
        gl_composite_1.verticalSpacing = 0;
        gl_composite_1.horizontalSpacing = 0;
        composite_1.setLayout(gl_composite_1);
        GridData gd_composite_1 = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
        gd_composite_1.heightHint = 33;
        composite_1.setLayoutData(gd_composite_1);
        
        Label lblGenerated = new Label(composite_1, SWT.RIGHT);
        lblGenerated.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblGenerated.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblGenerated.setText("Generated:");
        
        btnShowPassword = new Button(composite_1, SWT.FLAT | SWT.TOGGLE);
        btnShowPassword.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent arg0) {
        		onShowPasswordClicked();
        	}
        });
        btnShowPassword.setToolTipText("Toggles the visibility of the generated password.");
        GridData gd_btnShowPassword = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gd_btnShowPassword.widthHint = 20;
        gd_btnShowPassword.heightHint = 17;
        btnShowPassword.setLayoutData(gd_btnShowPassword);
        btnShowPassword.setImage(SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/eye.png"));
        
        Canvas outputCanvas = new Canvas(grpInput, SWT.BORDER);
        outputCanvas.setLayout(new FillLayout(SWT.HORIZONTAL));
        outputCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        outputCanvas.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                if(passwordImage!=null)
                    passwordImage.dispose();
                passwordImage = new Image(Display.getCurrent(), canvasOutput.getClientArea());
                regeneratePasswordAndDraw();
            }
            
        });
        outputCanvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent arg0) {
                if(passwordImage!=null) {
                    arg0.gc.drawImage(passwordImage, 0, 0);
                }
            }
        });
        canvasOutput = outputCanvas;
        new Label(grpInput, SWT.NONE);
        
        Composite compositeButtons = new Composite(grpInput, SWT.NONE);
        GridLayout gl_compositeButtons = new GridLayout(4, false);
        gl_compositeButtons.horizontalSpacing = 7;
        gl_compositeButtons.verticalSpacing = 0;
        gl_compositeButtons.marginWidth = 0;
        gl_compositeButtons.marginHeight = 0;
        compositeButtons.setLayout(gl_compositeButtons);
        
        btnCopyToClipboard = new Button(compositeButtons, SWT.NONE);
        btnCopyToClipboard.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        btnCopyToClipboard.setToolTipText("Copies the generated password to the clipboard and then based on the combobox next to the button:\r\n\r\n\"erase clipboard in\"\r\nClears the clipboard after X seconds.\r\n\r\n\"close PasswordMakerJE in\"\r\nSame thing as above but also closes PasswordMakerJE after X seconds.");
        btnCopyToClipboard.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                onCopyToClipboard();
            }
        });
        btnCopyToClipboard.setText("Copy to clipboard, then");
        
        comboCopyBehavior = new Combo(compositeButtons, SWT.READ_ONLY);
        comboCopyBehavior.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        comboCopyBehavior.setToolTipText("");
        comboCopyBehavior.setItems(new String[] {"erase clipboard in", "close app and erase clipboard in"});
        comboCopyBehavior.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboCopyBehavior.select(0);
        
        editCopySeconds = new Text(compositeButtons, SWT.BORDER);
        editCopySeconds.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent arg0) {
                onSecondsFocusLost();
            }
        });
        editCopySeconds.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        editCopySeconds.setText("5");
        GridData gd_editCloseSeconds = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_editCloseSeconds.widthHint = 33;
        editCopySeconds.setLayoutData(gd_editCloseSeconds);
        
        secondsDecoration = new ControlDecoration(editCopySeconds, SWT.LEFT | SWT.TOP);
        secondsDecoration.setDescriptionText("The number of seconds must be a positive value");
        
        Label lblSeconds = new Label(compositeButtons, SWT.NONE);
        lblSeconds.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        lblSeconds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblSeconds.setText("seconds");
        
        accountTreeViewer = new TreeViewer(grpAccounts, SWT.BORDER);
        accountTree = accountTreeViewer.getTree();
        accountTree.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
        accountTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        Menu menu = new Menu(accountTree);
        menu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuShown(MenuEvent arg0) {
                onAccountMenuShown();
            }
        });
        accountTree.setMenu(menu);
        
        menuItemNewAccount = new MenuItem(menu, SWT.NONE);
        menuItemNewAccount.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onNewAccountSelected();
            }
        });
        menuItemNewAccount.setImage(SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/key_add.png"));
        menuItemNewAccount.setText("&New Account");
        
        menuItemNewGroup = new MenuItem(menu, SWT.NONE);
        menuItemNewGroup.setImage(SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/folder_add.png"));
        menuItemNewGroup.setText("New Group");
        
        menuItemEditAccount = new MenuItem(menu, SWT.NONE);
        menuItemEditAccount.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent arg0) {
        		onEditAccount();
        	}
        });
        menuItemEditAccount.setText("&Edit Account");
        
        menuItemEditGroup = new MenuItem(menu, SWT.NONE);
        menuItemEditGroup.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onEditAccount();
            }
        });
        menuItemEditGroup.setText("Edit Group");
        
        new MenuItem(menu, SWT.SEPARATOR);
        
        menuItemDeleteAccount = new MenuItem(menu, SWT.NONE);
        menuItemDeleteAccount.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onDeleteAccount();
            }
        });
        menuItemDeleteAccount.setImage(SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/key_delete.png"));
        menuItemDeleteAccount.setText("&Delete Account");
        
        menuItemDeleteGroup = new MenuItem(menu, SWT.NONE);
        menuItemDeleteGroup.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onDeleteAccount();
            }
        });
        menuItemDeleteGroup.setImage(SWTResourceManager.getImage(GuiMain.class, "/org/daveware/passwordmaker/icons/folder_delete.png"));
        menuItemDeleteGroup.setText("Delete Group");
        
        menu_1 = new Menu(shlPasswordMaker, SWT.BAR);
        shlPasswordMaker.setMenuBar(menu_1);
        
        mntmFile = new MenuItem(menu_1, SWT.CASCADE);
        mntmFile.addArmListener(new ArmListener() {
            public void widgetArmed(ArmEvent arg0) {
                onFileMenuArmed();
            }
        });
        mntmFile.setText("File");
        
        menu_3 = new Menu(mntmFile);
        mntmFile.setMenu(menu_3);
        
        menuItemNew = new MenuItem(menu_3, SWT.NONE);
        menuItemNew.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                newFile();
            }
        });
        menuItemNew.setText("New");
        
        menuItemOpen = new MenuItem(menu_3, SWT.NONE);
        menuItemOpen.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                openFile();
            }
        });
        menuItemOpen.setText("Open");
        
        menuItemSave = new MenuItem(menu_3, SWT.NONE);
        menuItemSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                saveFile();
            }
        });
        menuItemSave.setText("Save");
        
        menuItemSaveAs = new MenuItem(menu_3, SWT.NONE);
        menuItemSaveAs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                saveFileAs();
            }
        });
        menuItemSaveAs.setText("Save As");
        
        new MenuItem(menu_3, SWT.SEPARATOR);
        
        menuItemExit = new MenuItem(menu_3, SWT.NONE);
        menuItemExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                exit();
            }
        });
        menuItemExit.setText("Exit");
        
        mntmhelp = new MenuItem(menu_1, SWT.CASCADE);
        mntmhelp.setText("&Help");
        
        menu_2 = new Menu(mntmhelp);
        mntmhelp.setMenu(menu_2);
        
        mntmabout = new MenuItem(menu_2, SWT.NONE);
        mntmabout.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent arg0) {
        		AboutDlg dlg = new AboutDlg(shlPasswordMaker, SWT.SHEET);
        		dlg.open();
        	}
        });
        mntmabout.setText("&About PasswordMakerJE");
        shlPasswordMaker.setTabList(new Control[]{grpInput, grpAccounts, sash});
    }

    /******************************
     * Below here is my stuff.
     */
    
    /**
     * Copies the currently generated password to the clipboard.
     */
    private void copyGeneratedToClipboard() {
        SecureCharArray generated = null;
        
        try {
            generated = generateOutput();
            Utilities.copyToClipboard(generated);
        }
        catch(Exception e) {}
        finally {
            if(generated!=null)
                generated.erase();
        }
    }
    
    /**
     * Locates the current account based on information in the config.
     * @return 0 if found, else non-zero.
     */
    public int findAccount() {
        int ret = 1;

        Account acc = null;
        String matchUrl = editUrl.getText();
        
        try {
            acc = db.findAccountByUrl(matchUrl);
            if(acc!=null)
                accountTreeViewer.setSelection(new StructuredSelection(acc));
            else
                accountTreeViewer.setSelection(null);
            //selectAccount(acc);
        }
        catch(Exception e) {
            MBox.showError(shlPasswordMaker, "Unable to locate account based on URL.\n" + e.getMessage());
            ret = 1;
        }
        
        return ret;
    }
    
    /**
     * Exits (disposes) this shell, prompting if db is dirty.
     */
    private void exit() {
        if(db.isDirty()) {
            switch(MBox.showYesNoCancel(shlPasswordMaker, EXIT_PROMPT)) {
            case SWT.YES:
                if(saveFile()==false)
                    return;
                break;
                
            case SWT.NO:
                break;
                
            case SWT.CANCEL:
                return;
            }
        }
        
        shlPasswordMaker.dispose();
    }

    /**
     * Generates the password and returns it.
     * @return A SecureCharArray object with the generated password.
     */
    private SecureCharArray generateOutput() {
        SecureCharArray mpw = null;
        SecureCharArray output = null;
        
        try {
        	if(selectedAccount!=null) {
        		mpw = new SecureCharArray(editMP.getText());
        		output = pwm.makePassword(mpw, selectedAccount);
        	}
        	else {
        		output = new SecureCharArray();
        	}
        }
        catch(Exception e) {}
        finally {
        	if(mpw!=null)
        		mpw.erase();
        }
        
        return output;
    }
    
    
    /**
     * Creates and returns a thread which will countdown from whatever the current countdown value is.
     * @param b The button to set the number of seconds left to.
     * @return The created thread.
     */
    private Thread getCountdownThread(Text text, int numSeconds) {
        final Text textControl = text;
        final int countdownValue = numSeconds;
        
        return new Thread() {
            public void run() {
                try {
                    // This is kinda dirty. A 'final' integer needs to be used inside the anonymous class
                    // so currentI is created each iteration. Gross.
                    for(int i=0; i<countdownValue; ++i) {
                        final int currentI = i;
                        display.asyncExec(new Runnable() {
                            public void run() {
                                textControl.setText(Integer.toString(countdownValue-currentI));
                            }
                        });
                        Thread.sleep(1000);
                    }
                }catch(Exception e) {}
                
                display.asyncExec(new Runnable() {
                    public void run() {
                        Utilities.clearClipboard();
                        
                        if(closeAfterTimer==true)
                        	shlPasswordMaker.dispose();
                        else {
                        	btnCopyToClipboard.setEnabled(true);
                        	comboCopyBehavior.setEnabled(true);
                        	editCopySeconds.setEnabled(true);
                        	editCopySeconds.setText(db.getGlobalSetting(GlobalSettingKey.CLIPBOARD_TIMEOUT));
                        }
                    }
                });
            }
        };
    }
    
    /**
     * Sets the various fields of the GUI based on the settings in the database.
     */
    private void setGuiFromGlobalSettings() {
        editCopySeconds.setText(db.getGlobalSetting(GlobalSettingKey.CLIPBOARD_TIMEOUT));
        btnShowPassword.setSelection(db.getGlobalSetting(GlobalSettingKey.SHOW_GEN_PW).compareTo("true")==0);
        onShowPasswordClicked();  // due to manual "setSelection" not triggering an event
    }
    
    /**
     * Loads the various fields with data from the config files.
     */
    private void loadFromCmdLineSettings() {
        if(cmdLineSettings.matchUrl!=null)
        	editUrl.setText(cmdLineSettings.matchUrl);
        if(cmdLineSettings.inputFilename!=null) {
            openFile(cmdLineSettings.inputFilename);
        }
        else {
            newFile();
        }
        
        // Only attempt an initial find if something was passed on the commandline
        if(cmdLineSettings.matchUrl!=null && cmdLineSettings.matchUrl.length()>0)
            findAccount();
    }

    /**
     * Creates a new blank(sorta) database. This will attempt to save first if the current
     * database is dirty and abort the new file operation if the save fails.
     * @return
     */
    private boolean newFile() {
        if(db!=null && db.isDirty()) {
            switch(MBox.showYesNoCancel(shlPasswordMaker, EXIT_PROMPT)) {
            case SWT.YES:
                // Attempt to save the file, if that fails then abort the new file operation
                if(saveFile()==false)
                    return false;
                break;
                
            case SWT.NO:
                break;
                
            case SWT.CANCEL:
                return false;
            }
        }
        
        db = new Database();
        db.addDatabaseListener(this);
        accountTreeViewer.setInput(db);
        textFilename.setText("");
    
        try {
            db.addDefaultAccount();
            db.setDirty(false);
            
            // TODO: This 2nd "setInput" is to work around a problem with the DatabaseListener not getting
            // messages - I'm not exactly sure why.
            accountTreeViewer.setInput(db);
            selectFirstAccount();
            setGuiFromGlobalSettings();
        } catch(Exception e) {
            // This REALLY should be impossible ... but, handle it anyway
            MBox.showError(shlPasswordMaker, "Unable to create default account.\n" + e.getMessage());
        }
        
        selectFirstAccount();
    
        return true;
    }

    /**
     * Handles when focus is gained on the accountFilter text box.
     */
    private void onAccountFilterTextFocusGained() {
    	String text = accountFilterText.getText();
    	
    	if(text.compareTo(ACCOUNT_FILTER_DESC)==0)
    		accountFilterText.setText("");
    	
    	/* Clear the italics */
    	accountFilterText.setFont(regularSearchFont);
    
    	filterIcon.setVisible(false);
    }

    /**
     * Handles when focus is lost from the accountFilter text box.
     */
    private void onAccountFilterTextFocusLost() {
    	String text = accountFilterText.getText();
    	if(text.length()==0) {
    		accountFilterText.setText(ACCOUNT_FILTER_DESC);
        	accountFilterText.setFont(italicsSearchFont);
        	filterIcon.setImage(searchImage);
    	}
    	else {
        	accountFilterText.setFont(regularSearchFont);
    		filterIcon.setImage(cancelImage);
    	}
    	filterIcon.setVisible(true);
    }

    private void onAccountMenuShown() {
        if(selectedAccount!=null) {
            if(selectedAccount.isFolder()) {
                menuItemEditGroup.setEnabled(true);
                menuItemDeleteGroup.setEnabled(true);
                menuItemEditAccount.setEnabled(false);
                menuItemDeleteAccount.setEnabled(false);
                
            }
            else {
                menuItemEditGroup.setEnabled(false);
                menuItemDeleteGroup.setEnabled(false);
                menuItemEditAccount.setEnabled(true);
                menuItemDeleteAccount.setEnabled(selectedAccount.isDefault()==false && selectedAccount.isRoot()==false);
            }
        }
        else {
            menuItemEditGroup.setEnabled(false);
            menuItemDeleteGroup.setEnabled(false);
            menuItemEditAccount.setEnabled(false);
            menuItemDeleteAccount.setEnabled(false);
        }
    }

    /**
     * Method invoked when the copy-and-exit button is clicked.
     * @param btn The button clicked.
     */
    private void onCopyToClipboard() {
        // First make sure the seconds-value is valid. If not, set it to 5 and carry on.
        int seconds;
        
        closeAfterTimer = comboCopyBehavior.getSelectionIndex() == 0 ? false : true;
        
        // Perform a dirty check if the app will close after the operation
        if(closeAfterTimer && db.isDirty()) {
            switch(MBox.showYesNoCancel(shlPasswordMaker, EXIT_PROMPT)) {
            case SWT.YES:
                // Only continue if the save succeeded
                if(saveFile()!=true)
                    return;
                break;
                
            case SWT.NO:
                break;
                
            case SWT.CANCEL:
                // user aborted
                return;
            }
        }
        
        try {
            seconds = Integer.parseInt(editCopySeconds.getText());
            if(seconds<1) {
                secondsDecoration.show();
                editCopySeconds.setFocus();
                return;
            }
            
            secondsDecoration.hide();
        }
        catch(Exception ee) {
            secondsDecoration.show();
            editCopySeconds.setFocus();
            return;
        }
        
        btnCopyToClipboard.setEnabled(false);
        editCopySeconds.setEnabled(false);
        comboCopyBehavior.setEnabled(false);
        
        copyGeneratedToClipboard();
        Thread thread = getCountdownThread(editCopySeconds, seconds);
        if(thread!=null)
            thread.start();
    }
    
    /**
     * Handles the deletion of the selected account.
     */
    private void onDeleteAccount() {
        if(selectedAccount==null) {
            MBox.showError(shlPasswordMaker, "No account is selected for deletion, this should not be possible. Please file a bug report.");
            return;
        }
            
        String type = selectedAccount.isFolder() ? "folder" : "account";
        String msg = "Are you sure you wish to delete " + type + " '" + selectedAccount.getName() + "'";
        int numChildren = selectedAccount.getNestedChildCount();
        if(numChildren>0)
            msg += " and it's " + numChildren + " children?";
        else
            msg += "?";
        
        if(MBox.showYesNo(shlPasswordMaker, msg)==SWT.YES) {
            Account nearestRelative = db.findNearestRelative(selectedAccount);
            db.removeAccount(selectedAccount);
            if(nearestRelative==null)
                accountTreeViewer.setSelection(null);
            else
                accountTreeViewer.setSelection(new StructuredSelection(nearestRelative));
        }
    }

    private void onCloseWindow(Event e) {
        if(db.isDirty()==true) {
            switch(MBox.showYesNoCancel(shlPasswordMaker, EXIT_PROMPT)) {
            case SWT.YES:
                if(saveFile()==true) {
                    e.doit = true;
                }
                break;
                 
            case SWT.NO:
                e.doit = true;
                break;
                
            case SWT.CANCEL:
                e.doit = false;
                break;
                
            default:
                e.doit = true;
                break;
            }
        }
    }
    
    /**
     * Invoked when the shell is being disposed (closed).
     * @param arg0 The dispose event.
     */
    private void onDisposing(DisposeEvent arg0) {
        if(passwordImage!=null)
            passwordImage.dispose();
        if(searchImage!=null)
        	searchImage.dispose();
        if(cancelImage!=null)
        	cancelImage.dispose();
        if(eyeImage!=null)
        	eyeImage.dispose();
        if(eyeClosedImage!=null)
        	eyeClosedImage.dispose();
        if(passwordFont!=null)
        	passwordFont.dispose();
        if(regularSearchFont!=null)
        	regularSearchFont.dispose();
        if(accountTreeModel!=null)
        	accountTreeModel.dispose();
        if(accountTreeLabelProvider!=null)
        	accountTreeLabelProvider.dispose();
        
        passwordImage = null;
        searchImage = null;
        cancelImage = null;
        eyeImage = null;
        eyeClosedImage = null;
        regularSearchFont = null;
        accountTreeModel = null;
        accountTreeLabelProvider = null;
    }
    
    /**
     * Handles editing an account.
     */
    private void onEditAccount() {
    	AccountDlg dlg = null;
    	if(selectedAccount!=null) {
    		dlg = new AccountDlg(selectedAccount);
    		
    		// A copy of the edited account is returned if "ok" is clicked.
    		Account newAccount = dlg.open();
    		if(newAccount!=null) {
    		    selectedAccount.copySettings(newAccount);
    		    db.changeAccount(selectedAccount);
    		    //accountTreeViewer.refresh(account, true);
    		    
    		    // The tree already has the account selected. Applying the same selection actually
    		    // has the side-effect of unselecting the account. So instead just invoke the selectAccount()
    		    // method which is normally invoked by the tree causing the selection.
    		    selectAccount(selectedAccount);
    		}
    	}
    }

    /**
     * Invoked when the File menu is armed.
     */
    private void onFileMenuArmed() {
        menuItemSave.setEnabled(db.isDirty());
    }
    
    /**
     * Invoked when the filter composite is resized so that the text box and filter 
     * icon can be re-aligned.
     * @param arg0 The composite-resize event.
     */
	private void onFilterCompositeResized(ControlEvent arg0) {
		Composite frame = (Composite)arg0.widget;
		accountFilterText.setSize(frame.getSize());
		filterIcon.setLocation(frame.getSize().x - filterIcon.getSize().x - 1, 1);
	}

	/**
     * Handles when the X is clicked in the accountFilterText widget.
     * 
     * This will reset the text/image/font back to defaults.
     */
    private void onFilterIconClicked() {
    	accountFilterText.setText(ACCOUNT_FILTER_DESC);
    	accountFilterText.setFont(italicsSearchFont);
    	filterIcon.setImage(searchImage);
    	filterIcon.setVisible(true);
    	isFiltering = false;
    }
    
    private void onFilterModified(ModifyEvent e) {
    	String text = accountFilterText.getText();
    	if(text.compareTo(ACCOUNT_FILTER_DESC)==0 || text.length()==0) {
    		isFiltering = false;
    	}
    	else {
    		isFiltering = true;
    	}
    	
    	if(accountTreeViewer!=null)
    		accountTreeViewer.refresh();
    }
    
    private void onNewAccountSelected() {
        Account parentAccount = null;
        AccountDlg dlg = null;
        
        // If no account is selected, then default to the root
        if(selectedAccount==null)
            parentAccount = db.getRootAccount();
        else {
            // Otherwise decide if it will be a sibling of the selected account or a child
            // of the selected group.
            parentAccount = selectedAccount;
            
            // If the parent is not a folder, it needs to be created as a sibling. So locate
            // who the real parent is.
            if(parentAccount.isFolder()==false) {
                parentAccount = db.findParent(parentAccount);
                if(parentAccount==null) {
                    MBox.showError(shlPasswordMaker, "Unable to locate parent account of '" + selectedAccount.getName() + "' id=" + selectedAccount.getId() +", cannot add new account.");
                    return;
                }
            }
        }

        // Create a new blank account with default settings and the dialog
        Account newAccount = new Account();
        dlg = new AccountDlg(newAccount);
            
        // A copy of the account is returned if "ok" is clicked.
        newAccount = dlg.open();
        if(newAccount!=null) {
            try {
                newAccount.setId(Account.createId(newAccount));
                db.addAccount(parentAccount, newAccount);
            } catch(Exception e) {
                MBox.showError(shlPasswordMaker, "While creating the new account, an error occurred. You should save your work and restart.\n" + e.getMessage());
            }
        }
    }
    
    private void onSecondsFocusLost() {
        int numSeconds;
        
        try {
            numSeconds = Integer.parseInt(editCopySeconds.getText());
            if(numSeconds<1) {
                // TODO: should this also check for some kind of max?
                secondsDecoration.show();
                return;
            }
            
            db.setGlobalSetting(GlobalSettingKey.CLIPBOARD_TIMEOUT, editCopySeconds.getText());
            secondsDecoration.hide();
        } catch(Exception e) {
            secondsDecoration.show();
        }
    }
    
    /**
     * Invoked when the eyeball is clicked.
     */
    private void onShowPasswordClicked() {
    	showPassword = btnShowPassword.getSelection();
    	if(showPassword)
    		btnShowPassword.setImage(eyeImage);
    	else
    		btnShowPassword.setImage(eyeClosedImage);
    	
   	    db.setGlobalSetting(GlobalSettingKey.SHOW_GEN_PW, Boolean.toString(showPassword));
    	
    	regeneratePasswordAndDraw();
    }
    
    /**
     * Invoked when the URL field is modified. Causes the accounts to be searched for
     * anything matching the current URL text.
     * @param arg0 Ignored, can be null.
     */
    private void onUrlModified(ModifyEvent arg0) {
    	findAccount();
    }
        
    
    /**
     * Opens up an "open" dialog and then opens the selected file.
     * @return true on success.
     */
    private boolean openFile() {
        FileDialog fd = new FileDialog(shlPasswordMaker, SWT.OPEN);
        fd.setText("Open RDF File");
        fd.setFilterExtensions(new String [] { "*.rdf", "*.*" });
        String selected = fd.open();
        if(selected!=null && selected.length()>0)
            return openFile(selected);
        return false;
    }

    /**
     * Reads a file in and sets up the necessary widgets with the data. If this fails
     * then it will create a new empty database and return false.
     * @param filename The filename (assumes RDF).
     * @return true on success.
     */
    private boolean openFile(String filename) {
        RDFDatabaseReader rdfReader = null;
        File inputFile = null;
        FileInputStream fin = null;
        boolean ret = false;
    
        try {
            rdfReader = new RDFDatabaseReader();
            inputFile = new File(filename);
            fin = new FileInputStream(inputFile);
            db = rdfReader.read(fin);
            db.addDatabaseListener(this);
            
            // Widget setup
            accountTreeViewer.setInput(db);
            textFilename.setText(filename);
            
            selectFirstAccount();
            setGuiFromGlobalSettings();
            
            db.setDirty(false);
            ret = true;
        } catch(Exception ex) {
            cmdLineSettings.inputFilename = "";
            db = new Database();
            db.addDatabaseListener(this);
            accountTreeViewer.setInput(db);
    
            MBox.showError(shlPasswordMaker, "Unable to open " + filename + "\n" + ex.getMessage());
        }
        finally {
            try {
                if(fin!=null)
                    fin.close();
            } catch(Exception exinner) { }
        }
        
        return ret;
    }

    /**
     * Causes the password to be regenerated.
     * 
     * This calculates the new generated password and draws it to the image which is used
     * to display its value. A standard text control is not used because they take a "String"
     * which cannot be reliably erased when finished. The password is drawn character by
     * character so the full string never sits on the stack anywhere.
     */
    private void regeneratePasswordAndDraw() {
       SecureCharArray output = null;
       GC gc = null;
       
       try {
           gc = new GC(passwordImage);
           gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
           gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
           gc.fillRectangle(canvasOutput.getClientArea());
           
           if(selectedAccount!=null && selectedAccount.isFolder()==false && editMP.getText().length()>0) {
               output = generateOutput();

               if(showPassword==true) {
    	           gc.setFont(passwordFont);
    	           int x = 0;
    	           int xPos = 5;
    	           for(x=0; x<output.getData().length; x++) {
    	               char strBytes [] = { output.getData()[x] };
    	               String str = new String(strBytes);
    	               gc.drawText(str, xPos, 5);
    	               xPos += gc.stringExtent(str).x + 2;
    	           }
               }
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
    }
    
	/**
     * Attempts to save a file to the current filename. If there is no current file
     * name, then save-as is invoked. This also clears the dirty status on success.
     * @return true on success.
     */
    private boolean saveFile() {
        boolean ret = false;
        
        // If we don't have a filename yet, call saveAs() which will call this function
        // in return with a filename set.
        if(cmdLineSettings.inputFilename==null || cmdLineSettings.inputFilename.length()==0) {
            return saveFileAs();
        }
        
        try {
            RDFDatabaseWriter out = new RDFDatabaseWriter();
            File newFile = new File(cmdLineSettings.inputFilename);
            if(newFile.exists()==false)
                newFile.createNewFile();
            
            FileOutputStream fout = new FileOutputStream(newFile);
            out.write(fout, db);
            db.setDirty(false);
            ret = true;
        }
        catch(Exception e) {
            MBox.showError(shlPasswordMaker, "Unable to save to " + cmdLineSettings.inputFilename + ".\n" + e.getMessage());
        }
        
        return ret;
    }

    /**
     * Opens up a dialog-box allowing the user to select a file to save to. This will invoke
     * saveFile behind the scenes and update config.inputFilename/textFilename on success.
     * @return true on success.
     */
    private boolean saveFileAs() {
        FileDialog fd = new FileDialog(shlPasswordMaker, SWT.SAVE);
        fd.setText("Save RDF As");
        fd.setFilterExtensions(new String [] { "*.rdf", "*.*" });
        String selected = fd.open();
        if(selected!=null && selected.length()>0) {
            String oldFilename = cmdLineSettings.inputFilename;
            cmdLineSettings.inputFilename = selected;
            if(saveFile()==true) {
                textFilename.setText(cmdLineSettings.inputFilename);
                return true;
            }
            
            // it failed if we get here, restore the filename
            cmdLineSettings.inputFilename = oldFilename;
            textFilename.setText(cmdLineSettings.inputFilename);
        }
        
        
        return false;
    }

    /**
     * Called when an account is selected. This is invoked by the tree when the
     * selection is made.
     * 
     * @param acc The new account selection.
     */
    private void selectAccount(Account acc) {
    	// Store the newly selected account
        selectedAccount = acc;
        if(acc!=null) {
            btnCopyToClipboard.setEnabled(selectedAccount.isFolder()==false);
            editAccount.setText(selectedAccount.getName());
            editDesc.setText(selectedAccount.getDesc());
            editUsername.setText(selectedAccount.getUsername());
        }
        else {
            btnCopyToClipboard.setEnabled(false);
            editAccount.setText("NO ACCOUNT SELECTED");
            editDesc.setText("");
            editUsername.setText("");
        }
        
        regeneratePasswordAndDraw();
    }

    private void selectFirstAccount() {
        if(db.getRootAccount().getChildren().size()>0)
	        accountTreeViewer.setSelection(new StructuredSelection(db.getRootAccount().getChildren().get(0)));
	    else
	        accountTreeViewer.setSelection(null);
	}

	//////////////////////////////////////////////////////////////
	//
	// DATABASELISTENER INTERFACE
	//
    //////////////////////////////////////////////////////////////
	
    @Override
    public void accountAdded(Account parent, Account account) {
        // I'm not sure why, but if you add a node off the root level, refreshing the root node
        // will not make it show up. Refreshing the whole tree does.
        if(parent.isRoot())
            accountTreeViewer.refresh();
        else
            accountTreeViewer.refresh(parent);
        
        accountTreeViewer.setSelection(new StructuredSelection(account));    
    }

    @Override
    public void accountRemoved(Account parent, Account account) {
        // I'm not sure why, but if you remove a node off the root level, refreshing the root node
        // will not make it show up. Refreshing the whole tree does.
        if(parent.isRoot())
            accountTreeViewer.refresh();
        else
            accountTreeViewer.refresh(parent);
    }

    @Override
    public void accountChanged(Account account) {
        //accountTreeViewer.refresh(account);
        accountTreeViewer.update(account, null);
    }

    @Override
    public void dirtyStatusChanged(boolean status) {
        if(status)
            shlPasswordMaker.setText("* " + TITLE_STRING);
        else
            shlPasswordMaker.setText(TITLE_STRING);
    }
}
