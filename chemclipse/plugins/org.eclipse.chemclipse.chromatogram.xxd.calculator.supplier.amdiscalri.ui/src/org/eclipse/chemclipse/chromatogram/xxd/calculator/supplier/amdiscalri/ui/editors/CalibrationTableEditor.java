/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.CalibrationFile;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.CalibrationFileListUI;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

public class CalibrationTableEditor extends FieldEditor {

	private CalibrationFileListUI calibrationFileListUI;
	private Composite compositeButtons;
	private Button buttonAdd;
	private Button buttonRemove;
	private Button buttonClear;
	private SelectionListener selectionListener;
	//
	private String[] filterExtensions;
	private String[] filterNames;
	//
	private CalibrationFileListUtil calibrationFileListUtil = new CalibrationFileListUtil();

	public CalibrationTableEditor(String name, String labelText, Composite parent) {

		init(name, labelText);
		createControl(parent);
	}

	@Override
	public void setEnabled(boolean enabled, Composite parent) {

		super.setEnabled(enabled, parent);
		getTableControl(parent).getTable().setEnabled(enabled);
		buttonAdd.setEnabled(enabled);
		buttonRemove.setEnabled(enabled);
		buttonClear.setEnabled(enabled);
	}

	@Override
	public void setFocus() {

		if(calibrationFileListUI != null) {
			calibrationFileListUI.getTable().setFocus();
		}
	}

	@Override
	public int getNumberOfControls() {

		return 2;
	}

	public void setFilterExtensionsAndNames(String[] filterExtensions, String[] filterNames) {

		this.filterExtensions = filterExtensions;
		this.filterNames = filterNames;
	}

	protected CalibrationFileListUI getListUI() {

		return calibrationFileListUI;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		Control control = getLabelControl();
		((GridData)control.getLayoutData()).horizontalSpan = numColumns;
		((GridData)calibrationFileListUI.getTable().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doLoad() {

		if(calibrationFileListUI != null) {
			String storedContent = getPreferenceStore().getString(getPreferenceName());
			if(storedContent == null || "".equals(storedContent)) {
				calibrationFileListUI.setInput(null);
			} else {
				calibrationFileListUI.setInput(getCalibrationFiles(storedContent));
			}
		}
	}

	@Override
	protected void doLoadDefault() {

		if(calibrationFileListUI != null) {
			String storedContent = getPreferenceStore().getDefaultString(getPreferenceName());
			if(storedContent == null || "".equals(storedContent)) {
				calibrationFileListUI.setInput(null);
			} else {
				calibrationFileListUI.setInput(getCalibrationFiles(storedContent));
			}
		}
	}

	@Override
	protected void doStore() {

		TableItem[] tableItems = calibrationFileListUI.getTable().getItems();
		String[] items = new String[tableItems.length];
		for(int i = 0; i < tableItems.length; i++) {
			Object object = tableItems[i].getData();
			String file = "";
			if(object instanceof CalibrationFile calibrationFile) {
				file = calibrationFile.getFile().getAbsolutePath();
			}
			items[i] = file;
		}
		//
		String storedContent = calibrationFileListUtil.createList(items);
		if(storedContent != null) {
			getPreferenceStore().setValue(getPreferenceName(), storedContent);
		}
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		Control control = getLabelControl(parent);
		GridData gridDataControl = new GridData();
		gridDataControl.horizontalSpan = numColumns;
		control.setLayoutData(gridDataControl);
		//
		GridData gridDataTable = new GridData(GridData.FILL_BOTH);
		gridDataTable.verticalAlignment = GridData.FILL;
		gridDataTable.horizontalAlignment = GridData.FILL;
		gridDataTable.horizontalSpan = numColumns - 1;
		gridDataTable.grabExcessHorizontalSpace = true;
		calibrationFileListUI = getTableControl(parent);
		calibrationFileListUI.getTable().setLayoutData(gridDataTable);
		//
		GridData gridDataButtons = new GridData();
		gridDataButtons.verticalAlignment = GridData.BEGINNING;
		compositeButtons = getButtonControl(parent);
		compositeButtons.setLayoutData(gridDataButtons);
	}

	private void createButtons(Composite composite) {

		buttonAdd = createButton(composite, "Add", "Add a new calibration file."); //$NON-NLS-1$
		buttonRemove = createButton(composite, "Remove", "Remove the selected calibration file."); //$NON-NLS-1$
		buttonClear = createButton(composite, "Clear", "Remove all calibration files."); //$NON-NLS-1$
	}

	private Button createButton(Composite parent, String text, String tooltip) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setToolTipText(tooltip);
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(getSelectionListener());
		return button;
	}

	private SelectionListener getSelectionListener() {

		if(selectionListener == null) {
			selectionListener = new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {

					Widget widget = event.widget;
					if(widget == buttonAdd) {
						addPressed();
					} else if(widget == buttonRemove) {
						removePressed();
					} else if(widget == buttonClear) {
						clearPressed();
					} else if(widget == calibrationFileListUI.getTable()) {
						selectionChanged();
					}
				}
			};
		}
		return selectionListener;
	}

