/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - make UI configurable, support selection of existing process methods
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.TableConfigSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.ProcessingWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageMethods;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ConfigurableUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.MethodListUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.MethodUIConfig;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePage;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ExtendedMethodUI extends Composite implements ConfigurableUI<MethodUIConfig> {

	private static final Logger logger = Logger.getLogger(ExtendedMethodUI.class);
	//
	private static final String MENU_CATEGORY_STEPS = "Steps";
	//
	private Composite toolbarHeader;
	private Label labelDataInfo;
	private Text textOperator;
	private Text textDescription;
	private ToolItem buttonAdd;
	private Button buttonCopy;
	private ToolItem buttonRemove;
	private ToolItem buttonMoveUp;
	private ToolItem buttonMoveDown;
	private ToolItem buttonModifySettings;
	private ToolItem buttonResetSettings;
	private MethodListUI listUI;
	//
	private IProcessMethod processMethod = null;
	private IModificationHandler modificationHandler = null;
	private Composite toolbarMain;
	private Composite buttons;
	protected boolean showSettingsOnAdd;
	private ProcessTypeSupport processingSupport;

	public ExtendedMethodUI(Composite parent, int style) {
		this(parent, style, new ProcessTypeSupport());
	}

	public ExtendedMethodUI(Composite parent, int style, ProcessTypeSupport processingSupport) {
		super(parent, style);
		this.processingSupport = processingSupport;
		createControl();
	}

	public void update(IProcessMethod processMethod) {

		this.processMethod = processMethod;
		updateProcessMethod();
	}

	public void setModificationHandler(IModificationHandler modificationHandler) {

		this.modificationHandler = modificationHandler;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		toolbarHeader = createToolbarHeader(composite);
		createTable(composite);
		buttons = createToolbarBottom(composite);
		//
		PartSupport.setCompositeVisibility(toolbarHeader, false);
		enableTableButtons();
	}

	public Composite getToolbarMain() {

		return toolbarMain;
	}

	private void createToolbarMain(Composite parent) {

		toolbarMain = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		toolbarMain.setLayoutData(gridData);
		toolbarMain.setLayout(new GridLayout(3, false));
		//
		createDataInfoLabel(toolbarMain);
		createButtonToggleToolbarHeader(toolbarMain);
		createSettingsButton(toolbarMain);
	}

	private void createDataInfoLabel(Composite parent) {

		labelDataInfo = new Label(parent, SWT.NONE);
		labelDataInfo.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		labelDataInfo.setLayoutData(gridData);
	}

	private Button createButtonToggleToolbarHeader(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the header toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEADER_DATA, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarHeader);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEADER_DATA, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEADER_DATA, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageProcessing = new PreferencePage();
				preferencePageProcessing.setTitle("Processing");
				//
				IPreferencePage preferencePageMethods = new PreferencePageMethods();
				preferencePageMethods.setTitle("Methods");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageProcessing));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageMethods));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		setDirty(true);
	}

	private Text createOperatorSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Operator:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Operator");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setOperator(text.getText().trim());
					setDirty(true);
				}
			}
		});
		//
		return text;
	}

	private Text createDescriptionSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Description:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Description");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setDescription(text.getText().trim());
					setDirty(true);
				}
			}
		});
		//
		return text;
	}

	private Composite createToolbarHeader(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		textOperator = createOperatorSection(composite);
		textDescription = createDescriptionSection(composite);
		//
		return composite;
	}

	private void createTable(Composite parent) {

		listUI = new MethodListUI(parent, SWT.BORDER | SWT.MULTI);
		Table table = listUI.getTable();
		GridData gridData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gridData);
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableTableButtons();
			}
		});
		//
		Shell shell = listUI.getTable().getShell();
		ITableSettings tableSettings = listUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		listUI.applySettings(tableSettings);
		//
		listUI.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				if(isEnabled() && buttonModifySettings.isEnabled()) {
					executeModifySettings(getShell());
				}
			}
		});
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Step(s)";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_STEPS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteSteps(shell);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void deleteSteps(Shell shell) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Processing Step(s)");
		messageBox.setMessage("Would you like to delete the selected processing step(s)?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Delete Step(s)
			 */
			Iterator iterator = listUI.getStructuredSelection().iterator();
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IProcessEntry) {
					deleteStep((IProcessEntry)object);
				}
			}
			updateProcessMethod();
		}
	}

	private void deleteStep(IProcessEntry processEntry) {

		if(processMethod != null) {
			processMethod.remove(processEntry);
		}
	}

	private void addKeyEventProcessors(Shell shell, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					deleteSteps(shell);
				}
			}
		});
	}

	private ToolBar createToolbarBottom(Composite parent) {

		ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		toolBar.setLayoutData(gridData);
		buttonAdd = createAddButton(toolBar);
		buttonRemove = createRemoveButton(toolBar);
		buttonCopy = createCopyButton(toolBar);
		buttonMoveUp = createMoveUpButton(toolBar);
		buttonMoveDown = createMoveDownButton(toolBar);
		buttonModifySettings = createModifySettingsButton(toolBar);
		buttonResetSettings = createResetSettingsButton(toolBar);
		return toolBar;
	}

	private ToolItem createAddButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Add a process method.");
		final Menu menu = new Menu(toolBar.getShell(), SWT.POP_UP);
		toolBar.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				menu.dispose();
			}
		});
		item.addListener(SWT.Selection, event -> {
			if(event.detail == SWT.ARROW) {
				Rectangle rect = item.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = toolBar.toDisplay(pt);
				for(MenuItem menuItem : menu.getItems()) {
					menuItem.dispose();
				}
				List<File> files = MethodSupportUI.getMethodFiles(Activator.getDefault().getPreferenceStore());
				for(File file : files) {
					MenuItem menuItem = new MenuItem(menu, SWT.NONE);
					menuItem.setText(file.getName());
					menuItem.addSelectionListener(new SelectionListener() {

						@Override
						public void widgetSelected(SelectionEvent e) {

							loadMethodFile(file);
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {

						}
					});
				}
				if(!files.isEmpty()) {
					new MenuItem(menu, SWT.SEPARATOR);
				}
				MenuItem loadItem = new MenuItem(menu, SWT.NONE);
				loadItem.setText("Load from file...");
				loadItem.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						FileDialog fileDialog = new FileDialog(toolBar.getShell(), SWT.OPEN);
						fileDialog.setText("Select Process Method file");
						fileDialog.setFileName(MethodConverter.DEFAULT_METHOD_FILE_NAME);
						fileDialog.setFilterExtensions(MethodConverter.DEFAULT_METHOD_FILE_EXTENSIONS);
						fileDialog.setFilterNames(MethodConverter.DEFAULT_METHOD_FILE_NAMES);
						//
						String filePath = fileDialog.open();
						if(filePath != null) {
							File file = new File(filePath);
							loadMethodFile(file);
						}
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			} else {
				if(processMethod != null) {
					ProcessingWizard wizard = new ProcessingWizard(processingSupport);
					WizardDialog wizardDialog = new WizardDialog(toolBar.getShell(), wizard);
					wizardDialog.setMinimumPageSize(ProcessingWizard.DEFAULT_WIDTH, ProcessingWizard.DEFAULT_HEIGHT);
					wizardDialog.create();
					//
					if(wizardDialog.open() == WizardDialog.OK) {
						IProcessEntry processEntry = wizard.getProcessEntry();
						if(processEntry != null) {
							processMethod.add(processEntry);
							if(showSettingsOnAdd) {
								modifyProcessEntry(getShell(), processEntry);
							}
							updateProcessMethod();
							select(Collections.singletonList(processEntry));
						}
					}
				}
			}
		});
		return item;
	}

	public void loadMethodFile(File file) {

		IProcessMethod method = Adapters.adapt(file, IProcessMethod.class);
		if(method != null) {
			processMethod.addAll(method);
			updateProcessMethod();
			select(method);
		}
	}

	private void select(List<? extends IProcessEntry> entries) {

		listUI.setSelection(new StructuredSelection(entries));
	}

	private Button createCopyButton(ToolBar parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Copy a process method.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COPY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					Table table = listUI.getTable();
					int index = table.getSelectionIndex();
					if(index > -1) {
						Object object = table.getItem(index).getData();
						if(object instanceof IProcessEntry) {
							IProcessEntry processEntry = (IProcessEntry)object;
							IProcessEntry processEntryCopy = new ProcessEntry(processEntry);
							processMethod.add(index, processEntryCopy);
							updateProcessMethod();
						}
					}
				}
			}
		});
		//
		return button;
	}

	private ToolItem createRemoveButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Remove the selected process method(s).");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					if(MessageDialog.openQuestion(e.display.getActiveShell(), "Delete Process Method(s)", "Would you like to delete the selected processor(s)?")) {
						for(Object object : listUI.getStructuredSelection().toArray()) {
							if(object instanceof IProcessEntry) {
								IProcessEntry processEntry = (IProcessEntry)object;
								processMethod.remove(processEntry);
							}
						}
						updateProcessMethod();
						select(Collections.emptyList());
					}
				}
			}
		});
		//
		return item;
	}

	private ToolItem createMoveUpButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Move the process method(s) up.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					Table table = listUI.getTable();
					ISelection selection = listUI.getSelection();
					for(int index : table.getSelectionIndices()) {
						Collections.swap(processMethod, index, index - 1);
					}
					updateProcessMethod();
					listUI.setSelection(selection);
				}
			}
		});
		//
		return item;
	}

	private ToolItem createMoveDownButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Move the process method(s) down.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					Table table = listUI.getTable();
					int[] indices = table.getSelectionIndices();
					ISelection selection = listUI.getSelection();
					for(int i = indices.length - 1; i >= 0; i--) {
						int index = indices[i];
						Collections.swap(processMethod, index, index + 1);
					}
					updateProcessMethod();
					listUI.setSelection(selection);
				}
			}
		});
		//
		return item;
	}

	private ToolItem createModifySettingsButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Modify the process method settings.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				executeModifySettings(toolBar.getShell());
			}
		});
		//
		return item;
	}

	private ToolItem createResetSettingsButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Reset the process method settings.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					for(Object object : listUI.getStructuredSelection().toArray()) {
						if(object instanceof IProcessEntry) {
							IProcessEntry processEntry = (IProcessEntry)object;
							processEntry.setJsonSettings(IProcessEntry.EMPTY_JSON_SETTINGS);
						}
					}
					updateProcessMethod();
				}
			}
		});
		//
		return item;
	}

	private void updateProcessMethod() {

		if(processMethod != null) {
			textOperator.setText(processMethod.getOperator());
			textDescription.setText(processMethod.getDescription());
		} else {
			textOperator.setText("");
			textDescription.setText("");
		}
		//
		listUI.setInput(processMethod);
		enableTableButtons();
		setDirty(true);
	}

	private void enableTableButtons() {

		buttonAdd.setEnabled(processMethod != null);
		//
		boolean enabled = (listUI.getTable().getSelectionIndex() > -1) ? true : false;
		buttonCopy.setEnabled(enabled);
		buttonRemove.setEnabled(enabled);
		buttonMoveUp.setEnabled(enabled);
		buttonMoveDown.setEnabled(enabled);
		buttonModifySettings.setEnabled(enabled);
		buttonResetSettings.setEnabled(enabled);
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		listUI.getControl().setEnabled(enabled);
		PartSupport.setCompositeVisibility(buttons, enabled);
	}

	private void setDirty(boolean dirty) {

		if(modificationHandler != null) {
			modificationHandler.setDirty(dirty);
		}
	}

	@Override
	public MethodUIConfig getConfig() {

		return new MethodUIConfig() {

			TableConfigSupport tabelConfig = new TableConfigSupport(listUI);

			@Override
			public void setToolbarVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarMain, visible);
			}

			@Override
			public boolean isToolbarVisible() {

				return toolbarMain.isVisible();
			}

			@Override
			public void setVisibleColumns(Set<String> visibleColumns) {

				tabelConfig.setVisibleColumns(visibleColumns);
			}

			@Override
			public Set<String> getColumns() {

				return tabelConfig.getColumns();
			}

			@Override
			public void setShowSettingsOnAdd(boolean showSettingsOnAdd) {

				ExtendedMethodUI.this.showSettingsOnAdd = showSettingsOnAdd;
			}
		};
	}

	private void executeModifySettings(Shell shell) {

		if(processMethod != null) {
			Object object = listUI.getStructuredSelection().getFirstElement();
			if(object instanceof IProcessEntry) {
				IProcessEntry processEntry = (IProcessEntry)object;
				modifyProcessEntry(shell, processEntry);
				updateProcessMethod();
			}
		}
	}

	public void modifyProcessEntry(Shell shell, IProcessEntry processEntry) {

		Class<? extends IProcessSettings> processSettingsClass = processEntry.getProcessSettingsClass();
		if(processSettingsClass != null) {
			try {
				SettingsSupport settingsSupport = new SettingsSupport();
				String content = settingsSupport.getSettingsAsJson(processEntry.getJsonSettings(), processSettingsClass, shell);
				processEntry.setJsonSettings(content);
			} catch(JsonParseException e1) {
				logger.warn(e1);
			} catch(JsonMappingException e1) {
				logger.warn(e1);
			} catch(IOException e1) {
				logger.warn(e1);
			}
		} else {
			logger.warn("Settings class is null: " + processEntry);
		}
	}
}
