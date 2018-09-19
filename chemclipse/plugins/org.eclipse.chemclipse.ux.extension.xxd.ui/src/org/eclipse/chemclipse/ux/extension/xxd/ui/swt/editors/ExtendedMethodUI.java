/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.chemclipse.chromatogram.filter.impl.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessMethods;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.ProcessingWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.MethodListUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ExtendedMethodUI {

	private static final Logger logger = Logger.getLogger(ExtendedMethodUI.class);
	//
	private Label labelDataInfo;
	private Button buttonRemove;
	private Button buttonMoveUp;
	private Button buttonMoveDown;
	private Button buttonProcessSettings;
	private MethodListUI listUI;
	//
	private ProcessMethods processMethods = null;

	public ExtendedMethodUI(Composite parent) {
		initialize(parent);
	}

	public void update(ProcessMethods processMethods) {

		this.processMethods = new ProcessMethods();
		this.processMethods.add(new ProcessMethod("msd.filter.id", "RT Filter", "Set the retention time range.", "{\"Start RT (Minutes)\":10.16,\"Stop RT (Minutes)\":13.45}", "MSD"));
		this.processMethods.add(new ProcessMethod("msd.sg.id", "SG Filter", "Savitzky-Golay smoothing.", "", "XXD"));
		this.processMethods.add(new ProcessMethod("csd.unitsum", "Unit Filter", "Unit Sum Normalizer.", "", "CSD"));
		//
		updateList();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		createTable(parent);
		createToolbarBottom(parent);
		//
		enableTableButtons();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		createDataInfoLabel(composite);
		createSettingsButton(composite);
	}

	private void createDataInfoLabel(Composite parent) {

		labelDataInfo = new Label(parent, SWT.NONE);
		labelDataInfo.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		labelDataInfo.setLayoutData(gridData);
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePageSWT();
				preferencePage.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

	}

	private void createTable(Composite parent) {

		/*
		 * Don't enable multi-select for now.
		 * If yes, then adjust the move up, down methods.
		 */
		listUI = new MethodListUI(parent, SWT.BORDER);
		Table table = listUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableTableButtons();
			}
		});
	}

	private void createToolbarBottom(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createAddButton(composite);
		buttonRemove = createRemoveButton(composite);
		buttonMoveUp = createMoveUpButton(composite);
		buttonMoveDown = createMoveDownButton(composite);
		buttonProcessSettings = createProcessSettingsButton(composite);
	}

	private void createAddButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add a process method.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethods != null) {
					ProcessingWizard wizard = new ProcessingWizard();
					WizardDialog wizardDialog = new WizardDialog(e.widget.getDisplay().getActiveShell(), wizard);
					wizardDialog.setMinimumPageSize(ProcessingWizard.DEFAULT_WIDTH, ProcessingWizard.DEFAULT_HEIGHT);
					wizardDialog.create();
					//
					if(wizardDialog.open() == WizardDialog.OK) {
						IProcessMethod processMethod = wizard.getProcessMethod();
						processMethods.add(processMethod);
						updateList();
					}
				}
			}
		});
	}

	private Button createRemoveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Remove the selected process method(s).");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethods != null) {
					if(MessageDialog.openQuestion(e.widget.getDisplay().getActiveShell(), "Delete Process Method(s)", "Would you like to delete the selected processor(s)?")) {
						for(Object object : listUI.getStructuredSelection().toArray()) {
							processMethods.remove(object);
						}
						updateList();
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

				if(processMethods != null) {
					Table table = listUI.getTable();
					int index = table.getSelectionIndex();
					Collections.swap(processMethods, index, index - 1);
					updateList();
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

				if(processMethods != null) {
					Table table = listUI.getTable();
					int index = table.getSelectionIndex();
					Collections.swap(processMethods, index, index + 1);
					updateList();
				}
			}
		});
		//
		return button;
	}

	private Button createProcessSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Modify the process method settings.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethods != null) {
					Object object = listUI.getStructuredSelection().getFirstElement();
					if(object instanceof IProcessMethod) {
						IProcessMethod processMethod = (IProcessMethod)object;
						try {
							SettingsSupport settingsSupport = new SettingsSupport();
							String content = settingsSupport.getSettingsAsJson(null, SupplierFilterSettings.class, e.widget.getDisplay().getActiveShell());
							processMethod.setSettings(content);
							updateList();
						} catch(JsonParseException e1) {
							logger.warn(e1);
						} catch(JsonMappingException e1) {
							logger.warn(e1);
						} catch(IOException e1) {
							logger.warn(e1);
						}
					}
				}
			}
		});
		//
		return button;
	}

	private void updateList() {

		listUI.setInput(processMethods);
		enableTableButtons();
	}

	private void enableTableButtons() {

		boolean enabled = (listUI.getTable().getSelectionIndex() > -1) ? true : false;
		buttonRemove.setEnabled(enabled);
		buttonMoveUp.setEnabled(enabled);
		buttonMoveDown.setEnabled(enabled);
		buttonProcessSettings.setEnabled(enabled);
	}
}
