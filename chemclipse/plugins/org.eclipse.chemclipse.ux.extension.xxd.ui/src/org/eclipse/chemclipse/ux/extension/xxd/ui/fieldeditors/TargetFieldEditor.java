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
package org.eclipse.chemclipse.ux.extension.xxd.ui.fieldeditors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.ITargetTemplate;
import org.eclipse.chemclipse.model.identifier.TargetTemplate;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverterSupport;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.support.DatabaseImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TargetTemplateInputValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.TargetTemplates;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.TargetTemplateListUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

public class TargetFieldEditor extends FieldEditor {

	private static final Logger logger = Logger.getLogger(TargetFieldEditor.class);
	//
	private static final int NUMBER_COLUMNS = 2;
	private static final int WARN_NUMBER_IMPORT_ENTRIES = 500;
	//
	private Composite composite;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private TargetTemplates targetTemplates = new TargetTemplates();
	private TargetTemplateListUI targetTemplateListUI;

	public TargetFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		//
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(NUMBER_COLUMNS, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		//
		createLabelSection(composite);
		createSearchSection(composite);
		createTableSection(composite);
		createButtonGroup(composite);
	}

	private void createSearchSection(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = NUMBER_COLUMNS;
		searchSupportUI.setLayoutData(gridData);
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				targetTemplateListUI.setSearchText(searchText, caseSensitive);
			}
		});
	}

	private void createLabelSection(Composite parent) {

		Label label = new Label(parent, SWT.LEFT);
		label.setText("");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = NUMBER_COLUMNS;
		label.setLayoutData(gridData);
	}

	private void createTableSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);
		//
		targetTemplateListUI = new TargetTemplateListUI(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		setTableViewerInput();
	}

	private void createButtonGroup(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(gridData);
		//
		setButtonLayoutData(createButtonAdd(composite));
		setButtonLayoutData(createButtonImport(composite));
		setButtonLayoutData(createButtonEdit(composite));
		setButtonLayoutData(createButtonRemove(composite));
		setButtonLayoutData(createButtonRemoveAll(composite));
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add");
		button.setToolTipText("Add a target template.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(button.getShell(), "Target", "You can create a new target here.", "Styrene | 100-42-5 | comment | contributor | referenceId", new TargetTemplateInputValidator(targetTemplates.keySet()));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					ITargetTemplate targetTemplate = targetTemplates.extractTargetTemplate(item);
					if(targetTemplate != null) {
						targetTemplates.add(targetTemplate);
						setTableViewerInput();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Import");
		button.setToolTipText("Import a target template(s) from a library.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				try {
					DatabaseConverterSupport databaseConverterSupport = DatabaseConverter.getDatabaseConverterSupport();
					FileDialog fileDialog = new FileDialog(button.getShell(), SWT.READ_ONLY);
					fileDialog.setText("Select a library to import");
					fileDialog.setFilterExtensions(databaseConverterSupport.getFilterExtensions());
					fileDialog.setFilterNames(databaseConverterSupport.getFilterNames());
					fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER));
					String pathname = fileDialog.open();
					if(pathname != null) {
						//
						File file = new File(pathname);
						String path = file.getParentFile().getAbsolutePath();
						preferenceStore.putValue(PreferenceConstants.P_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER, path);
						//
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(button.getShell());
						DatabaseImportRunnable databaseImportRunnable = new DatabaseImportRunnable(file);
						try {
							dialog.run(false, false, databaseImportRunnable);
							IMassSpectra massSpectra = databaseImportRunnable.getMassSpectra();
							if(massSpectra.size() > WARN_NUMBER_IMPORT_ENTRIES) {
								if(MessageDialog.openQuestion(button.getShell(), "Import", "Do you really want to import " + massSpectra.size() + " target entries?")) {
									addTargetTemplates(massSpectra);
								}
							} else {
								addTargetTemplates(massSpectra);
							}
							setTableViewerInput();
						} catch(InvocationTargetException e1) {
							logger.warn(e1);
						} catch(InterruptedException e1) {
							logger.warn(e1);
						}
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		//
		return button;
	}

	private void addTargetTemplates(IMassSpectra massSpectra) {

		for(IScanMSD scanMSD : massSpectra.getList()) {
			if(scanMSD instanceof ILibraryMassSpectrum) {
				/*
				 * Get the library
				 */
				ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)scanMSD;
				ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
				/*
				 * Transfer the target
				 */
				ITargetTemplate targetTemplate = new TargetTemplate();
				targetTemplate.setName(libraryInformation.getName());
				targetTemplate.setCasNumber(libraryInformation.getCasNumber());
				targetTemplate.setComments(libraryInformation.getComments());
				targetTemplate.setContributor(libraryInformation.getContributor());
				targetTemplate.setReferenceId(libraryInformation.getReferenceIdentifier());
				//
				targetTemplates.add(targetTemplate);
			}
		}
		setTableViewerInput();
	}

	private Button createButtonEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Edit");
		button.setToolTipText("Edit the selected target template.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = (IStructuredSelection)targetTemplateListUI.getSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof ITargetTemplate) {
					ITargetTemplate targetTemplate = (ITargetTemplate)object;
					InputDialog dialog = new InputDialog(button.getShell(), "Target", "Edit the target.", targetTemplates.extractTargetTemplate(targetTemplate), new TargetTemplateInputValidator(targetTemplates.keySet()));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						ITargetTemplate targetTemplateNew = targetTemplates.extractTargetTemplate(item);
						if(targetTemplateNew != null) {
							targetTemplate.setName(targetTemplateNew.getName());
							targetTemplate.setCasNumber(targetTemplateNew.getCasNumber());
							targetTemplate.setComments(targetTemplateNew.getComments());
							targetTemplate.setContributor(targetTemplateNew.getContributor());
							targetTemplate.setReferenceId(targetTemplateNew.getReferenceId());
							setTableViewerInput();
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRemove(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Remove");
		button.setToolTipText("Remove the selected target template(s).");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(button.getShell(), "Target Template(s)", "Do you want to delete the selected target template(s)?")) {
					IStructuredSelection structuredSelection = (IStructuredSelection)targetTemplateListUI.getSelection();
					for(Object object : structuredSelection.toArray()) {
						if(object instanceof ITargetTemplate) {
							targetTemplates.remove(((ITargetTemplate)object).getName());
						}
					}
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Remove All");
		button.setToolTipText("Remove all target template(s).");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(button.getShell(), "Target Template(s)", "Do you want to delete all target template(s)?")) {
					targetTemplates.clear();
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private void setTableViewerInput() {

		targetTemplateListUI.setInput(targetTemplates.values());
	}

	@Override
	protected void doLoad() {

		String entries = getPreferenceStore().getString(getPreferenceName());
		targetTemplates.load(entries);
		setTableViewerInput();
	}

	@Override
	protected void doLoadDefault() {

		String entries = getPreferenceStore().getDefaultString(getPreferenceName());
		targetTemplates.loadDefault(entries);
		setTableViewerInput();
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), targetTemplates.save());
	}

	@Override
	public int getNumberOfControls() {

		return 1;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		GridData gridData = (GridData)composite.getLayoutData();
		gridData.horizontalSpan = numColumns - 1;
		gridData.grabExcessHorizontalSpace = gridData.horizontalSpan == 1;
	}
}
