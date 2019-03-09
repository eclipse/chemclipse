/*******************************************************************************
 * Copyright (c) 2012, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.ui.export.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.ux.extension.msd.ui.wizards.ChromatogramSelectionWizardPage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class ChromatogramReportExportWizard extends Wizard implements IExportWizard {

	private static final Logger logger = Logger.getLogger(ChromatogramReportExportWizard.class);
	private static final String DESCRIPTION = "Chromatogram Report";
	private static final String CONVERTER_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	private ChromatogramSelectionWizardPage chromatogramSelectionWizardPage;
	private ReportSupplierSelectionWizardPage reportSupplierSelectionWizardPage;

	public ChromatogramReportExportWizard() {

		setNeedsProgressMonitor(true);
		setWindowTitle("Chromatogram Report Wizard");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	@Override
	public void addPages() {

		chromatogramSelectionWizardPage = new ChromatogramSelectionWizardPage(DESCRIPTION, "Select (*.ocb) chromatograms for reporting", null);
		addPage(chromatogramSelectionWizardPage);
		reportSupplierSelectionWizardPage = new ReportSupplierSelectionWizardPage(DESCRIPTION, "Select the report provider", null);
		addPage(reportSupplierSelectionWizardPage);
	}

	@Override
	public boolean performFinish() {

		final List<String> inputFiles = getInputFiles();
		final Map<String, String> reportSupplier = getReportSupplier();
		IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					/*
					 * Append reports to one file?
					 */
					Set<String> reportSupplierIds = reportSupplier.keySet();
					for(String reportSupplierId : reportSupplierIds) {
						/*
						 * Process the chromatograms
						 */
						for(String inputFile : inputFiles) {
							/*
							 * Load each chromatogram
							 */
							File chromatogramFile = new File(inputFile);
							IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(chromatogramFile, CONVERTER_ID, monitor);
							try {
								IChromatogramMSD chromatogram = processingInfo.getProcessingResult();
								if(chromatogram != null) {
									/*
									 * Report: Append the reports or use distinct files?
									 */
									boolean appendReport;
									String reportFolderOrFile = reportSupplier.get(reportSupplierId);
									File chromatogramReportFile = new File(reportFolderOrFile);
									/*
									 * If it's a directory, then prepare the file name. Otherwise, the stored selection is the file name.
									 */
									if(chromatogramReportFile.isDirectory()) {
										appendReport = false;
										//
										if(!reportFolderOrFile.endsWith(File.separator)) {
											reportFolderOrFile += File.separator;
										}
										chromatogramReportFile = new File(reportFolderOrFile + chromatogram.getName());
									} else {
										appendReport = true;
									}
									/*
									 * Report the chromatogram.
									 */
									ChromatogramReports.generate(chromatogramReportFile, appendReport, chromatogram, reportSupplierId, monitor);
								}
							} catch(TypeCastException e) {
								logger.warn(e);
							}
						}
					}
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, runnableWithProgress);
		} catch(InvocationTargetException e) {
			logger.warn(e);
			showErrorMessage();
			return false;
		} catch(InterruptedException e) {
			logger.warn(e);
			showErrorMessage();
			return false;
		}
		MessageDialog.openInformation(getShell(), DESCRIPTION, "The reports have been created successfully.");
		return true;
	}

	private List<String> getInputFiles() {

		List<String> inputFiles = new ArrayList<>();
		Table inputFilesTable = chromatogramSelectionWizardPage.getTable();
		TableItem[] items = inputFilesTable.getItems();
		for(TableItem item : items) {
			inputFiles.add(item.getText(1));
		}
		return inputFiles;
	}

	private Map<String, String> getReportSupplier() {

		Map<String, String> reportSupplier = new HashMap<>();
		Table reportSupplierTable = reportSupplierSelectionWizardPage.getTable();
		TableItem[] items = reportSupplierTable.getItems();
		for(TableItem item : items) {
			String reportFolder = item.getText(1);
			String reportSupplierId = item.getText(2);
			reportSupplier.put(reportSupplierId, reportFolder);
		}
		return reportSupplier;
	}

	private void showErrorMessage() {

		MessageDialog.openError(getShell(), "Error", "An error has occurred reporting the chromatograms.");
	}
}
