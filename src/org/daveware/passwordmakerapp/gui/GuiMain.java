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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.BuildInfo;
import org.daveware.passwordmaker.Database;
import org.daveware.passwordmaker.DatabaseListener;
import org.daveware.passwordmaker.GlobalSettingKey;
import org.daveware.passwordmaker.PasswordMaker;
import org.daveware.passwordmaker.RDFDatabaseReader;
import org.daveware.passwordmaker.RDFDatabaseWriter;
import org.daveware.passwordmaker.SecureCharArray;
import org.daveware.passwordmaker.Utilities;
import org.daveware.passwordmakerapp.AccountComparator;
import org.daveware.passwordmakerapp.CmdLineSettings;
import org.daveware.passwordmakerapp.SortOptions;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
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
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Spinner;


/**
 * Implements the main window for PasswordMakerJE.
 * 
 * TODO: this should really be using validation, but I'm yet to figure out how
 * to make it work properly. I can't find the damned fieldassist package of
 * jface.
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
    private Spinner editCopySeconds;
    private Text editUrlSearch;
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
    private MenuItem menuItemSort;
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
    private ControlDecoration secondsDecoration;

    // -------- BEGIN RESOURCES THAT MUST BE DISPOSED OF
    // ---------------------------------
    private Image searchImage = null;
    private Image cancelImage = null;
    private Image passwordImage = null;
    private Image eyeImage = null;
    private Image eyeClosedImage = null;
    private Image iconImage = null;

    private Font passwordFont = null;

    private Font regularSearchFont = null;
    private Font italicsSearchFont = null;

    private AccountTreeModel accountTreeModel = new AccountTreeModel();
    private AccountTreeLabelProvider accountTreeLabelProvider = new AccountTreeLabelProvider();
    // -------- End RESOURCES THAT MUST BE DISPOSED OF
    // -----------------------------------

    private Thread countdownThread = null;
    private CmdLineSettings cmdLineSettings;
    private String currentFilename = "";
    private Account selectedAccount = null;
    private PasswordMaker pwm = null;
    private Database db = null;
    private BuildInfo buildInfo = null;
    private SortOptions sortOptions = new SortOptions();
    private boolean urlSearchEnabled = true;

    private boolean isFiltering = false;
    private boolean showPassword = true;

    private boolean closeAfterTimer = false;
    private Text editInputUrl;
    private Label lblInputUrl;
    private Text editUrl;
    private Label lblUrl_1;

    public GuiMain(CmdLineSettings c) {
        cmdLineSettings = c;
        buildInfo = new BuildInfo();
    }

    private void showException(Exception e) {
        Shell bogus = new Shell();

        if (e == null) {
            MBox.showError(
                    bogus,
                    "An exception has occurred but the data it contained was empty. Please consider filing a bug report at http://code.google.com/p/passwordmaker-je");
        } else {
            ExceptionDlg dlg = new ExceptionDlg(bogus, e);
            dlg.open();
        }

        bogus.dispose();
    }

    /**
     * Causes everything to happen. This is called by the calling class.
     * 
     * @return
     */
    public int run() {
        open();

        return 0;
    }

    /**
     * Open the window, builds the UI, and runs the event loop.
     * 
     * @wbp.parser.entryPoint
     * 
     */
    private void open() {
        try {
            display = Display.getDefault();
            createContents();
            
            setupAppIcon();
            setupDragQueens();
            setupFonts();
            setupTree();
            setupDecorators();
            
            setupOSX();

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
        } catch (NullPointerException ne) {
            showException(ne);
            return;
        } catch (Exception e) {
            showException(e);
        }

    }

    /**
     * Assigns the application icon.
     */
    private void setupAppIcon() {
        iconImage = SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/pwmlogo.png");
        shlPasswordMaker.setImage(iconImage);
    }
    
    /**
     * Adds drag'n'drop to the shell for URLs (and other text, but really for
     * URLs).
     */
    private void setupDragQueens() {
        DropTarget dt = new DropTarget(shlPasswordMaker, DND.DROP_MOVE
                | DND.DROP_COPY | DND.DROP_LINK);
        dt.setTransfer(new Transfer[] { TextTransfer.getInstance(),
                URLTransfer.getInstance() });
        dt.addDropListener(new DropTargetAdapter() {
            public void dragEnter(DropTargetEvent e) {
                e.detail = DND.DROP_COPY;
            }

            public void dragOperationChanged(DropTargetEvent e) {
                e.detail = DND.DROP_COPY;
            }

            public void drop(org.eclipse.swt.dnd.DropTargetEvent event) {
                editUrlSearch.setText(event.data.toString());
                editMP.setFocus();

                // TODO: *could* clear out the account filtering, but I'm not
                // sure why someone would
                // add a filter and then drag & drop.
            }
        });
    }

    protected void setupDecorators() {
        ControlDecoration[] decors = { secondsDecoration };
        Image image = FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
                .getImage();
        for (ControlDecoration dec : decors) {
            dec.setImage(image);
            dec.hide();
        }
    }

    protected void setupFonts() {
        // Clone the font of the accountFilterText widget
        Font stockFont = accountFilterText.getFont();
        FontData[] fdItalics = display.getSystemFont().getFontData();

        // Default OSX font doesn't support italics (on my system)
        if (Utilities.isMac()) {
            italicsSearchFont = new Font(display, fdItalics[0].getName(),
                    fdItalics[0].getHeight(), 0);
        } else {
            italicsSearchFont = new Font(display, fdItalics[0].getName(),
                    fdItalics[0].getHeight(), SWT.ITALIC);
        }

        regularSearchFont = new Font(display, stockFont.getFontData());
        accountFilterText.setFont(italicsSearchFont);

        passwordFont = new Font(Display.getCurrent(), JFaceResources.getFont(JFaceResources.TEXT_FONT).getFontData()[0].getName(),
                14, SWT.BOLD);
    }
    
    /*
     * Does OSX-specific setup.
     * 
     * OSX has a separate application menu which requires special handling. This adds the special
     * handling for the quit, preferences, and about menu items of that menu.
     */
    protected void setupOSX() {
    	if(System.getProperty("os.name").toLowerCase().contains("mac")) {
    		CocoaUIEnhancer enh = new CocoaUIEnhancer("PWMJE");
    		enh.hookApplicationMenu(display, new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					onCloseWindow(arg0);
					
				}
    		}, new Action("&About PWMJE") {
    			public void run() {
    				AboutDlg dlg = new AboutDlg(shlPasswordMaker, SWT.SHEET);
                    dlg.open();
    			}
    		}, new Action("&Preferences") {
    			public void run() {
    				
    			}
    		});
    	}
    }

    protected void setupTree() {
        // The tree must have a brain!
        accountTreeLabelProvider.loadImages();
        accountTreeViewer.setContentProvider(accountTreeModel);
        accountTreeViewer.setLabelProvider(accountTreeLabelProvider);

        accountTreeViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent e) {
                        if (e.getSelection().isEmpty()) {
                            selectAccount(null);
                        } else if (e.getSelection() instanceof IStructuredSelection) {
                            IStructuredSelection selection = (IStructuredSelection) e
                                    .getSelection();
                            Account account = (Account) selection.iterator()
                                    .next();
                            selectAccount(account);
                        }
                    }
                });

        accountTreeViewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                    Object element) {
                ArrayList<Account> parentList = new ArrayList<Account>();

                if (isFiltering == false)
                    return true;

                if (element == null)
                    return false;

                String filterText = accountFilterText.getText().toLowerCase();
                Account account = (Account) element;

                if (account.isFolder() == false)
                    return account.getName().toLowerCase().contains(filterText);

                // Search through all children looking for a matching account.
                // If there is
                // a match, then this folder must remain visible.
                parentList.add(account);
                while (parentList.size() > 0) {
                    account = parentList.remove(0);

                    for (Account child : account.getChildren()) {
                        if (child.isFolder()) {
                            parentList.add(child);
                        } else {
                            if (child.getName().toLowerCase()
                                    .contains(filterText))
                                return true;
                        }
                    }
                }

                return false;
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
            	// For OSX we install a different close-handler during setup
            	if(System.getProperty("os.name").toLowerCase().contains("mac")==false) {
            		onCloseWindow(arg0);
            	}
            }

        });

        shlPasswordMaker.getDisplay().addFilter(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if ((e.stateMask & SWT.CTRL) == SWT.CTRL
                        || (e.stateMask & SWT.COMMAND) == SWT.COMMAND) {
                    if (e.keyCode == 'f') {
                        accountFilterText.setFocus();
                    }
                }
            }
        });
        searchImage = SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/magglass.png");
        cancelImage = SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/cancel.png");
        eyeImage = SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/eye.png");
        eyeClosedImage = SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/eye_closed.png");

        shlPasswordMaker.setMinimumSize(new Point(855, 345));
        shlPasswordMaker.setSize(855, 412);
        setTitle();
        // shlPasswordMaker.setText(TITLE_STRING + " - " +
        // buildInfo.getVersion());
        shlPasswordMaker.setLayout(new FormLayout());

        Sash sash = new Sash(shlPasswordMaker, SWT.VERTICAL);
        Group grpAccounts = new Group(shlPasswordMaker, SWT.NONE);
        FormData fd_grpAccounts = new FormData();
        fd_grpAccounts.left = new FormAttachment(0, 5);
        fd_grpAccounts.right = new FormAttachment(sash);
        fd_grpAccounts.bottom = new FormAttachment(100, -10);
        fd_grpAccounts.top = new FormAttachment(0, 5);
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
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
                1, 1));

        filterIcon = new CLabel(composite, SWT.NONE);
        filterIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent arg0) {
                onFilterIconClicked();
            }
        });
        filterIcon.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_LIST_BACKGROUND));
        filterIcon.setImage(SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/magglass.png"));
        filterIcon.setBounds(187, 1, 22, 20);
        filterIcon.setText("");

        accountFilterText = new Text(composite, SWT.BORDER);
        accountFilterText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (arg0.keyCode == SWT.CR) {
                    editMP.setFocus();
                }
            }
        });
        accountFilterText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                onFilterModified(arg0);
            }
        });
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
        sashData.left = new FormAttachment(22);
        sashData.top = new FormAttachment(0);
        sashData.bottom = new FormAttachment(100);
        sash.setLayoutData(sashData);
        sash.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail != SWT.DRAG) {
                    sashData.left = new FormAttachment(0, event.x);
                    shlPasswordMaker.layout();
                }
            }
        });
        Group grpInput = new Group(shlPasswordMaker, SWT.NONE);
        grpInput.setText("Password Input && Search");
        FormData fd_grpInput = new FormData();
        fd_grpInput.right = new FormAttachment(100, -5);
        fd_grpInput.left = new FormAttachment(sash);
        fd_grpInput.bottom = new FormAttachment(grpAccounts, 0, SWT.BOTTOM);
        fd_grpInput.top = new FormAttachment(0, 5);
        grpInput.setLayoutData(fd_grpInput);
        GridLayout gl_grpInput = new GridLayout(2, false);
        grpInput.setLayout(gl_grpInput);

        Label lblUrl = new Label(grpInput, SWT.NONE);
        lblUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
                1, 1));
        lblUrl.setText("URL Search:");

        editUrlSearch = new Text(grpInput, SWT.BORDER);
        editUrlSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                ((Text) (arg0.widget)).selectAll();
            }
        });
        editUrlSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        editUrlSearch.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                onUrlSearchModified(arg0);
            }
        });

        Label lblAccount = new Label(grpInput, SWT.RIGHT);
        lblAccount.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblAccount.setText("Account:");

        editAccount = new Text(grpInput, SWT.BORDER);
        editAccount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        editAccount.setEnabled(false);
        editAccount.setEditable(false);

        Label lblDescription = new Label(grpInput, SWT.RIGHT);
        lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
                false, 1, 1));
        lblDescription.setText("Description:");

        editDesc = new Text(grpInput, SWT.BORDER | SWT.V_SCROLL);
        editDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
                1));
        editDesc.setEnabled(false);
        editDesc.setEditable(false);

        lblInputUrl = new Label(grpInput, SWT.NONE);
        lblInputUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblInputUrl.setText("Input URL:");

        editInputUrl = new Text(grpInput, SWT.BORDER);
        editInputUrl.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                ((Text) (arg0.widget)).selectAll();
            }
        });
        editInputUrl.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                regeneratePasswordAndDraw();
            }
        });
        editInputUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));

        lblUrl_1 = new Label(grpInput, SWT.NONE);
        lblUrl_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblUrl_1.setText("URL:");

        editUrl = new Text(grpInput, SWT.BORDER);
        editUrl.setEnabled(false);
        editUrl.setEditable(false);
        editUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));

        Label lblUsername = new Label(grpInput, SWT.RIGHT);
        lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblUsername.setText("Username:");

        editUsername = new Text(grpInput, SWT.BORDER);
        editUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                ((Text) (arg0.widget)).selectAll();
            }
        });
        editUsername.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                regeneratePasswordAndDraw();
            }
        });
        editUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));

        Label lblMasterPw = new Label(grpInput, SWT.RIGHT);
        lblMasterPw.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblMasterPw.setText("Master PW:");

        editMP = new Text(grpInput, SWT.BORDER | SWT.PASSWORD);
        editMP.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                ((Text) (arg0.widget)).selectAll();
            }
        });
        editMP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
                1));
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
        GridData gd_composite_1 = new GridData(SWT.RIGHT, SWT.TOP, false,
                false, 1, 1);
        gd_composite_1.heightHint = 33;
        composite_1.setLayoutData(gd_composite_1);

        Label lblGenerated = new Label(composite_1, SWT.RIGHT);
        lblGenerated.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblGenerated.setText("Generated:");

        btnShowPassword = new Button(composite_1, SWT.FLAT | SWT.TOGGLE);
        btnShowPassword.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onShowPasswordClicked();
            }
        });
        btnShowPassword
                .setToolTipText("Toggles the visibility of the generated password.");
        GridData gd_btnShowPassword = new GridData(SWT.RIGHT, SWT.CENTER,
                false, false, 1, 1);
        gd_btnShowPassword.widthHint = 20;
        gd_btnShowPassword.heightHint = 17;
        btnShowPassword.setLayoutData(gd_btnShowPassword);
        btnShowPassword.setImage(SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/eye.png"));

        Canvas outputCanvas = new Canvas(grpInput, SWT.BORDER);
        outputCanvas.setLayout(new FillLayout(SWT.HORIZONTAL));
        outputCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                false, 1, 1));
        outputCanvas.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                if (passwordImage != null)
                    passwordImage.dispose();
                passwordImage = new Image(Display.getCurrent(), canvasOutput
                        .getClientArea());
                regeneratePasswordAndDraw();
            }

        });
        outputCanvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent arg0) {
                if (passwordImage != null) {
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
        btnCopyToClipboard
                .setToolTipText("Copies the generated password to the clipboard and then based on the combobox next to the button:\r\n\r\n\"erase clipboard in\"\r\nClears the clipboard after X seconds.\r\n\r\n\"close PasswordMakerJE in\"\r\nSame thing as above but also closes PasswordMakerJE after X seconds.");
        btnCopyToClipboard.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                onCopyToClipboard();
            }
        });
        btnCopyToClipboard.setText("Copy to clipboard, then");

        comboCopyBehavior = new Combo(compositeButtons, SWT.READ_ONLY);
        comboCopyBehavior.setToolTipText("");
        comboCopyBehavior.setItems(new String[] { "erase clipboard in",
                "close app and erase clipboard in" });
        comboCopyBehavior.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 1, 1));
        comboCopyBehavior.select(0);

        editCopySeconds = new Spinner(compositeButtons, SWT.BORDER);
        editCopySeconds.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent arg0) {
                onSecondsFocusLost();
            }
        });
        editCopySeconds.setMinimum(1);
        editCopySeconds.setSelection(5);
        GridData gd_editCloseSeconds = new GridData(SWT.LEFT, SWT.CENTER,
                false, false, 1, 1);
        gd_editCloseSeconds.widthHint = 33;
        editCopySeconds.setLayoutData(gd_editCloseSeconds);

        secondsDecoration = new ControlDecoration(editCopySeconds, SWT.LEFT
                | SWT.TOP);
        secondsDecoration
                .setDescriptionText("The number of seconds must be a positive value");

        Label lblSeconds = new Label(compositeButtons, SWT.NONE);
        lblSeconds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        lblSeconds.setText("seconds");

        accountTreeViewer = new TreeViewer(grpAccounts, SWT.BORDER);
        accountTree = accountTreeViewer.getTree();
        accountTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
                1, 1));

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
        menuItemNewAccount.setImage(SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/key_add.png"));
        menuItemNewAccount.setText("&New Account");

        menuItemEditAccount = new MenuItem(menu, SWT.NONE);
        menuItemEditAccount.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onEditAccount();
            }
        });
        menuItemEditAccount.setText("&Edit Account");

        new MenuItem(menu, SWT.SEPARATOR);

        menuItemNewGroup = new MenuItem(menu, SWT.NONE);
        menuItemNewGroup.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onNewGroupSelected();
            }
        });
        menuItemNewGroup.setImage(SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/folder_add.png"));
        menuItemNewGroup.setText("New Group");

        menuItemEditGroup = new MenuItem(menu, SWT.NONE);
        menuItemEditGroup.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onEditAccount();
            }
        });
        menuItemEditGroup.setText("Edit Group");

        MenuItem menuItem = new MenuItem(menu, SWT.SEPARATOR);

        menuItemSort = new MenuItem(menu, SWT.NONE);
        menuItemSort.setImage(SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/sort_ascend.png"));
        menuItemSort.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onSort();
            }
        });
        menuItemSort.setText("Sort");

        new MenuItem(menu, SWT.SEPARATOR);

        menuItemDeleteAccount = new MenuItem(menu, SWT.NONE);
        menuItemDeleteAccount.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onDeleteAccount();
            }
        });
        menuItemDeleteAccount.setImage(SWTResourceManager.getImage(
                GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/key_delete.png"));
        menuItemDeleteAccount.setText("&Delete Account");

        menuItemDeleteGroup = new MenuItem(menu, SWT.NONE);
        menuItemDeleteGroup.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                onDeleteAccount();
            }
        });
        menuItemDeleteGroup.setImage(SWTResourceManager.getImage(GuiMain.class,
                "/org/daveware/passwordmakerapp/icons/folder_delete.png"));
        menuItemDeleteGroup.setText("Delete Group");

        menu_1 = new Menu(shlPasswordMaker, SWT.BAR);
        shlPasswordMaker.setMenuBar(menu_1);

        mntmFile = new MenuItem(menu_1, SWT.CASCADE);
        mntmFile.addArmListener(new ArmListener() {
            public void widgetArmed(ArmEvent arg0) {
                onFileMenuArmed();
            }
        });
        mntmFile.setText("&File");

        menu_3 = new Menu(mntmFile);
        mntmFile.setMenu(menu_3);

        menuItemNew = new MenuItem(menu_3, SWT.NONE);
        menuItemNew.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                newFile();
            }
        });

        int specialKey = Utilities.isMac() ? SWT.COMMAND : SWT.CTRL;
        String specialKeyStr = Utilities.isMac() ? "\tCommand+" : "\tCtrl+";

        menuItemNew.setText("New Database\tCtrl+N");
        menuItemNew.setAccelerator(specialKey + 'N');

        menuItemOpen = new MenuItem(menu_3, SWT.NONE);
        menuItemOpen.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                openFile();
            }
        });
        menuItemOpen.setText("Open Database\tCtrl+O");
        menuItemOpen.setAccelerator(specialKey + 'O');

        menuItemSave = new MenuItem(menu_3, SWT.NONE);
        menuItemSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                saveFile();
            }
        });
        menuItemSave.setText("Save Database\tCtrl+S");
        menuItemSave.setAccelerator(specialKey + 'S');

        menuItemSaveAs = new MenuItem(menu_3, SWT.NONE);
        menuItemSaveAs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                saveFileAs();
            }
        });
        menuItemSaveAs.setText("Save Database As");

        new MenuItem(menu_3, SWT.SEPARATOR);

        menuItemExit = new MenuItem(menu_3, SWT.NONE);
        menuItemExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                exit();
            }
        });
        menuItemExit.setText("Exit");

        // The MAC's "about" menuitem is part of the application menu. For everyone else
        // it is in a separate "Help" menu.
        if(System.getProperty("os.name").toLowerCase().contains("mac")==false) {
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
		}
        shlPasswordMaker
                .setTabList(new Control[] { grpInput, grpAccounts, sash });
    }

    /******************************
     * Below here is my stuff.
     */


    /**
     * Disables all controls when the copy-to-clipboard timer is running.
     */
    private void disableControlsForCopy() {
        Control [] controls = { accountTree, editUsername, editMP, editUrlSearch, 
                editCopySeconds, comboCopyBehavior, accountFilterText, filterIcon
        };
        for(Control c: controls) {
            c.setEnabled(false);
        }
        
        btnCopyToClipboard.setText("Cancel countdown");
        
        filterIcon.setVisible(false);
    }
    
    /**
     * Enables all controls once the copy-to-clipboard timer has expired.
     */
    private void enableControlsForCopy() {
        Control [] controls = { accountTree, editUsername, editMP, editUrlSearch, 
                editCopySeconds, comboCopyBehavior, accountFilterText, filterIcon
        };
        for(Control c: controls) {
            c.setEnabled(true);
        }
        
        btnCopyToClipboard.setText("Copy to clipboard, then");
        
        filterIcon.setVisible(true);
    }

    
    /**
     * Copies the currently generated password to the clipboard.
     */
    private void copyGeneratedToClipboard() {
        SecureCharArray generated = null;

        try {
            generated = generateOutput();
            Utilities.copyToClipboard(generated);
        } catch (Exception e) {
        } finally {
            if (generated != null)
                generated.erase();
        }
    }

    /**
     * Locates the current account based on information in the config.
     * 
     * @return 0 if found, else non-zero.
     */
    public int findAccountByUrl() {
        // Url search gets disabled when manually setting the value of the
        // editUrlSearch widget,
        // otherwise it invokes this function
        if (!urlSearchEnabled)
            return 0;

        int ret = 1;

        Account acc = null;
        String matchUrl = editUrlSearch.getText();

        try {
            acc = db.findAccountByUrl(matchUrl);
            if (acc != null)
                accountTreeViewer.setSelection(new StructuredSelection(acc));
            else
                accountTreeViewer.setSelection(null);
            // selectAccount(acc);
            ret = 0;
        } catch (Exception e) {
            MBox.showError(shlPasswordMaker,
                    "Unable to locate account based on URL.\n" + e.getMessage());
            ret = 1;
        }

        return ret;
    }

    /**
     * Exits (disposes) this shell, prompting if db is dirty.
     */
    private void exit() {
        if (db.isDirty()) {
            switch (MBox.showYesNoCancel(shlPasswordMaker, EXIT_PROMPT)) {
            case SWT.YES:
                if (saveFile() == false)
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
     * 
     * @return A SecureCharArray object with the generated password.
     */
    private SecureCharArray generateOutput() {
        SecureCharArray mpw = null;
        SecureCharArray output = null;

        try {
            if (selectedAccount != null) {
                // If the username text has been edited by the user then it will
                // need to be updated
                // in the account. But since the account shouldn't be saved that
                // way, use a temporary
                // account.
                Account tempAccount = new Account();
                tempAccount.copySettings(selectedAccount);
                tempAccount.setUsername(editUsername.getText());
                tempAccount.setId(selectedAccount.getId());
                tempAccount.setUrl(editUrl.getText());

                mpw = new SecureCharArray(editMP.getText());

                output = pwm.makePassword(mpw, tempAccount);
            } else {
                output = new SecureCharArray();
            }
        } catch (Exception e) {
        } finally {
            if (mpw != null)
                mpw.erase();
        }

        return output;
    }

    /**
     * Creates and returns a thread which will countdown from whatever the
     * current countdown value is.
     * 
     * @param b
     *            The button to set the number of seconds left to.
     * @return The created thread.
     */
    private Thread getCountdownThread(Spinner spinner, int numSeconds) {
        final Spinner spinnerControl = spinner;
        final int countdownValue = numSeconds;

        return new Thread() {
            public void run() {
                boolean wasInterrupted = false;
                try {
                    // This is kinda dirty. A 'final' integer needs to be used
                    // inside the anonymous class
                    // so currentI is created each iteration. Gross.
                    for (int i = 0; i < countdownValue && wasInterrupted==false; ++i) {
                        final int currentI = i;
                        display.asyncExec(new Runnable() {
                            public void run() {
                                spinnerControl.setSelection(countdownValue - currentI);
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException iExc) {
                    wasInterrupted = true;
                }

                // gross again... the async execution below needs a final var
                final boolean anotherWasInterrupted = wasInterrupted;
                display.asyncExec(new Runnable() {
                    public void run() {
                        Utilities.clearClipboard();

                        if (anotherWasInterrupted==false && closeAfterTimer == true)
                            shlPasswordMaker.dispose();
                        else {
                            enableControlsForCopy();
                            int seconds = 5;
                            try {
                                seconds = Integer.parseInt(db.getGlobalSetting(GlobalSettingKey.CLIPBOARD_TIMEOUT));
                            } catch(NumberFormatException e) { }
                            editCopySeconds.setSelection(seconds);
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
        int seconds = 5;
        try {
            seconds = Integer.parseInt(db.getGlobalSetting(GlobalSettingKey.CLIPBOARD_TIMEOUT));
        } catch(NumberFormatException e) { }
        editCopySeconds.setSelection(seconds);
        btnShowPassword.setSelection(db.getGlobalSetting(
                GlobalSettingKey.SHOW_GEN_PW).compareTo("true") == 0);
        onShowPasswordClicked(); // due to manual "setSelection" not triggering
                                 // an event
    }

    private boolean loadPredefinedLocations() {
        // Scan the current directory looking for anything ending in ".rdf"
        try {
            String curDir = System.getProperty("user.dir");
            if (curDir != null) {
                File curDirFile = new File(curDir);
                File[] filelist = curDirFile.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getAbsolutePath().endsWith(".rdf");
                    }
                });

                for (File f : filelist) {
                    if (openFile(f.getAbsolutePath(), true))
                        return true;
                }
            }
        } catch (Exception e) {

        }

        // Now try the known home-directory files
        try {
            String[] locations = { "passwordmaker.rdf", ".passwordmaker.rdf", "pwmje.rdf",
                    ".pwmje.rdf", ".passwordmakerrc", };
            String home = System.getProperty("user.home");
            if (home != null) {
                for (String pathPiece : locations) {
                    String fullPath = home + File.separator + pathPiece;
                    if (openFile(fullPath, true) == true) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * Loads the various fields with data from the config files.
     */
    private void loadFromCmdLineSettings() {
        if (cmdLineSettings.matchUrl != null)
            editUrlSearch.setText(cmdLineSettings.matchUrl);
        if (cmdLineSettings.inputFilename != null) {
            openFile(cmdLineSettings.inputFilename, false);
        } else {
            if (loadPredefinedLocations() == false)
                newFile();
        }

        // Only attempt an initial find if something was passed on the
        // commandline
        if (cmdLineSettings.matchUrl != null
                && cmdLineSettings.matchUrl.length() > 0)
            findAccountByUrl();
    }

    /**
     * Sets the title of the window based on the current data state.
     */
    private void setTitle() {
        String title = (currentFilename.trim().length() == 0 ? "Untitled"
                : currentFilename)
                + " - "
                + TITLE_STRING
                + " - "
                + buildInfo.getVersion();
        String dirty = " ";
        if (db != null && db.isDirty())
            dirty = "* ";
        shlPasswordMaker.setText(dirty + title);
    }

    /**
     * Creates a new blank(sorta) database. This will attempt to save first if
     * the current database is dirty and abort the new file operation if the
     * save fails.
     * 
     * @return
     */
    private boolean newFile() {
        if (db != null && db.isDirty()) {
            switch (MBox.showYesNoCancel(shlPasswordMaker, EXIT_PROMPT)) {
            case SWT.YES:
                // Attempt to save the file, if that fails then abort the new
                // file operation
                if (saveFile() == false)
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

        try {
            db.addDefaultAccount();

            // TODO: This 2nd "setInput" is to work around a problem with the
            // DatabaseListener not getting
            // messages - I'm not exactly sure why.
            accountTreeViewer.setInput(db);
            selectFirstAccount();
            setGuiFromGlobalSettings();
            db.setDirty(false);
        } catch (Exception e) {
            // This REALLY should be impossible ... but, handle it anyway
            MBox.showError(shlPasswordMaker,
                    "Unable to create default account.\n" + e.getMessage());
        }

        selectFirstAccount();

        return true;
    }

    /**
     * Handles when focus is gained on the accountFilter text box.
     */
    private void onAccountFilterTextFocusGained() {
        String text = accountFilterText.getText();

        if (text.compareTo(ACCOUNT_FILTER_DESC) == 0)
            accountFilterText.setText("");

        /* Clear the italics */
        accountFilterText.setFont(regularSearchFont);

        filterIcon.setVisible(false);

        accountFilterText.selectAll();
    }

    /**
     * Handles when focus is lost from the accountFilter text box.
     */
    private void onAccountFilterTextFocusLost() {
        String text = accountFilterText.getText();
        if (text.length() == 0) {
            accountFilterText.setText(ACCOUNT_FILTER_DESC);
            accountFilterText.setFont(italicsSearchFont);
            filterIcon.setImage(searchImage);
        } else {
            accountFilterText.setFont(regularSearchFont);
            filterIcon.setImage(cancelImage);
        }
        filterIcon.setVisible(true);
    }

    /**
     * Invoked when the account menu is about to be shown.
     */
    private void onAccountMenuShown() {
        if (selectedAccount != null) {
            if (selectedAccount.isFolder()) {
                menuItemEditGroup.setEnabled(true);
                menuItemDeleteGroup.setEnabled(true);
                menuItemEditAccount.setEnabled(false);
                menuItemDeleteAccount.setEnabled(false);
                menuItemSort.setEnabled(true);
            } else {
                menuItemEditGroup.setEnabled(false);
                menuItemDeleteGroup.setEnabled(false);
                menuItemEditAccount.setEnabled(true);
                menuItemDeleteAccount
                        .setEnabled(selectedAccount.isDefault() == false
                                && selectedAccount.isRoot() == false);
                menuItemSort.setEnabled(true);
            }
        } else {
            menuItemEditGroup.setEnabled(false);
            menuItemDeleteGroup.setEnabled(false);
            menuItemEditAccount.setEnabled(false);
            menuItemDeleteAccount.setEnabled(false);
            menuItemSort.setEnabled(false);
        }
    }

    /**
     * Method invoked when the copy-and-exit button is clicked.
     * 
     * @param btn
     *            The button clicked.
     */
    private void onCopyToClipboard() {
        // If a thread is already running, the button serves as a cancellation
        if(countdownThread!=null && countdownThread.isAlive()) {
            countdownThread.interrupt();
        }
        else {
            // First make sure the seconds-value is valid. If not, set it to 5 and
            // carry on.
            int seconds;
    
            closeAfterTimer = comboCopyBehavior.getSelectionIndex() == 0 ? false
                    : true;
    
            // Perform a dirty check if the app will close after the operation
            if (closeAfterTimer && db.isDirty()) {
                switch (MBox.showYesNoCancel(shlPasswordMaker, EXIT_PROMPT)) {
                case SWT.YES:
                    // Only continue if the save succeeded
                    if (saveFile() != true)
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
                if (seconds < 1) {
                    secondsDecoration.show();
                    editCopySeconds.setFocus();
                    return;
                }
    
                secondsDecoration.hide();
            } catch (Exception ee) {
                secondsDecoration.show();
                editCopySeconds.setFocus();
                return;
            }
    
            disableControlsForCopy();
    
            copyGeneratedToClipboard();
            countdownThread = getCountdownThread(editCopySeconds, seconds);
            if (countdownThread != null)
                countdownThread.start();
            else {
                MBox.showError(shlPasswordMaker,
                        "Unable to create a timer. Your password is on the clipboard and WILL NOT be erased. This is a bug, please file a bug report.");
            }
        }
    }

    /**
     * Handles the deletion of the selected account.
     */
    private void onDeleteAccount() {
        if (selectedAccount == null) {
            MBox.showError(
                    shlPasswordMaker,
                    "No account is selected for deletion, this should not be possible. Please file a bug report.");
            return;
        }

        String type = selectedAccount.isFolder() ? "folder" : "account";
        String msg = "Are you sure you wish to delete " + type + " '"
                + selectedAccount.getName() + "'";
        int numChildren = selectedAccount.getNestedChildCount();
        if (numChildren > 0)
            msg += " and it's " + numChildren + " children?";
        else
            msg += "?";

        if (MBox.showYesNo(shlPasswordMaker, msg) == SWT.YES) {
            Account nearestRelative = db.findNearestRelative(selectedAccount);
            db.removeAccount(selectedAccount);
            if (nearestRelative == null)
                accountTreeViewer.setSelection(null);
            else
                accountTreeViewer.setSelection(new StructuredSelection(
                        nearestRelative));
        }
    }

    private void onCloseWindow(Event e) {
        if (db.isDirty() == true) {
            switch (MBox.showYesNoCancel(shlPasswordMaker, EXIT_PROMPT)) {
            case SWT.YES:
                if (saveFile() == true) {
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
     * 
     * @param arg0
     *            The dispose event.
     */
    private void onDisposing(DisposeEvent arg0) {
        Utilities.clearClipboard();
        
        if (passwordImage != null)
            passwordImage.dispose();
        if (searchImage != null)
            searchImage.dispose();
        if (cancelImage != null)
            cancelImage.dispose();
        if (eyeImage != null)
            eyeImage.dispose();
        if (eyeClosedImage != null)
            eyeClosedImage.dispose();
        if (iconImage != null)
            iconImage.dispose();
        if (passwordFont != null)
            passwordFont.dispose();
        if (regularSearchFont != null)
            regularSearchFont.dispose();
        if (italicsSearchFont != null)
            italicsSearchFont.dispose();
        if (accountTreeModel != null)
            accountTreeModel.dispose();
        if (accountTreeLabelProvider != null)
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
        SecureCharArray mpw = null;
        if (selectedAccount != null) {
            try {
                mpw = new SecureCharArray(editMP.getText());
                dlg = new AccountDlg(selectedAccount, mpw, passwordFont, pwm,
                        eyeImage, eyeClosedImage, showPassword);

                // A copy of the edited account is returned if "ok" is clicked.
                Account newAccount = dlg.open();
                if (newAccount != null) {
                    selectedAccount.copySettings(newAccount);
                    db.changeAccount(selectedAccount);
                    // accountTreeViewer.refresh(account, true);

                    // The tree already has the account selected. Applying the
                    // same selection actually
                    // has the side-effect of unselecting the account. So
                    // instead just invoke the selectAccount()
                    // method which is normally invoked by the tree causing the
                    // selection.
                    selectAccount(selectedAccount);
                }
            } finally {
                if (mpw != null) {
                    mpw.erase();
                    mpw = null;
                }
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
     * Invoked when the filter composite is resized so that the text box and
     * filter icon can be re-aligned.
     * 
     * @param arg0
     *            The composite-resize event.
     */
    private void onFilterCompositeResized(ControlEvent arg0) {
        Composite frame = (Composite) arg0.widget;
        accountFilterText.setSize(frame.getSize());
        filterIcon.setLocation(frame.getSize().x - filterIcon.getSize().x - 1,
                1);
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

    /**
     * Invoked when the filter text is modified.
     * 
     * @param e
     *            The modification event.
     */
    private void onFilterModified(ModifyEvent e) {
        String text = accountFilterText.getText();
        if (text.compareTo(ACCOUNT_FILTER_DESC) == 0 || text.length() == 0) {
            isFiltering = false;
        } else {
            isFiltering = true;
        }

        if (accountTreeViewer != null) {
            accountTreeViewer.refresh();
            if (text.length() > 0) {
                accountTreeViewer.expandAll();
                selectFirstLeafAccount();
            }
        }
    }

    /**
     * Selects the first available account that is not a folder, eg a leaf node.
     * It takes into consideration filtering rules.
     */
    private void selectFirstLeafAccount() {
        // This is a gross hack that doesn't operate on what accountTreeViewer
        // tells us, and that's
        // because I can't for the life of me figure out how to get it to tell
        // me what leaf nodes
        // are visible. I *hate* SWT's documentation.
        //
        // This is inefficient as it is walking the entire account database tree
        // looking
        // for nodes that match the text, essentially re-filtering. If anyone
        // sees this and knows
        // how to get at the filtered data, please let me know.

        // For some reason only on OSX, selectFirstLeafAccount() gets called
        // before the database
        // is ever created. So...
        if (db == null)
            return;

        String filterText = accountFilterText.getText().toLowerCase();
        ArrayList<Account> parentStack = new ArrayList<Account>();
        parentStack.add(db.getRootAccount());

        while (parentStack.size() > 0) {
            Account parentAccount = parentStack.remove(0);

            for (Account child : parentAccount.getChildren()) {
                if (child.isFolder())
                    parentStack.add(child);
                else {
                    if (filterText.length() == 0
                            || child.getName().toLowerCase()
                                    .contains(filterText)) {
                        accountTreeViewer.setSelection(new StructuredSelection(
                                child));
                        return;
                    }
                }
            }
        }
    }

    private void onNewAccountSelected() {
        Account parentAccount = null;
        AccountDlg dlg = null;

        // If no account is selected, then default to the root
        if (selectedAccount == null)
            parentAccount = db.getRootAccount();
        else {
            // Otherwise decide if it will be a sibling of the selected account
            // or a child
            // of the selected group.
            parentAccount = selectedAccount;

            // If the parent is not a folder, it needs to be created as a
            // sibling. So locate
            // who the real parent is.
            if (parentAccount.isFolder() == false) {
                parentAccount = db.findParent(parentAccount);
                if (parentAccount == null) {
                    MBox.showError(shlPasswordMaker,
                            "Unable to locate parent account of '"
                                    + selectedAccount.getName() + "' id="
                                    + selectedAccount.getId()
                                    + ", cannot add new account.");
                    return;
                }
            }
        }

        // Create a new blank account with default settings and the dialog
        Account newAccount = new Account();
        SecureCharArray mpw = new SecureCharArray(editMP.getText());
        try {
            dlg = new AccountDlg(newAccount, mpw, passwordFont, pwm, eyeImage,
                    eyeClosedImage, showPassword);

            // A copy of the account is returned if "ok" is clicked.
            newAccount = dlg.open();
            if (newAccount != null) {
                newAccount.setId(Account.createId(newAccount));
                db.addAccount(parentAccount, newAccount);
            }
        } catch (Exception e) {
            MBox.showError(
                    shlPasswordMaker,
                    "While creating the new account, an error occurred. You should save your work to a new file and restart.\n"
                            + e.getMessage());
        } finally {
            mpw.erase();
            mpw = null;
        }
    }

    private void onNewGroupSelected() {
        Account parentAccount = null;
        AccountDlg dlg = null;

        // If no account is selected, then default to the root
        if (selectedAccount == null)
            parentAccount = db.getRootAccount();
        else {
            // Otherwise decide if it will be a sibling of the selected account
            // or a child
            // of the selected group.
            parentAccount = selectedAccount;

            // If the parent is not a folder, it needs to be created as a
            // sibling. So locate
            // who the real parent is.
            if (parentAccount.isFolder() == false) {
                parentAccount = db.findParent(parentAccount);
                if (parentAccount == null) {
                    MBox.showError(shlPasswordMaker,
                            "Unable to locate parent account of '"
                                    + selectedAccount.getName() + "' id="
                                    + selectedAccount.getId()
                                    + ", cannot add new group.");
                    return;
                }
            }
        }

        // Create a new blank account with default settings and the dialog
        Account newAccount = new Account();
        newAccount.setIsFolder(true);
        SecureCharArray mpw = new SecureCharArray(editMP.getText());
        try {
            dlg = new AccountDlg(newAccount, mpw, passwordFont, pwm, eyeImage,
                    eyeClosedImage, showPassword);
        } finally {
            mpw.erase();
            mpw = null;
        }

        // A copy of the account is returned if "ok" is clicked.
        newAccount = dlg.open();
        if (newAccount != null) {
            try {
                newAccount.setId(Account.createId(newAccount));
                db.addAccount(parentAccount, newAccount);
            } catch (Exception e) {
                MBox.showError(
                        shlPasswordMaker,
                        "While creating the new group, an error occurred. You should save your work to a new file and restart.\n"
                                + e.getMessage());
            }
        }
    }

    private void onSecondsFocusLost() {
        int numSeconds;

        try {
            numSeconds = editCopySeconds.getSelection();
            if (numSeconds < 1) {
                // TODO: should this also check for some kind of max?
                secondsDecoration.show();
                return;
            }

            db.setGlobalSetting(GlobalSettingKey.CLIPBOARD_TIMEOUT,
                    editCopySeconds.getText());
            secondsDecoration.hide();
        } catch (Exception e) {
            secondsDecoration.show();
        }
    }

    private void onSort() {
        // shouldn't be possible
        if (selectedAccount == null) {
            MBox.showError(shlPasswordMaker,
                    "An account must be selected before sorting can occur (how did you do this?).");
            return;
        }

        // Locate the parent if it is just an account
        Account parentAccount = selectedAccount;
        if (selectedAccount.isFolder() == false) {
            parentAccount = db.findParent(selectedAccount);
            if (parentAccount == null) {
                MBox.showError(shlPasswordMaker,
                        "Unable to locate parent account for "
                                + selectedAccount.getName()
                                + " (how did you do this?).");
                return;
            }
        }

        int style = SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL;
        if (Utilities.isMac())
            style = SWT.SHEET;

        SortDlg dlg = new SortDlg(shlPasswordMaker, style, sortOptions);
        SortOptions newOptions = null;
        newOptions = dlg.open();
        if (newOptions != null) {
            sortOptions = newOptions;
            Collections.sort(parentAccount.getChildren(),
                    new AccountComparator(sortOptions));

            if (parentAccount.isRoot())
                accountTreeViewer.refresh(null, true);
            else
                accountTreeViewer.refresh(parentAccount, true);

            db.setDirty(true);
        }
    }

    /**
     * Invoked when the eyeball is clicked.
     */
    private void onShowPasswordClicked() {
        showPassword = btnShowPassword.getSelection();
        if (showPassword)
            btnShowPassword.setImage(eyeImage);
        else
            btnShowPassword.setImage(eyeClosedImage);

        db.setGlobalSetting(GlobalSettingKey.SHOW_GEN_PW,
                Boolean.toString(showPassword));

        regeneratePasswordAndDraw();
    }

    /**
     * Invoked when the URL field is modified. Causes the accounts to be
     * searched for anything matching the current URL text.
     * 
     * @param arg0
     *            Ignored, can be null.
     */
    private void onUrlSearchModified(ModifyEvent arg0) {
        findAccountByUrl();
    }

    /**
     * Opens up an "open" dialog and then opens the selected file.
     * 
     * @return true on success.
     */
    private boolean openFile() {
        if (db != null && db.isDirty()) {
            switch (MBox.showYesNoCancel(shlPasswordMaker, EXIT_PROMPT)) {
            case SWT.YES:
                if (saveFile() == false)
                    return false;
                break;

            case SWT.NO:
                break;

            case SWT.CANCEL:
                return false;
            }
        }

        FileDialog fd = new FileDialog(shlPasswordMaker, SWT.OPEN);
        fd.setText("Open RDF File");
        fd.setFilterExtensions(new String[] { "*.rdf", "*.*" });
        String selected = fd.open();
        if (selected != null && selected.length() > 0)
            return openFile(selected, false);
        return false;
    }

    /**
     * Reads a file in and sets up the necessary widgets with the data. If this
     * fails then it will create a new empty database and return false.
     * 
     * @param filename
     *            The filename (assumes RDF).
     * @param inhibitErrors
     *            If true, failures to load will not be displayed. This is used
     *            for auto-loading of passwordmaker databases.
     * @return true on success.
     */
    private boolean openFile(String filename, boolean inhibitErrors) {
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
            currentFilename = filename;

            // Widget setup
            accountTreeViewer.setInput(db);

            selectFirstAccount();
            setGuiFromGlobalSettings();

            db.setDirty(false);
            ret = true;
        } catch (Exception ex) {
            currentFilename = "";
            db = new Database();
            db.addDatabaseListener(this);
            accountTreeViewer.setInput(db);

            if (!inhibitErrors)
                MBox.showError(shlPasswordMaker, "Unable to open " + filename
                        + "\n" + ex.getMessage());
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (Exception exinner) {
            }
        }

        return ret;
    }

    /**
     * Causes the password to be regenerated.
     * 
     * This calculates the new generated password and draws it to the image
     * which is used to display its value. A standard text control is not used
     * because they take a "String" which cannot be reliably erased when
     * finished. The password is drawn character by character so the full string
     * never sits on the stack anywhere.
     */
    private void regeneratePasswordAndDraw() {
        SecureCharArray output = null;
        GC gc = null;

        try {
            updateUrlFromInputUrl();

            gc = new GC(passwordImage);
            gc.setBackground(Display.getCurrent().getSystemColor(
                    SWT.COLOR_BLACK));
            gc.setForeground(Display.getCurrent().getSystemColor(
                    SWT.COLOR_WHITE));
            gc.fillRectangle(canvasOutput.getClientArea());

            if (selectedAccount != null && selectedAccount.isFolder() == false
                    && editMP.getText().length() > 0) {
                output = generateOutput();
                if (output != null && showPassword == true) {
                    gc.setFont(passwordFont);
                    int x = 0;
                    int xPos = 5;
                    int yPos = 1;

                    if (Utilities.isMac())
                        yPos = 7;

                    // TODO: really should calculate the text extents and center
                    // off of it
                    for (x = 0; x < output.getData().length; x++) {
                        char strBytes[] = { output.getData()[x] };
                        String str = new String(strBytes);
                        gc.drawText(str, xPos, yPos);
                        xPos += gc.stringExtent(str).x + 2;
                    }
                }
            }

            canvasOutput.redraw();
        } catch (Exception e) {
        } finally {
            if (output != null)
                output.erase();
            if (gc != null)
                gc.dispose();
        }
    }

    /**
     * Attempts to save a file to the current filename. If there is no current
     * file name, then save-as is invoked. This also clears the dirty status on
     * success.
     * 
     * @return true on success.
     */
    private boolean saveFile() {
        boolean ret = false;

        // If we don't have a filename yet, call saveAs() which will call this
        // function
        // in return with a filename set.
        if (currentFilename.trim().length() == 0) {
            return saveFileAs();
        }

        try {
            RDFDatabaseWriter out = new RDFDatabaseWriter();
            File newFile = new File(currentFilename);
            if (newFile.exists() == false)
                newFile.createNewFile();

            FileOutputStream fout = new FileOutputStream(newFile);
            out.write(fout, db);
            db.setDirty(false);
            ret = true;
        } catch (Exception e) {
            MBox.showError(shlPasswordMaker, "Unable to save to "
                    + currentFilename + ".\n" + e.getMessage());
        }

        return ret;
    }

    /**
     * Opens up a dialog-box allowing the user to select a file to save to. This
     * will invoke saveFile behind the scenes and update currentFilename on
     * success.
     * 
     * @return true on success.
     */
    private boolean saveFileAs() {
        FileDialog fd = new FileDialog(shlPasswordMaker, SWT.SAVE);
        fd.setText("Save RDF As");
        fd.setFilterExtensions(new String[] { "*.rdf", "*.*" });
        String selected = fd.open();
        if (selected != null && selected.length() > 0) {
            String oldFilename = currentFilename;
            currentFilename = selected;

            File fileTest = new File(selected);
            if (fileTest.exists()) {
                if (MBox.showYesNo(shlPasswordMaker, "File " + selected
                        + " already exists, would you like to replace it?") != SWT.YES)
                    return false;
            }

            if (saveFile() == true) {
                return true;
            }

            // it failed if we get here, restore the filename
            currentFilename = oldFilename;
        }

        return false;
    }

    /**
     * Called when an account is selected. This is invoked by the tree when the
     * selection is made.
     * 
     * @param acc
     *            The new account selection.
     */
    private void selectAccount(Account acc) {
        // Store the newly selected account
        selectedAccount = acc;
        if (acc != null) {
            btnCopyToClipboard.setEnabled(selectedAccount.isFolder() == false);
            editAccount.setText(selectedAccount.getName());
            editDesc.setText(selectedAccount.getDesc());
            editUsername.setText(selectedAccount.getUsername());

            // When the default account is selected, the user can enter an URL
            // to have
            // the account settings applied against.
            if (acc.isDefault()) {
                editInputUrl.setEnabled(true);
                lblInputUrl.setEnabled(true);
                editInputUrl.setText("");

                urlSearchEnabled = false;
                editUrlSearch.setText("");
                urlSearchEnabled = true;
            } else {
                editInputUrl.setEnabled(false);
                lblInputUrl.setEnabled(false);
                editUrl.setText(acc.getUrl());
            }

        } else {
            btnCopyToClipboard.setEnabled(false);
            editAccount.setText("NO ACCOUNT SELECTED");
            editDesc.setText("");
            editUsername.setText("");
        }

        regeneratePasswordAndDraw();
    }

    private void selectFirstAccount() {
        if (db.getRootAccount().getChildren().size() > 0)
            accountTreeViewer.setSelection(new StructuredSelection(db
                    .getRootAccount().getChildren().get(0)));
        else
            accountTreeViewer.setSelection(null);
    }

    /**
     * Updates editUrl with modified text from editInputUrl based in the rules
     * of the current account if it is the default account.
     */
    private void updateUrlFromInputUrl() {
        if (selectedAccount != null) {
            if (selectedAccount.isDefault()) {
                String origUrl = editInputUrl.getText();
                String newUrl = pwm.getModifiedInputText(origUrl,
                        selectedAccount);
                editUrl.setText(newUrl);
            }
        }
    }

    // ////////////////////////////////////////////////////////////
    //
    // DATABASELISTENER INTERFACE
    //
    // ////////////////////////////////////////////////////////////

    @Override
    public void accountAdded(Account parent, Account account) {
        // I'm not sure why, but if you add a node off the root level,
        // refreshing the root node
        // will not make it show up. Refreshing the whole tree does.
        if (parent.isRoot())
            accountTreeViewer.refresh();
        else
            accountTreeViewer.refresh(parent);

        accountTreeViewer.setSelection(new StructuredSelection(account));
    }

    @Override
    public void accountRemoved(Account parent, Account account) {
        // I'm not sure why, but if you remove a node off the root level,
        // refreshing the root node
        // will not make it show up. Refreshing the whole tree does.
        if (parent.isRoot())
            accountTreeViewer.refresh();
        else
            accountTreeViewer.refresh(parent);
    }

    @Override
    public void accountChanged(Account account) {
        // accountTreeViewer.refresh(account);
        accountTreeViewer.update(account, null);
    }

    @Override
    public void dirtyStatusChanged(boolean status) {
        setTitle();
    }
}
