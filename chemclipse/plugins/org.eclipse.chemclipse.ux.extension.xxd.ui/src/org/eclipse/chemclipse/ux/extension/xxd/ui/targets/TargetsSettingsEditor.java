/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.targets;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.template.TargetTemplate;
import org.eclipse.chemclipse.model.identifier.template.TargetTemplates;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverterSupport;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.support.DatabaseImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TargetTemplateInputValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsUIProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

public class TargetsSettingsEditor implements SettingsUIProvider.SettingsUIControl, IExtendedPartUI {

	private static final int WARN_NUMBER_IMPORT_ENTRIES = 500;
	//
	public static final String DESCRIPTION = "Target List";
	public static final String FILE_EXTENSION = ".txt";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
	private Composite control;
	//
	private Button buttonToolbarSearch;
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	//
	private TargetTemplates settings = new TargetTemplates();
	private TargetTemplateListUI listUI;
	//
	private List<Listener> listeners = new ArrayList<>();
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IProcessorPreferences<TargetTemplates> preferences = null;

	public TargetsSettingsEditor(Composite parent, IProcessorPreferences<TargetTemplates> preferences, TargetTemplates targetTemplates) {

		/*
		 * Populate the settings on demand.
		 */
		this.preferences = preferences;
		if(targetTemplates != null) {
			this.settings.load(targetTemplates.save());
		}
		//
		control = createControl(parent);
	}

	@Override
	public void setEnabled(boolean enabled) {

		listUI.getControl().setEnabled(enabled);
	}

	@Override
	public IStatus validate() {

		return ValidationStatus.ok();
	}

	@Override
	public String getSettings() throws IOException {

		if(preferences != null) {
			TargetTemplates settingz = new TargetTemplates();
			settingz.load(settings.save());
			return preferences.getSerialization().toString(settingz);
		}
		return "";
	}

	@Override
	public void addChangeListener(Listener listener) {

		listeners.add(listener);
	}

	@Override
	public Control getControl() {

		return control;
	}

	public void load(String entries) {

		settings.load(entries);
		setTableViewerInput();
	}

	public String getValues() {

		return settings.save();
	}

	private Composite createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		//
		createButtonSection(composite);
		createToolbarSearch(composite);
		createTableSection(composite);
		//
		initialize();
		//
		return composite;
	}

	private void initialize() {

		enableToolbar(toolbarSearch, buttonToolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH, false);
	}

	private void createButtonSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(9, false));
		//
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		createButtonAdd(composite);
		createButtonEdit(composite);
		createButtonRemove(composite);
		createButtonRemoveAll(composite);
		createButtonImportDB(composite);
		createButtonImport(composite);
		createButtonExport(composite);
		createButtonSave(composite);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				listUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createTableSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		//
		listUI = new TargetTemplateListUI(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		setTableViewerInput();
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add a target template.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(e.display.getActiveShell(), "Target", "You can create a new target here.", "Styrene | 100-42-5 | comment | contributor | referenceId", new TargetTemplateInputValidator(settings.keySet()));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					TargetTemplate targetTemplate = settings.extractTargetTemplate(item);
					if(targetTemplate != null) {
						settings.add(targetTemplate);
						setTableViewerInput();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Edit the selected target template.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = (IStructuredSelection)listUI.getSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof TargetTemplate) {
					Set<String> keySetEdit = new HashSet<>();
					keySetEdit.addAll(settings.keySet());
					TargetTemplate targetTemplate = (TargetTemplate)object;
					keySetEdit.remove(targetTemplate.getName());
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), "Target", "Edit the target.", settings.extractTargetTemplate(targetTemplate), new TargetTemplateInputValidator(keySetEdit));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						TargetTemplate targetTemplateNew = settings.extractTargetTemplate(item);
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
		button.setText("");
		button.setToolTipText("Remove the selected target templates.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), "Target Templates", "Do you want to delete the selected target templates?")) {
					IStructuredSelection structuredSelection = (IStructuredSelection)listUI.getSelection();
					for(Object object : structuredSelection.toArray()) {
						if(object instanceof TargetTemplate) {
							settings.remove(((TargetTemplate)object).getName());
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
		button.setText("");
		button.setToolTipText("Remove all target templates.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), "Target Templates", "Do you want to delete all target templates?")) {
					settings.clear();
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImportDB(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Import a target templates from a library.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MASS_SPECTRUM_LIBRARY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				try {
					DatabaseConverterSupport databaseConverterSupport = DatabaseConverter.getDatabaseConverterSupport();
					FileDialog fileDialog = new FileDialog(e.display.getActiveShell(), SWT.READ_ONLY);
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
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(e.display.getActiveShell());
						DatabaseImportRunnable databaseImportRunnable = new DatabaseImportRunnable(file);
						try {
							dialog.run(false, false, databaseImportRunnable);
							IMassSpectra massSpectra = databaseImportRunnable.getMassSpectra();
							if(massSpectra.size() > WARN_NUMBER_IMPORT_ENTRIES) {
								if(MessageDialog.openQuestion(e.display.getActiveShell(), "Import", "Do you really want to import " + massSpectra.size() + " target entries?")) {
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

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Import a target list.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText("Target List");
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER));
				String pathname = fileDialog.open();
				if(pathname != null) {
					File file = new File(pathname);
					String path = file.getParentFile().getAbsolutePath();
					preferenceStore.putValue(PreferenceConstants.P_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER, path);
					settings.importItems(file);
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Export the target list.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText("Target List");
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFileName(FILE_NAME);
				fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER));
				String pathname = fileDialog.open();
				if(pathname != null) {
					File file = new File(pathname);
					String path = file.getParentFile().getAbsolutePath();
					preferenceStore.putValue(PreferenceConstants.P_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER, path);
					if(settings.exportItems(file)) {
						MessageDialog.openInformation(button.getShell(), "Target List", "The target list has been exported successfully.");
					} else {
						MessageDialog.openWarning(button.getShell(), "Target List", "Something went wrong to export the target list.");
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonSave(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Save the target list.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				settings.save();
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
				TargetTemplate targetTemplate = new TargetTemplate();
				targetTemplate.setName(libraryInformation.getName());
				targetTemplate.setCasNumber(libraryInformation.getCasNumber());
				targetTemplate.setComments(libraryInformation.getComments());
				targetTemplate.setContributor(libraryInformation.getContributor());
				targetTemplate.setReferenceId(libraryInformation.getReferenceIdentifier());
				//
				settings.add(targetTemplate);
			}
		}
		setTableViewerInput();
	}

	private void setTableViewerInput() {

		listUI.setInput(settings.values());
	}
}
