/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.editors;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter.SampleQuantExportCSV;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter.SampleQuantExportPDF;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter.SampleQuantReader;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter.SampleQuantWriter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.files.ExtendedFileDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public class EditorSampleQuant extends MultiPageEditorPart {

	private static final Logger logger = Logger.getLogger(EditorSampleQuant.class);
	//
	private PageSampleQuant pageSampleQuant;
	private File file;
	private boolean isDirty = false;
	private boolean initialize = true;
	private ISampleQuantReport sampleQuantReport;

	@Override
	protected void createPages() {

		pageSampleQuant = new PageSampleQuant(getContainer(), this);
		int pageIndex = addPage(pageSampleQuant.getControl());
		setPageText(pageIndex, "Sample Quant Report (*.sqr)");
		setDirty(true);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		try {
			SampleQuantWriter sampleQuantWriter = new SampleQuantWriter();
			sampleQuantWriter.write(file, sampleQuantReport, new NullProgressMonitor());
			setDirty(false);
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	@Override
	public void doSaveAs() {

		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog fileDialog = ExtendedFileDialog.create(shell, SWT.SAVE);
		fileDialog.setText("Save the *.sqr report.");
		fileDialog.setFilterExtensions(new String[]{"*.sqr", "*.csv", "*.pdf"});
		fileDialog.setFilterNames(new String[]{"Sample Quant Report *.sqr", "CSV Report *.csv", "PDF Report *.pdf"});
		fileDialog.setFileName(file.getName());
		String reportFile = fileDialog.open();
		if(reportFile != null) {
			try {
				String fileName = reportFile.toLowerCase();
				int filterIndex = fileDialog.getFilterIndex();
				if(filterIndex == 1) {
					/*
					 * csv
					 */
					if(!fileName.endsWith(".csv")) {
						reportFile += ".csv";
					}
					File file = new File(reportFile);
					SampleQuantExportCSV sampleQuantExportCSV = new SampleQuantExportCSV();
					sampleQuantExportCSV.write(file, sampleQuantReport, new NullProgressMonitor());
				} else if(filterIndex == 2) {
					/*
					 * pdf
					 */
					if(!fileName.endsWith(".pdf")) {
						reportFile += ".pdf";
					}
					File file = new File(reportFile);
					SampleQuantExportPDF sampleQuantExportPDF = new SampleQuantExportPDF();
					sampleQuantExportPDF.write(file, sampleQuantReport, new NullProgressMonitor());
				} else {
					/*
					 * sqr
					 */
					if(!fileName.endsWith(".sqr")) {
						reportFile += ".sqr";
					}
					File file = new File(reportFile);
					SampleQuantWriter sampleQuantWriter = new SampleQuantWriter();
					sampleQuantWriter.write(file, sampleQuantReport, new NullProgressMonitor());
					setDirty(false);
				}
				//
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
				messageBox.setText("Export Sample Quant Report");
				messageBox.setMessage("The report has been exported successfully.");
				messageBox.open();
			} catch(Exception e) {
				logger.warn(e);
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
				messageBox.setText("Export Sample Quant Report");
				messageBox.setMessage("Something has gone wrong while exporting the report.");
				messageBox.open();
			}
		}
	}

	@Override
	public boolean isSaveAsAllowed() {

		return true;
	}

	@Override
	public void setFocus() {

		if(initialize) {
			initialize = false;
			try {
				SampleQuantReader sampleQuantReader = new SampleQuantReader();
				sampleQuantReport = sampleQuantReader.read(file, new NullProgressMonitor());
				pageSampleQuant.showData(sampleQuantReport);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		super.init(site, input);
		String fileName = input.getName();
		fileName = fileName.substring(0, fileName.length() - 4);
		setPartName(fileName);
		if(input instanceof IFileEditorInput fileEditorInput) {
			file = fileEditorInput.getFile().getLocation().toFile();
		} else {
			throw new PartInitException("The file could't be loaded.");
		}
	}

	@Override
	public void dispose() {

		super.dispose();
	}

	@Override
	public boolean isDirty() {

		return isDirty;
	}

	public void setDirty(boolean isDirty) {

		this.isDirty = isDirty;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}
}
