/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.instruments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.instruments.Instrument;
import org.eclipse.chemclipse.model.instruments.Instruments;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsUIProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.InstrumentListUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.validation.InstrumentInputValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

public class InstrumentsSettingsEditor implements SettingsUIProvider.SettingsUIControl, IExtendedPartUI {

	private static final String FILTER_EXTENSION = "*.txt";
	private static final String INSTRUMENTS = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.INSTRUMENTS);
	private static final String FILTER_NAME = INSTRUMENTS + "(" + FILTER_EXTENSION + ")";
	private static final String FILE_NAME = INSTRUMENTS + FILTER_EXTENSION;
	private static final String TITLE = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.INSTRUMENT);
	private static final String CREATE_NEW_INSTRUMENT = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.CREATE_NEW_INSTRUMENT);
	private static final String EDIT_SELECTED_INSTRUMENT = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.EDIT_SELECTED_INSTRUMENT);
	private static final String REMOVE_SELECTED_INSTRUMENT = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.REMOVE_SELECTED_INSTRUMENT);
	private static final String REALLY_DELETE_SELECTED_INSTRUMENT = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.REALLY_DELETE_SELECTED_INSTRUMENT);
	private static final String REMOVE_ALL_INSTRUMENTS = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.REMOVE_ALL_INSTRUMENTS);
	private static final String REALLY_REMOVE_ALL_INSTRUMENTS = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.REALLY_REMOVE_ALL_INSTRUMENTS);
	//
	private Composite control;
	//
	private Button buttonToolbarSearch;
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	//
	private Instruments settings = new Instruments();
	private InstrumentListUI listUI;
	//
	private List<Listener> listeners = new ArrayList<>();
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IProcessorPreferences<Instruments> preferences = null;

	public InstrumentsSettingsEditor(Composite parent, IProcessorPreferences<Instruments> preferences, Instruments instruments) {

		/*
		 * Populate the settings on demand.
		 */
		this.preferences = preferences;
		if(instruments != null) {
			this.settings.load(instruments.save());
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
			Instruments settingz = new Instruments();
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
		composite.setLayout(new GridLayout(8, false));
		//
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		createButtonAdd(composite);
		createButtonEdit(composite);
		createButtonRemove(composite);
		createButtonRemoveAll(composite);
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
		listUI = new InstrumentListUI(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		setTableViewerInput();
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(CREATE_NEW_INSTRUMENT);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(e.display.getActiveShell(), TITLE, CREATE_NEW_INSTRUMENT, "Instrument1 | GC-MS | Used for research and development.", new InstrumentInputValidator(settings.keySet()));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					Instrument instrument = settings.extractInstrument(item);
					if(instrument != null) {
						settings.add(instrument);
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
		button.setToolTipText(EDIT_SELECTED_INSTRUMENT);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = (IStructuredSelection)listUI.getSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof Instrument instrument) {
					Set<String> keySetEdit = new HashSet<>();
					keySetEdit.addAll(settings.keySet());
					keySetEdit.remove(instrument.getIdentifier());
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), TITLE, EDIT_SELECTED_INSTRUMENT, settings.extractInstrument(instrument), new InstrumentInputValidator(keySetEdit));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						Instrument instrumentNew = settings.extractInstrument(item);
						if(instrumentNew != null) {
							settings.remove(instrument);
							settings.add(instrumentNew);
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
		button.setToolTipText(REMOVE_SELECTED_INSTRUMENT);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), TITLE, REALLY_DELETE_SELECTED_INSTRUMENT)) {
					IStructuredSelection structuredSelection = (IStructuredSelection)listUI.getSelection();
					for(Object object : structuredSelection.toArray()) {
						if(object instanceof Instrument instrument) {
							settings.remove(instrument.getIdentifier());
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
		button.setToolTipText(REMOVE_ALL_INSTRUMENTS);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), TITLE, REALLY_REMOVE_ALL_INSTRUMENTS)) {
					settings.clear();
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.IMPORT_INSTRUMENT_LIST));
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.INSTRUMENT_LIST));
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_INSTRUMENTS_TEMPLATE_FOLDER));
				String pathname = fileDialog.open();
				if(pathname != null) {
					File file = new File(pathname);
					String path = file.getParentFile().getAbsolutePath();
					preferenceStore.putValue(PreferenceConstants.P_INSTRUMENTS_TEMPLATE_FOLDER, path);
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
		button.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.EXPORT_INSTRUMENT_LIST));
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.INSTRUMENT_LIST));
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFileName(FILE_NAME);
				fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_INSTRUMENTS_TEMPLATE_FOLDER));
				String pathname = fileDialog.open();
				if(pathname != null) {
					File file = new File(pathname);
					String path = file.getParentFile().getAbsolutePath();
					preferenceStore.putValue(PreferenceConstants.P_INSTRUMENTS_TEMPLATE_FOLDER, path);
					if(settings.exportItems(file)) {
						MessageDialog.openInformation(button.getShell(), TITLE, ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.INSTRUMENT_LIST_EXPORTED));
					} else {
						MessageDialog.openWarning(button.getShell(), TITLE, ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.INSTRUMENT_LIST_EXPORT_FAILED));
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
		button.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SAVE_INSTRUMENT_LIST));
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				settings.save();
			}
		});
		//
		return button;
	}

	private void setTableViewerInput() {

		listUI.setInput(settings.values());
	}
}
