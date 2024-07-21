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
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TimeRangeInputValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.IChangeListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

public class TimeRangesEditorUI extends Composite implements IChangeListener, IExtendedPartUI {

	public static final String ADD = "Add";
	public static final String ADD_TOOLTIP = "Add a new time range.";
	public static final String EDIT = "Edit";
	public static final String EDIT_TOOLTIP = "Edit the selected time range.";
	public static final String REMOVE = "Remove";
	public static final String REMOVE_TOOLTIP = "Remove selected time ranges.";
	public static final String REMOVE_ALL = "Remove all";
	public static final String REMOVE_ALL_TOOLTIP = "Remove all time ranges.";
	public static final String IMPORT = "Import";
	public static final String EXPORT = "Export";
	//
	public static final String IMPORT_TITLE = "Import";
	public static final String EXPORT_TITLE = "Export";
	public static final String DIALOG_TITLE = "Time Ranges";
	public static final String MESSAGE_ADD = "You can create a new time range here.";
	public static final String MESSAGE_EDIT = "Edit the selected time ranges.";
	public static final String MESSAGE_REMOVE = "Do you want to delete the selected time ranges?";
	public static final String MESSAGE_REMOVE_ALL = "Do you want to delete all time ranges?";
	public static final String MESSAGE_EXPORT_SUCCESSFUL = "Time ranges have been exported successfully.";
	public static final String MESSAGE_EXPORT_FAILED = "Failed to export the time ranges.";
	//
	public static final String EXAMPLE_ENTRY = "Styrene | 1.2 | 1.4 | 1.6";
	//
	private AtomicReference<TimeRangesListUI> listControl = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarSearchControl = new AtomicReference<>();
	private AtomicReference<Button> buttonAddControl = new AtomicReference<>();
	private AtomicReference<Button> buttonRemoveControl = new AtomicReference<>();
	private AtomicReference<Button> buttonRemoveAllControl = new AtomicReference<>();
	private AtomicReference<Button> buttonImportControl = new AtomicReference<>();
	private AtomicReference<Button> buttonExportControl = new AtomicReference<>();
	//
	private TimeRanges timeRanges = new TimeRanges();
	//
	private Listener listener;

	public TimeRangesEditorUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void addChangeListener(Listener listener) {

		this.listener = listener;
		//
		Table table = listControl.get().getTable();
		table.addListener(SWT.Selection, listener);
		table.addListener(SWT.KeyUp, listener);
		table.addListener(SWT.MouseUp, listener);
		table.addListener(SWT.MouseDoubleClick, listener);
		//
		buttonAddControl.get().addListener(SWT.KeyUp, listener);
		buttonRemoveControl.get().addListener(SWT.KeyUp, listener);
		buttonRemoveAllControl.get().addListener(SWT.KeyUp, listener);
		buttonImportControl.get().addListener(SWT.KeyUp, listener);
		buttonExportControl.get().addListener(SWT.KeyUp, listener);
	}

	@Override
	public void setEnabled(boolean enabled) {

		toolbarSearch.get().setEnabled(enabled);
		buttonAddControl.get().setEnabled(enabled);
		buttonRemoveControl.get().setEnabled(enabled);
		buttonRemoveAllControl.get().setEnabled(enabled);
		buttonImportControl.get().setEnabled(enabled);
		buttonExportControl.get().setEnabled(enabled);
		listControl.get().getControl().setEnabled(enabled);
	}

	public void clear() {

		this.timeRanges = new TimeRanges();
		setInput();
	}

	public TimeRanges getTimeRanges() {

		return timeRanges;
	}

	public void setInput(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		setInput();
	}

	public void load(String entries) {

		timeRanges.load(entries);
		setInput();
	}

	public String save() {

		return timeRanges.save();
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

		enableToolbar(toolbarSearch, buttonToolbarSearchControl.get(), IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		setInput();
	}

	private void createTableSection(Composite parent) {

		TimeRangesListUI timeRangesListUI = new TimeRangesListUI(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		Table table = timeRangesListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		timeRangesListUI.setEditEnabled(true);
		timeRangesListUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				setInput();
			}
		});
		//
		ITableSettings tableSettings = timeRangesListUI.getTableSettings();
		timeRangesListUI.applySettings(tableSettings);
		//
		listControl.set(timeRangesListUI);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		createButtonToggleSearch(composite);
		createButtonAdd(composite);
		createButtonRemove(composite);
		createButtonRemoveAll(composite);
		createButtonImport(composite);
		createButtonExport(composite);
	}

	private void createButtonToggleSearch(Composite parent) {

		buttonToolbarSearchControl.set(createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH));
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				listControl.get().setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(ADD_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Set<String> identifier = timeRanges.values().stream().map(s -> s.getIdentifier()).collect(Collectors.toSet());
				InputDialog dialog = new InputDialog(button.getShell(), DIALOG_TITLE, MESSAGE_ADD, EXAMPLE_ENTRY, new TimeRangeInputValidator(identifier, null));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					TimeRange setting = timeRanges.extractTimeRange(item);
					if(setting != null) {
						timeRanges.add(setting);
						setInput();
					}
				}
			}
		});
		//
		buttonAddControl.set(button);
	}

	private void createButtonRemove(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(REMOVE_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(listControl.get().getStructuredSelection().getFirstElement() instanceof TimeRange timeRange) {
					timeRanges.remove(timeRange.getIdentifier());
					setInput();
				}
			}
		});
		//
		buttonRemoveControl.set(button);
	}

	private void createButtonRemoveAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(REMOVE_ALL_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), DIALOG_TITLE, MESSAGE_REMOVE_ALL)) {
					timeRanges.clear();
					setInput();
				}
			}
		});
		//
		buttonRemoveAllControl.set(button);
	}

	private void createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(IMPORT_TITLE);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText(IMPORT_TITLE);
				fileDialog.setFilterExtensions(new String[]{TimeRanges.FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{TimeRanges.FILTER_NAME});
				fileDialog.setFilterPath(PreferenceSupplier.getListPathImport());
				String path = fileDialog.open();
				if(path != null) {
					PreferenceSupplier.setListPathImport(fileDialog.getFilterPath());
					File file = new File(path);
					timeRanges.importItems(file);
					setInput();
				}
			}
		});
		//
		buttonImportControl.set(button);
	}

	private void createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(EXPORT_TITLE);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText(EXPORT_TITLE);
				fileDialog.setFilterExtensions(new String[]{TimeRanges.FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{TimeRanges.FILTER_NAME});
				fileDialog.setFileName(TimeRanges.FILE_NAME);
				fileDialog.setFilterPath(PreferenceSupplier.getListPathExport());
				String path = fileDialog.open();
				if(path != null) {
					PreferenceSupplier.setListPathExport(fileDialog.getFilterPath());
					File file = new File(path);
					if(timeRanges.exportItems(file)) {
						MessageDialog.openInformation(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_SUCCESSFUL);
					} else {
						MessageDialog.openWarning(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_FAILED);
					}
				}
			}
		});
		//
		buttonExportControl.set(button);
	}

	private void setInput() {

		List<TimeRange> timeRangesSorted = new ArrayList<>(timeRanges.values());
		Collections.sort(timeRangesSorted, (s1, s2) -> Integer.compare(s1.getStart(), s2.getStart()));
		listControl.get().setInput(timeRangesSorted);
		//
		if(listener != null) {
			listener.handleEvent(new Event());
		}
	}
}