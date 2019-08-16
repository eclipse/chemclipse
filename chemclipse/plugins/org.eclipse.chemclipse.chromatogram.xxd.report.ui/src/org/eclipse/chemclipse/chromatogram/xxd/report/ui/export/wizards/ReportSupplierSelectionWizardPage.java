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
package org.eclipse.chemclipse.chromatogram.xxd.report.ui.export.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.chemclipse.chromatogram.xxd.report.model.IChromatogramReportSupplierEntry;
import org.eclipse.chemclipse.chromatogram.xxd.report.support.ReportSupplierTypeSupport;
import org.eclipse.chemclipse.chromatogram.xxd.report.ui.internal.wizards.ChromatogramReportEntriesWizard;
import org.eclipse.chemclipse.chromatogram.xxd.report.ui.internal.wizards.ProcessWizardDialog;

public class ReportSupplierSelectionWizardPage extends WizardPage {

	private Table reportSupplierTable;
	boolean appendReports = false;

	public ReportSupplierSelectionWizardPage(String pageName, String title, ImageDescriptor titleImage) {
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
		createReportSelection(container, layoutData);
		/*
		 * Set the control, otherwise an error will be thrown.
		 */
		setControl(container);
	}

	public Table getTable() {

		return reportSupplierTable;
	}

	private void createReportSelection(Composite parent, GridData layoutData) {

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
	}

	/**
	 * Creates the table.
	 * 
	 * @param parent
	 */
	private void createTable(Composite parent) {

		reportSupplierTable = new Table(parent, SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 300;
		gridData.widthHint = 100;
		gridData.verticalSpan = 3;
		reportSupplierTable.setLayoutData(gridData);
		reportSupplierTable.setHeaderVisible(true);
		reportSupplierTable.setLinesVisible(true);
		/*
		 * Header
		 */
		String[] titles = {"Report Name", "Report Folder/Report File", "Report Id"};
		for(int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(reportSupplierTable, SWT.NONE);
			column.setText(titles[i]);
		}
		/*
		 * Pack to make the entries visible.
		 */
		for(int i = 0; i < titles.length; i++) {
			reportSupplierTable.getColumn(i).pack();
		}
		/*
		 * Set a message
		 */
		checkReportSupplier();
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
	 * Create the add button.
	 * 
	 * @param parent
	 * @param editorPart
	 */
	private void createAddButton(Composite parent) {

		Button buttonAdd = new Button(parent, SWT.PUSH);
		buttonAdd.setText("Add");
		buttonAdd.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				Shell shell = Display.getCurrent().getActiveShell();
				ChromatogramReportEntriesWizard reportEntriesWizard = new ChromatogramReportEntriesWizard();
				ProcessWizardDialog wizardDialog = new ProcessWizardDialog(shell, reportEntriesWizard);
				wizardDialog.create();
				int returnCode = wizardDialog.open();
				/*
				 * If OK
				 */
				if(returnCode == WizardDialog.OK) {
					IChromatogramReportSupplierEntry reportEntry = reportEntriesWizard.getChromatogramReportEntry();
					if(reportEntry != null) {
						ReportSupplierTypeSupport reportSupplierTypeSupport = new ReportSupplierTypeSupport();
						TableItem item = new TableItem(reportSupplierTable, SWT.NONE);
						item.setText(0, reportSupplierTypeSupport.getReportName(reportEntry));
						item.setText(1, reportEntry.getReportFolderOrFile());
						item.setText(2, reportEntry.getReportSupplierId());
					} else {
						MessageBox messageBox = new MessageBox(shell);
						messageBox.setText("Error Add Report Entry");
						messageBox.setMessage("Please select a valid chromatogram report supplier and output folder.");
						messageBox.open();
					}
				}
				checkReportSupplier();
			}
		});
	}

	/**
	 * Create the remove button.
	 * 
	 * @param parent
	 * @param editorPart
	 */
	private void createRemoveButton(Composite parent) {

		Button buttonRemove = new Button(parent, SWT.PUSH);
		buttonRemove.setText("Remove");
		buttonRemove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				removeEntries(reportSupplierTable.getSelectionIndices());
				checkReportSupplier();
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
				reportSupplierTable.removeAll();
				checkReportSupplier();
			}
		});
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
		reportSupplierTable.remove(indices);
	}

	private void checkReportSupplier() {

		/*
		 * Report Supplier
		 */
		int count = reportSupplierTable.getItemCount();
		if(count > 0) {
			setErrorMessage(null);
			setPageComplete(true);
		} else {
			setErrorMessage("Please select at least one report provider.");
			setPageComplete(false);
		}
	}
}
