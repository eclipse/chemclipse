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
 * Christoph Läubrich - make UI configurable
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.TableConfigSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.ProcessingWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageMethods;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ConfigurableUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.MethodListUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.MethodUIConfig;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ExtendedMethodUI extends Composite implements ConfigurableUI<MethodUIConfig> {

	private static final Logger logger = Logger.getLogger(ExtendedMethodUI.class);
	//
	private Composite toolbarHeader;
	private Label labelDataInfo;
	private Text textOperator;
	private Text textDescription;
	private Button buttonAdd;
	private Button buttonRemove;
	private Button buttonMoveUp;
	private Button buttonMoveDown;
	private Button buttonModifySettings;
	private Button buttonResetSettings;
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
		createToolbarBottom(composite);
		//
		PartSupport.setCompositeVisibility(toolbarHeader, false);
		enableTableButtons();
		listUI.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				if(isEnabled() && buttonModifySettings.isEnabled()) {
					executeModifySettings(getShell());
				}
			}
		});
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
	}

	private void createToolbarBottom(Composite parent) {

		buttons = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		buttons.setLayoutData(gridData);
		buttons.setLayout(new GridLayout(6, false));
		//
		buttonAdd = createAddButton(buttons);
		buttonRemove = createRemoveButton(buttons);
		buttonMoveUp = createMoveUpButton(buttons);
		buttonMoveDown = createMoveDownButton(buttons);
		buttonModifySettings = createModifySettingsButton(buttons);
		buttonResetSettings = createResetSettingsButton(buttons);
	}

	private Button createAddButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add a process method.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					ProcessingWizard wizard = new ProcessingWizard(processingSupport);
					WizardDialog wizardDialog = new WizardDialog(e.display.getActiveShell(), wizard);
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
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Button createRemoveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Remove the selected process method(s).");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

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
					}
				}
			}
		});
		//
		return button;
	}

	private Button createMoveUpButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move the process method(s) up.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					Table table = listUI.getTable();
					for(int index : table.getSelectionIndices()) {
						Collections.swap(processMethod, index, index - 1);
					}
					updateProcessMethod();
				}
			}
		});
		//
		return button;
	}

	private Button createMoveDownButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move the process method(s) down.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					Table table = listUI.getTable();
					int[] indices = table.getSelectionIndices();
					for(int i = indices.length - 1; i >= 0; i--) {
						int index = indices[i];
						Collections.swap(processMethod, index, index + 1);
					}
					updateProcessMethod();
				}
			}
		});
		//
		return button;
	}

	private Button createModifySettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Modify the process method settings.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				executeModifySettings(parent.getShell());
			}
		});
		//
		return button;
	}

	private Button createResetSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the process method settings.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

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
		return button;
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
