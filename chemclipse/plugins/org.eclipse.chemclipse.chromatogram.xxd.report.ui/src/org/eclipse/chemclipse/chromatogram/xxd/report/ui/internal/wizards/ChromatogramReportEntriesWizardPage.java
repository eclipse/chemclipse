/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupport;
import org.eclipse.chemclipse.chromatogram.xxd.report.exceptions.NoReportSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.report.model.ChromatogramReportSupplierEntry;
import org.eclipse.chemclipse.chromatogram.xxd.report.model.IChromatogramReportSupplierEntry;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.swt.EnhancedCombo;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ChromatogramReportEntriesWizardPage extends WizardPage {

	private static final Logger logger = Logger.getLogger(ChromatogramReportEntriesWizardPage.class);
	private Combo chromatogramReportSupplierComboBox;
	private Text chromatogramReportFolderOrFileTextBox;
	private IChromatogramReportSupport reportSupport;
	private boolean appendReports = false;
	private Button buttonSelectFileOrFolder;
	private Label label;

	/**
	 * @param pageName
	 */
	protected ChromatogramReportEntriesWizardPage(String pageName) {

		super(pageName);
		setTitle("Chromatogram Report Supplier");
		setDescription("This wizard lets you select several chromatogram report supplier.");
	}

	/**
	 * Returns the report supplier id.
	 * 
	 * @return String
	 * @throws NoConverterAvailableException
	 */
	public IChromatogramReportSupplierEntry getChromatogramReportEntry() throws NoReportSupplierAvailableException {

		/*
		 * Report Supplier
		 */
		IChromatogramReportSupplier reportSupplier = getReportSupplier();
		String reportSupplierId = reportSupplier.getId();
		/*
		 * Output folder or append file
		 */
		String reportFolderOrFile = chromatogramReportFolderOrFileTextBox.getText();
		if(reportFolderOrFile == null || reportFolderOrFile.equals("")) {
			throw new NoReportSupplierAvailableException("The report folder/file must not be null.");
		}
		/*
		 * Report entry
		 */
		return new ChromatogramReportSupplierEntry(reportFolderOrFile, reportSupplierId);
	}

	private IChromatogramReportSupplier getReportSupplier() throws NoReportSupplierAvailableException {

		int index = chromatogramReportSupplierComboBox.getSelectionIndex();
		if(index >= 0) {
			String description = chromatogramReportSupplierComboBox.getItem(index);
			String reportSupplierId = reportSupport.getReportSupplierId(description);
			return reportSupport.getReportSupplier(reportSupplierId);
		} else {
			throw new NoReportSupplierAvailableException("No report supplier has been selected.");
		}
	}

	@Override
	public void createControl(Composite parent) {

		GridLayout gridLayout;
		GridData gridData;
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		/*
		 * Select the output file format converter.
		 */
		reportSupport = ChromatogramReports.getChromatogramReportSupplierSupport();
		try {
			/*
			 * Label
			 */
			Label labelx = new Label(composite, SWT.NONE);
			labelx.setText("Select the chromatogram report supplier:");
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			gridData.heightHint = 30;
			labelx.setLayoutData(gridData);
			/*
			 * Output converter combo box.
			 */
			String[] filterNames = reportSupport.getFilterNames();
			chromatogramReportSupplierComboBox = EnhancedCombo.create(composite, SWT.NONE);
			chromatogramReportSupplierComboBox.setItems(filterNames);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			gridData.heightHint = 30;
			chromatogramReportSupplierComboBox.setLayoutData(gridData);
			/*
			 * Append file
			 */
			final Button appendReportButton = new Button(composite, SWT.CHECK);
			appendReportButton.setText("Append the chromatogram reports to a distinct file");
			appendReportButton.setSelection(appendReports);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.verticalIndent = 20;
			gridData.horizontalSpan = 2;
			appendReportButton.setLayoutData(gridData);
			appendReportButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					appendReports = (appendReportButton.getSelection() == false) ? false : true;
					buttonSelectFileOrFolder.setText((appendReports == true) ? "Select report file" : "Select report folder");
					label.setText((appendReports == true) ? "Chromatogram report file" : "Chromatogram report folder");
				}
			});
			/*
			 * Label
			 */
			label = new Label(composite, SWT.NONE);
			label.setText((appendReports == true) ? "Chromatogram report file" : "Chromatogram report folder");
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			gridData.heightHint = 30;
			gridData.verticalIndent = 20;
			label.setLayoutData(gridData);
			/*
			 * Text output folder
			 */
			chromatogramReportFolderOrFileTextBox = new Text(composite, SWT.BORDER);
			chromatogramReportFolderOrFileTextBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			/*
			 * Button select output folder.
			 */
			buttonSelectFileOrFolder = new Button(composite, SWT.NONE);
			buttonSelectFileOrFolder.setText((appendReports == true) ? "Select report file" : "Select report folder");
			buttonSelectFileOrFolder.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					Shell shell = Display.getCurrent().getActiveShell();
					String reportFolderOrFile;
					if(appendReports) {
						/*
						 * Select a file if the reports shall be appended.
						 * Try to get the report supplier default values.
						 */
						String fileName = "ChromatogramReport";
						String[] filterExtensions = new String[]{"*.*"};
						String[] filterNames = new String[]{"Chromatogram Report File (*.*)"};
						//
						try {
							IChromatogramReportSupplier reportSupplier = getReportSupplier();
							fileName = reportSupplier.getFileName();
							filterExtensions = new String[]{"*" + reportSupplier.getFileExtension()};
							filterNames = new String[]{reportSupplier.getReportName()};
						} catch(NoReportSupplierAvailableException e1) {
							logger.warn(e1);
						}
						/*
						 * Initialize the file dialog.
						 */
						FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
						fileDialog.setText("Select a chromatogram report file:");
						fileDialog.setFileName(fileName);
						fileDialog.setFilterExtensions(filterExtensions);
						fileDialog.setOverwrite(true);
						fileDialog.setFilterNames(filterNames);
						reportFolderOrFile = fileDialog.open();
					} else {
						/*
						 * Select a directory otherwise. The chromatogram name will be taken as the report file name.
						 */
						DirectoryDialog directoryDialog = new DirectoryDialog(shell);
						directoryDialog.setText("Select a chromatogram report folder:");
						reportFolderOrFile = directoryDialog.open();
					}
					/*
					 * Set the selection
					 */
					if(reportFolderOrFile != null) {
						chromatogramReportFolderOrFileTextBox.setText(reportFolderOrFile);
					}
				}
			});
		} catch(NoReportSupplierAvailableException e) {
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			gridData.heightHint = 30;
			label = new Label(composite, SWT.NONE);
			label.setText("Sorry, there are no report supplier available.");
			label.setLayoutData(gridData);
		}
		/*
		 * Set the control.
		 */
		setControl(composite);
	}
}
