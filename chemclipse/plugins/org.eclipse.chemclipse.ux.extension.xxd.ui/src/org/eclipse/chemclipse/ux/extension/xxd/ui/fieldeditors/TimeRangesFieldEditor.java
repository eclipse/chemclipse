/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TimeRangeInputValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.TimeRangesListUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

public class TimeRangesFieldEditor extends FieldEditor {

	private static final int NUMBER_COLUMNS = 2;
	private static final String FILTER_EXTENSION = "*.txt";
	private static final String FILTER_NAME = "Time Ranges (*.txt)";
	private static final String FILE_NAME = "TimeRanges.txt";
	//
	private Composite composite;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private TimeRanges timeRanges = new TimeRanges();
	private TimeRangesListUI timeRangesListUI;

	public TimeRangesFieldEditor(String name, String labelText, Composite parent) {
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

				timeRangesListUI.setSearchText(searchText, caseSensitive);
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
		timeRangesListUI = new TimeRangesListUI(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
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
		setButtonLayoutData(createButtonEdit(composite));
		setButtonLayoutData(createButtonRemove(composite));
		setButtonLayoutData(createButtonRemoveAll(composite));
		setButtonLayoutData(createButtonImport(composite));
		setButtonLayoutData(createButtonExport(composite));
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add");
		button.setToolTipText("Add a time range.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(e.display.getActiveShell(), "Time Range", "Create a new time range.", "C10 | 10.2 | 10.4 | 10.6", new TimeRangeInputValidator(timeRanges.keySet()));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					TimeRange timeRange = timeRanges.extractTimeRange(item);
					if(timeRange != null) {
						timeRanges.add(timeRange);
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
		button.setText("Edit");
		button.setToolTipText("Edit the selected time range.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = (IStructuredSelection)timeRangesListUI.getSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof TimeRange) {
					Set<String> keySetEdit = new HashSet<>();
					keySetEdit.addAll(timeRanges.keySet());
					TimeRange timeRange = (TimeRange)object;
					keySetEdit.remove(timeRange.getIdentifier());
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), "Time Range", "Edit the selected time range.", timeRanges.extractTimeRange(timeRange), new TimeRangeInputValidator(keySetEdit));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						TimeRange timeRangeNew = timeRanges.extractTimeRange(item);
						if(timeRangeNew != null) {
							timeRanges.remove(timeRange);
							timeRanges.add(timeRangeNew);
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
		button.setToolTipText("Remove the selected time range(s).");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), "Time Range(s)", "Do you want to delete the selected time range(s)?")) {
					IStructuredSelection structuredSelection = (IStructuredSelection)timeRangesListUI.getSelection();
					for(Object object : structuredSelection.toArray()) {
						if(object instanceof TimeRange) {
							timeRanges.remove(((TimeRange)object).getIdentifier());
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
		button.setToolTipText("Remove all time ranges(s).");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), "Time Range(s)", "Do you want to delete all time range(s)?")) {
					timeRanges.clear();
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Import");
		button.setToolTipText("Import a time range list.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText("Time Range List");
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER));
				String pathname = fileDialog.open();
				if(pathname != null) {
					File file = new File(pathname);
					String path = file.getParentFile().getAbsolutePath();
					preferenceStore.putValue(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER, path);
					timeRanges.importItems(file);
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Export");
		button.setToolTipText("Export the time range list.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText("Time Range List");
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFileName(FILE_NAME);
				fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER));
				String pathname = fileDialog.open();
				if(pathname != null) {
					File file = new File(pathname);
					String path = file.getParentFile().getAbsolutePath();
					preferenceStore.putValue(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER, path);
					if(timeRanges.exportItems(file)) {
						MessageDialog.openInformation(button.getShell(), "Time Range List", "The time range list has been exported successfully.");
					} else {
						MessageDialog.openWarning(button.getShell(), "Time Range List", "Something went wrong to export the time range list.");
					}
				}
			}
		});
		//
		return button;
	}

	private void setTableViewerInput() {

		timeRangesListUI.setInput(timeRanges.values());
	}

	@Override
	protected void doLoad() {

		String entries = getPreferenceStore().getString(getPreferenceName());
		timeRanges.load(entries);
		setTableViewerInput();
	}

	@Override
	protected void doLoadDefault() {

		String entries = getPreferenceStore().getDefaultString(getPreferenceName());
		timeRanges.loadDefault(entries);
		setTableViewerInput();
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), timeRanges.save());
	}

	@Override
	public int getNumberOfControls() {

		return 1;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		if(numColumns >= 2) {
			GridData gridData = (GridData)composite.getLayoutData();
			gridData.horizontalSpan = numColumns - 1;
			gridData.grabExcessHorizontalSpace = true;
		}
	}
}
