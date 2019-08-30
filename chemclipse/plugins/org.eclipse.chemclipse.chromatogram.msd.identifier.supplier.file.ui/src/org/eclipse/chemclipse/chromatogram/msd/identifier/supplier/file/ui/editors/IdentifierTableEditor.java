/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.model.IdentifierFile;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.swt.IdentifierFileListUI;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

public class IdentifierTableEditor extends FieldEditor {

	private IdentifierFileListUI identifierFileListUI;
	private Composite compositeButtons;
	private Button buttonAdd;
	private Button buttonRemove;
	private Button buttonClear;
	private SelectionListener selectionListener;
	//
	private String[] filterExtensions;
	private String[] filterNames;
	//
	private IdentifierFileListUtil identifierFileListUtil = new IdentifierFileListUtil();

	public IdentifierTableEditor(String name, String labelText, Composite parent) {
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

		if(identifierFileListUI != null) {
			identifierFileListUI.getTable().setFocus();
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

	protected IdentifierFileListUI getListUI() {

		return identifierFileListUI;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		Control control = getLabelControl();
		((GridData)control.getLayoutData()).horizontalSpan = numColumns;
		((GridData)identifierFileListUI.getTable().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doLoad() {

		if(identifierFileListUI != null) {
			String storedContent = getPreferenceStore().getString(getPreferenceName());
			if(storedContent == null || "".equals(storedContent)) {
				identifierFileListUI.setInput(null);
			} else {
				identifierFileListUI.setInput(getCalibrationFiles(storedContent));
			}
		}
	}

	@Override
	protected void doLoadDefault() {

		if(identifierFileListUI != null) {
			String storedContent = getPreferenceStore().getDefaultString(getPreferenceName());
			if(storedContent == null || "".equals(storedContent)) {
				identifierFileListUI.setInput(null);
			} else {
				identifierFileListUI.setInput(getCalibrationFiles(storedContent));
			}
		}
	}

	@Override
	protected void doStore() {

		TableItem[] tableItems = identifierFileListUI.getTable().getItems();
		String[] items = new String[tableItems.length];
		for(int i = 0; i < tableItems.length; i++) {
			Object object = tableItems[i].getData();
			String file = "";
			if(object instanceof IdentifierFile) {
				IdentifierFile calibrationFile = (IdentifierFile)object;
				file = calibrationFile.getFile().getAbsolutePath();
			}
			items[i] = file;
		}
		//
		String storedContent = identifierFileListUtil.createList(items);
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
		identifierFileListUI = getTableControl(parent);
		identifierFileListUI.getTable().setLayoutData(gridDataTable);
		//
		GridData gridDataButtons = new GridData();
		gridDataButtons.verticalAlignment = GridData.BEGINNING;
		compositeButtons = getButtonControl(parent);
		compositeButtons.setLayoutData(gridDataButtons);
	}

	private void createButtons(Composite composite) {

		buttonAdd = createButton(composite, "Add", "Add a new identifier file."); //$NON-NLS-1$
		buttonRemove = createButton(composite, "Remove", "Remove the selected identifier file."); //$NON-NLS-1$
		buttonClear = createButton(composite, "Clear", "Remove all identifier files."); //$NON-NLS-1$
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
						addPressed(buttonAdd.getDisplay().getActiveShell());
					} else if(widget == buttonRemove) {
						removePressed();
					} else if(widget == buttonClear) {
						clearPressed();
					} else if(widget == identifierFileListUI.getTable()) {
						selectionChanged();
					}
				}
			};
		}
		return selectionListener;
	}

	private void addPressed(Shell shell) {

		setPresentsDefaultValue(false);
		IdentifierFile calibrationFile = selectCalibrationFile(shell);
		if(calibrationFile != null) {
			identifierFileListUI.add(calibrationFile);
			selectionChanged();
		}
	}

	private void removePressed() {

		setPresentsDefaultValue(false);
		Table table = identifierFileListUI.getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			table.remove(index);
			table.select(index >= table.getItemCount() ? index - 1 : index);
			selectionChanged();
		}
	}

	private void clearPressed() {

		setPresentsDefaultValue(false);
		identifierFileListUI.getTable().removeAll();
	}

	private void selectionChanged() {

		int index = identifierFileListUI.getTable().getSelectionIndex();
		buttonRemove.setEnabled(index >= 0);
		buttonClear.setEnabled(index >= 0);
	}

	private IdentifierFile selectCalibrationFile(Shell shell) {

		IdentifierFile identifierFile = null;
		//
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setText("Select New Identifier File");
		fileDialog.setFilterPath(PreferenceSupplier.getFilterPathIdentifierFiles());
		//
		if(filterExtensions != null && filterNames != null) {
			fileDialog.setFilterExtensions(filterExtensions);
			fileDialog.setFilterNames(filterNames);
		}
		//
		String filterPath = fileDialog.open();
		if(filterPath != null) {
			File file = new File(filterPath);
			PreferenceSupplier.setFilterPathIdentifierFiles(file.getParentFile().getAbsolutePath());
			identifierFile = new IdentifierFile(file);
			/*
			 * Check that file and column not exists already.
			 */
			TableItem[] tableItems = identifierFileListUI.getTable().getItems();
			exitloop:
			for(TableItem tableItem : tableItems) {
				Object object = tableItem.getData();
				if(object instanceof IdentifierFile) {
					if(((IdentifierFile)object).equals(identifierFile)) {
						identifierFile = null;
						break exitloop;
					}
				}
			}
		}
		//
		return identifierFile;
	}

	private List<IdentifierFile> getCalibrationFiles(String storedContent) {

		String[] files = identifierFileListUtil.parseString(storedContent);
		List<IdentifierFile> identifierFiles = new ArrayList<>();
		for(String file : files) {
			identifierFiles.add(new IdentifierFile(new File(file)));
		}
		return identifierFiles;
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

	private IdentifierFileListUI getTableControl(Composite parent) {

		if(identifierFileListUI == null) {
			identifierFileListUI = new IdentifierFileListUI(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
			identifierFileListUI.getTable().addSelectionListener(getSelectionListener());
		} else {
			checkParent(identifierFileListUI.getTable(), parent);
		}
		return identifierFileListUI;
	}
}
