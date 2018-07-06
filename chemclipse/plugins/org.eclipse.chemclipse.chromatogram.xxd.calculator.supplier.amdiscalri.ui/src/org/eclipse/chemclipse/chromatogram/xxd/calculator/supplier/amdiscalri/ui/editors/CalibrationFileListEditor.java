/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
import java.util.Arrays;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileReader;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;

public class CalibrationFileListEditor extends ListEditor {

	private CalibrationFileReader calibrationFileReader = new CalibrationFileReader();
	private String[] filterExtensions;
	private String[] filterNames;

	public CalibrationFileListEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		initialize(parent);
	}

	public void setFilterExtensionsAndNames(String[] filterExtensions, String[] filterNames) {

		this.filterExtensions = filterExtensions;
		this.filterNames = filterNames;
	}

	@Override
	protected String createList(String[] items) {

		CalibrationFileListUtil fileListUtil = new CalibrationFileListUtil();
		return fileListUtil.createList(items);
	}

	@Override
	protected String getNewInputObject() {

		List list = getList();
		//
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		dialog.setText("Select File");
		if(filterExtensions != null && filterNames != null) {
			dialog.setFilterExtensions(filterExtensions);
			dialog.setFilterNames(filterNames);
		}
		//
		String file = dialog.open();
		if(file != null) {
			return addFile(file, list);
		}
		return null;
	}

	private void initialize(Composite parent) {

		Composite composite = getButtonBoxControl(parent);
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Clear");
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List list = getList();
				if(list != null) {
					MessageBox messageBox = new MessageBox(DisplayUtils.getShell(), SWT.YES | SWT.NO | SWT.CANCEL);
					messageBox.setText("File(s)");
					messageBox.setMessage("Remove all files from the list?");
					int decision = messageBox.open();
					if(decision == SWT.YES) {
						list.removeAll();
					}
				}
			}
		});
	}

	private String addFile(String file, List list) {

		String[] items = list.getItems();
		String extendedDescription = getExtendedDescription(file);
		if(!itemExistsInList(extendedDescription, items)) {
			getList().add(file);
		}
		return null;
	}

	private boolean itemExistsInList(String file, String[] list) {

		for(String item : list) {
			if(item.equals(file)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String[] parseString(String stringList) {

		CalibrationFileListUtil fileListUtil = new CalibrationFileListUtil();
		String[] files = fileListUtil.parseString(stringList);
		for(int i = 0; i < files.length; i++) {
			files[i] = getExtendedDescription(files[i]);
		}
		Arrays.sort(files);
		return files;
	}

	private String getExtendedDescription(String file) {

		File calibrationFile = new File(file);
		ISeparationColumnIndices separationColumnIndices = calibrationFileReader.parse(calibrationFile);
		String separationColumn = separationColumnIndices.getSeparationColumn().getName();
		if("".equals(separationColumn)) {
			separationColumn = "N.A.";
		}
		// return separationColumn + "\t\t(" + calibrationFile.getName() + ") -> " + file;
		return file;
	}
}
