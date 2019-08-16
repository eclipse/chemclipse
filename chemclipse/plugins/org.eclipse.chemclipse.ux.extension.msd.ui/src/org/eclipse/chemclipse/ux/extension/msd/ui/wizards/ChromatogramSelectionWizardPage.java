/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ChromatogramSelectionWizardPage extends WizardPage {

	private Table inputFilesTable;
	private Label countFiles;
	private static final String FILES = "Input Files: ";

	public ChromatogramSelectionWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		container.setLayout(layout);
		//
		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		layoutData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		//
		createChromatogramReportExportFileSelection(container, layoutData);
		/*
		 * Set the control, otherwise an error will be thrown.
		 */
		setControl(container);
	}

	public Table getTable() {

		return inputFilesTable;
	}

	private void createChromatogramReportExportFileSelection(Composite parent, GridData layoutData) {

		/*
		 * Set the layout for the client.
		 */
		Composite client = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		client.setLayoutData(layoutData);
		/*
		 * Creates the table and the action buttons.
		 */
		createTable(client);
		createButtons(client);
		createLabels(client);
	}

	/**
	 * Creates the table.
	 * 
	 * @param parent
	 */
	private void createTable(Composite parent) {

		GridData gridData;
		inputFilesTable = new Table(parent, SWT.MULTI);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 300;
		gridData.widthHint = 100;
		gridData.verticalSpan = 3;
		inputFilesTable.setLayoutData(gridData);
		inputFilesTable.setHeaderVisible(true);
		inputFilesTable.setLinesVisible(true);
		/*
		 * Header
		 */
		String[] titles = {"Filename", "Path"};
		for(int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(inputFilesTable, SWT.NONE);
			column.setText(titles[i]);
		}
		/*
		 * Pack to make the entries visible.
		 */
		for(int i = 0; i < titles.length; i++) {
			inputFilesTable.getColumn(i).pack();
		}
	}

	/**
	 * Create the action buttons.
	 * 
	 * @param parent
	 */
	private void createButtons(Composite parent) {

		createAddButton(parent);
		createRemoveButton(parent);
		createRemoveAllButton(parent);
	}

	/**
	 * Creates the add button.
	 * 
	 * @param parent
	 */
	private void createAddButton(Composite parent) {

		Button add;
		add = new Button(parent, SWT.PUSH);
		add.setText("Add");
		add.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				List<String> ocbFiles = getChromatogramFileSelection();
				/*
				 * Data
				 */
				TableItem[] items = inputFilesTable.getItems();
				for(String ocbFile : ocbFiles) {
					/*
					 * Don't add files twice.
					 */
					File file = new File(ocbFile);
					if(addItem(items, file)) {
						TableItem item = new TableItem(inputFilesTable, SWT.NONE);
						item.setText(0, file.getName());
						item.setText(1, file.getAbsolutePath());
					}
				}
				//
				updateCountFileLabel();
			}
		});
	}

	/**
	 * Checks whether the file name still exists in the list or not.
	 * Cause files will be reported to a sole directory, duplicate file names are bad.
	 * 
	 * @param items
	 * @param file
	 * @return boolean
	 */
	private boolean addItem(TableItem[] items, File file) {

		for(TableItem item : items) {
			if(item.getText().equals(file.getName())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Create the remove button.
	 * 
	 * @param parent
	 */
	private void createRemoveButton(Composite parent) {

		Button remove = new Button(parent, SWT.PUSH);
		remove.setText("Remove");
		remove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				removeEntries(inputFilesTable.getSelectionIndices());
			}
		});
	}

	/**
	 * Create the remove all button.
	 * 
	 * @param parent
	 */
	private void createRemoveAllButton(Composite parent) {

		Button remove = new Button(parent, SWT.PUSH);
		remove.setText("Remove All");
		remove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				inputFilesTable.removeAll();
				updateCountFileLabel();
			}
		});
	}

	/**
	 * Creates the file count labels.
	 * 
	 * @param parent
	 */
	private void createLabels(Composite parent) {

		countFiles = new Label(parent, SWT.NONE);
		countFiles.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		updateCountFileLabel();
	}

	/**
	 * Remove the given entries.
	 * The table need not to be reloaded.
	 * 
	 * @param indices
	 */
	private void removeEntries(int[] indices) {

		if(indices == null || indices.length == 0) {
			return;
		}
		/*
		 * Remove the entries from the table.
		 */
		inputFilesTable.remove(indices);
		updateCountFileLabel();
	}

	private void updateCountFileLabel() {

		int count = inputFilesTable.getItemCount();
		countFiles.setText(FILES + count);
		if(count > 0) {
			setErrorMessage(null);
			setPageComplete(true);
		} else {
			setErrorMessage("Please select chromatograms that shall be used for reporting.");
			setPageComplete(false);
		}
	}

	private List<String> getChromatogramFileSelection() {

		List<String> selectedFiles = new ArrayList<String>();
		FileDialog fileDialog = new FileDialog(DisplayUtils.getShell(), SWT.OPEN | SWT.MULTI);
		fileDialog.setText("Please select the chromatograms used for reporting.");
		fileDialog.setFilterExtensions(new String[]{"*.ocb"});
		fileDialog.setFilterNames(new String[]{"OpenChrom Chromatogram (*.ocb)"});
		String value = fileDialog.open();
		if(value != null) {
			String directory = fileDialog.getFilterPath();
			if(!directory.endsWith(File.separator)) {
				directory += File.separator;
			}
			String[] fileNames = fileDialog.getFileNames();
			for(String fileName : fileNames) {
				selectedFiles.add(directory + fileName);
			}
		}
		return selectedFiles;
	}
}
