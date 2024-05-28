/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.vsd.filter.ui.swt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.chromatogram.vsd.filter.model.WavenumberSignal;
import org.eclipse.chemclipse.chromatogram.vsd.filter.model.WavenumberSignals;
import org.eclipse.chemclipse.chromatogram.vsd.filter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.vsd.filter.ui.internal.provider.WavenumberSignalsInputValidator;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.IChangeListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class WavenumberSignalsEditor extends Composite implements IChangeListener, IExtendedPartUI {

	private static final String ADD_TOOLTIP = "Add a new signal.";
	private static final String EDIT_TOOLTIP = "Edit the selected signal.";
	private static final String REMOVE_TOOLTIP = "Remove Selected Signals.";
	private static final String REMOVE_ALL_TOOLTIP = "Remove All Signals.";
	//
	private static final String IMPORT_TITLE = "Import Signals";
	private static final String EXPORT_TITLE = "Export Signals";
	private static final String MESSAGE_ADD = "You can create a new signal here.";
	private static final String MESSAGE_EDIT = "Edit the selected signal.";
	private static final String MESSAGE_REMOVE = "Do you want to delete the selected signals?";
	private static final String MESSAGE_REMOVE_ALL = "Do you want to delete all signals?";
	private static final String MESSAGE_EXPORT_SUCCESSFUL = "Signals have been exported successfully.";
	private static final String MESSAGE_EXPORT_FAILED = "Failed to export the signals.";
	//
	private AtomicReference<Button> buttonSearchControl = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<WavenumberSignalsUI> tableViewer = new AtomicReference<>();
	//
	private List<Button> buttons = new ArrayList<>();
	private List<Listener> listeners = new ArrayList<>();
	//
	private WavenumberSignals wavenumberSignals = new WavenumberSignals();

	public WavenumberSignalsEditor(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public WavenumberSignals getWavenumberSignals() {

		return wavenumberSignals;
	}

	public void load(String entries) {

		wavenumberSignals.load(entries);
		setViewerInput();
	}

	public String save() {

		return wavenumberSignals.save();
	}

	@Override
	public void addChangeListener(Listener listener) {

		/*
		 * Listen to changes in the table.
		 */
		Control control = tableViewer.get().getControl();
		control.addListener(SWT.Selection, listener);
		control.addListener(SWT.KeyUp, listener);
		control.addListener(SWT.MouseUp, listener);
		control.addListener(SWT.MouseDoubleClick, listener);
		/*
		 * Listen to selection of the buttons.
		 */
		for(Button button : buttons) {
			button.addListener(SWT.Selection, listener);
			button.addListener(SWT.KeyUp, listener);
			button.addListener(SWT.MouseUp, listener);
			button.addListener(SWT.MouseDoubleClick, listener);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {

		toolbarSearch.get().setEnabled(enabled);
		tableViewer.get().getControl().setEnabled(enabled);
		for(Button button : buttons) {
			button.setEnabled(enabled);
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		//
		createToolbarMain(this);
		createToolbarSearch(this);
		createTableSection(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarSearch, buttonSearchControl.get(), IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		setViewerInput();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createButtonToggleToolbar(composite);
		add(createButtonAdd(composite));
		add(createButtonEdit(composite));
		add(createButtonRemove(composite));
		add(createButtonRemoveAll(composite));
		add(createButtonImport(composite));
		add(createButtonExport(composite));
	}

	private void createButtonToggleToolbar(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		buttonSearchControl.set(button);
	}

	private void add(Button button) {

		buttons.add(button);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				tableViewer.get().setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createTableSection(Composite parent) {

		WavenumberSignalsUI wavenumberSignalsUI = new WavenumberSignalsUI(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		wavenumberSignalsUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		wavenumberSignalsUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				setViewerInput();
			}
		});
		//
		Shell shell = wavenumberSignalsUI.getTable().getShell();
		ITableSettings tableSettings = wavenumberSignalsUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		wavenumberSignalsUI.applySettings(tableSettings);
		//
		tableViewer.set(wavenumberSignalsUI);
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(ADD_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(e.display.getActiveShell(), WavenumberSignals.DESCRIPTION, MESSAGE_ADD, WavenumberSignals.DEMO_ENTRY, new WavenumberSignalsInputValidator(wavenumberSignals));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					WavenumberSignal wavenumberSignal = wavenumberSignals.extractSettingInstance(item);
					if(wavenumberSignal != null) {
						wavenumberSignals.add(wavenumberSignal);
						setViewerInput();
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
		button.setToolTipText(EDIT_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = tableViewer.get().getStructuredSelection().getFirstElement();
				if(object instanceof WavenumberSignal wavenumberSignal) {
					WavenumberSignals wavenumberSignalsModified = new WavenumberSignals();
					wavenumberSignalsModified.addAll(wavenumberSignals);
					wavenumberSignalsModified.remove(wavenumberSignal);
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), WavenumberSignals.DESCRIPTION, MESSAGE_EDIT, wavenumberSignals.extractSetting(wavenumberSignal), new WavenumberSignalsInputValidator(wavenumberSignalsModified));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						WavenumberSignal wavenumberSignalNew = wavenumberSignals.extractSettingInstance(item);
						wavenumberSignal.copyFrom(wavenumberSignalNew);
						setViewerInput();
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
		button.setToolTipText(REMOVE_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteItems(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(REMOVE_ALL_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), WavenumberSignals.DESCRIPTION, MESSAGE_REMOVE_ALL)) {
					wavenumberSignals.clear();
					setViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(IMPORT_TITLE);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText(IMPORT_TITLE);
				fileDialog.setFilterExtensions(new String[]{WavenumberSignals.FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{WavenumberSignals.FILTER_NAME});
				fileDialog.setFilterPath(PreferenceSupplier.getListPathImport());
				String path = fileDialog.open();
				if(path != null) {
					PreferenceSupplier.setListPathImport(fileDialog.getFilterPath());
					File file = new File(path);
					wavenumberSignals.importItems(file);
					setViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(EXPORT_TITLE);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText(EXPORT_TITLE);
				fileDialog.setFilterExtensions(new String[]{WavenumberSignals.FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{WavenumberSignals.FILTER_NAME});
				fileDialog.setFileName(WavenumberSignals.FILE_NAME);
				fileDialog.setFilterPath(PreferenceSupplier.getListPathExport());
				String path = fileDialog.open();
				if(path != null) {
					PreferenceSupplier.setListPathExport(fileDialog.getFilterPath());
					File file = new File(path);
					if(wavenumberSignals.exportItems(file)) {
						MessageDialog.openInformation(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_SUCCESSFUL);
					} else {
						MessageDialog.openWarning(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_FAILED);
					}
				}
			}
		});
		//
		return button;
	}

	private void setViewerInput() {

		tableViewer.get().setInput(wavenumberSignals);
		for(Listener listener : listeners) {
			listener.handleEvent(new Event());
		}
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete";
			}

			@Override
			public String getCategory() {

				return "Signals";
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteItems(shell);
			}
		});
	}

	private void addKeyEventProcessors(Shell shell, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					deleteItems(shell);
				}
			}
		});
	}

	private void deleteItems(Shell shell) {

		if(MessageDialog.openQuestion(shell, WavenumberSignals.DESCRIPTION, MESSAGE_REMOVE)) {
			for(Object object : tableViewer.get().getStructuredSelection().toArray()) {
				if(object instanceof WavenumberSignal wavenumberSignal) {
					wavenumberSignals.remove(wavenumberSignal);
				}
			}
			setViewerInput();
		}
	}
}