	private void addPressed() {

		setPresentsDefaultValue(false);
		CalibrationFile calibrationFile = selectCalibrationFile();
		if(calibrationFile != null) {
			calibrationFileListUI.add(calibrationFile);
			selectionChanged();
		}
	}

	private void removePressed() {

		setPresentsDefaultValue(false);
		Table table = calibrationFileListUI.getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			table.remove(index);
			table.select(index >= table.getItemCount() ? index - 1 : index);
			selectionChanged();
		}
	}

	private void clearPressed() {

		setPresentsDefaultValue(false);
		calibrationFileListUI.getTable().removeAll();
	}

	private void selectionChanged() {

		int index = calibrationFileListUI.getTable().getSelectionIndex();
		buttonRemove.setEnabled(index >= 0);
		buttonClear.setEnabled(index >= 0);
	}

	private CalibrationFile selectCalibrationFile() {

		CalibrationFile calibrationFile = null;
		//
		FileDialog fileDialog = new FileDialog(DisplayUtils.getShell(buttonAdd), SWT.OPEN);
		fileDialog.setText("Select New Calibration File");
		fileDialog.setFilterPath(PreferenceSupplier.getFilterPathIndexFiles());
		//
		if(filterExtensions != null && filterNames != null) {
			fileDialog.setFilterExtensions(filterExtensions);
			fileDialog.setFilterNames(filterNames);
		}
		//
		String filterPath = fileDialog.open();
		if(filterPath != null) {
			File file = new File(filterPath);
			PreferenceSupplier.setFilterPathIndexFiles(file.getParentFile().getAbsolutePath());
			calibrationFile = new CalibrationFile(file);
			/*
			 * Check that file and column not exists already.
			 */
			TableItem[] tableItems = calibrationFileListUI.getTable().getItems();
			exitloop:
			for(TableItem tableItem : tableItems) {
				Object object = tableItem.getData();
				if(object instanceof CalibrationFile newCalibrationFile && newCalibrationFile.equals(calibrationFile)) {
					calibrationFile = null;
					break exitloop;
				}
			}
		}
		//
		return calibrationFile;
	}

	private List<CalibrationFile> getCalibrationFiles(String storedContent) {

		String[] files = calibrationFileListUtil.parseString(storedContent);
		List<CalibrationFile> calibrationFiles = new ArrayList<>();
		for(String file : files) {
			calibrationFiles.add(new CalibrationFile(new File(file)));
		}
		return calibrationFiles;
	}

	private Composite getButtonControl(Composite parent) {

		if(compositeButtons == null) {
			compositeButtons = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			compositeButtons.setLayout(layout);
			//
			createButtons(compositeButtons);
			compositeButtons.addDisposeListener(event -> {
				buttonAdd = null;
				buttonRemove = null;
				buttonClear = null;
				compositeButtons = null;
			});
		} else {
			checkParent(compositeButtons, parent);
		}
		//
		selectionChanged();
		return compositeButtons;
	}

	private CalibrationFileListUI getTableControl(Composite parent) {

		if(calibrationFileListUI == null) {
			calibrationFileListUI = new CalibrationFileListUI(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
			calibrationFileListUI.getTable().addSelectionListener(getSelectionListener());
		} else {
			checkParent(calibrationFileListUI.getTable(), parent);
		}
		return calibrationFileListUI;
	}
}
