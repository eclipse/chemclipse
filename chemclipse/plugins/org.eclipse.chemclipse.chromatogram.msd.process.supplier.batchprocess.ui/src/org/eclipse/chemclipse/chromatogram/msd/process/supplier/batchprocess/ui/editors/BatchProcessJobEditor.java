/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io.BatchProcessJobWriter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.runnables.BatchProcessExportRunnable;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.runnables.BatchProcessImportRunnable;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;

public class BatchProcessJobEditor extends MultiPageEditorPart {

	private static final Logger logger = Logger.getLogger(BatchProcessJobEditor.class);
	/*
	 * A reference to the file kept by the actual editor.
	 */
	private IFile editorBatchProcessJobFile;
	private IBatchProcessJob batchProcessJob;
	private boolean isDirty = false;
	/*
	 * Pages.
	 */
	private BatchProcessEditorPage batchProcessEditorPage;
	private List<IMultiEditorPage> pages = new ArrayList<IMultiEditorPage>();
	/*
	 * Indices of the pages.
	 */
	private static final String BATCHPROCESS_EDITOR_PAGE = "bpp";
	public static final String INPUT_FILES_PAGE = "ifp";
	public static final String PROCESS_ENTRIES_PAGE = "pep";
	public static final String OUTPUT_FILES_PAGE = "ofp";
	public static final String REPORT_ENTRIES_PAGE = "rep";
	/*
	 * Store the indices of the pages.
	 */
	private HashMap<String, Integer> pagesMap = new HashMap<String, Integer>();

	/**
	 * Sets the active page.
	 * USE IT ONLY IF NECESSARY.
	 * E.g.: key -> BATCHPROCESS_EDITOR_PAGE
	 * 
	 * @param key
	 */
	protected void setActivePage(String key) {

		if(pagesMap != null) {
			Integer pageIndex = pagesMap.get(key);
			if(pageIndex != null) {
				setActivePage(pageIndex);
			}
		}
	}

	@Override
	public boolean isDirty() {

		return isDirty;
	}

	/**
	 * Sets the editor dirty.
	 */
	protected void setDirty() {

		this.isDirty = true;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	protected void createPages() {

		/*
		 * Create the pages.
		 */
		createBatchProcessEditorPage();
		createInputFilesPage();
		createProcessEntriesPage();
		createOutputFilesPage();
		createReportEntriesPage();
		/*
		 * Set the batch process job.
		 */
		setBatchProcessJobInPages();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		super.init(site, input);
		String fileName = input.getName();
		fileName = fileName.substring(0, fileName.length() - 4);
		setPartName(fileName);
		if(input instanceof IFileEditorInput) {
			editorBatchProcessJobFile = ((IFileEditorInput)input).getFile();
			Display display = Display.getCurrent();
			BatchProcessImportRunnable runnable = new BatchProcessImportRunnable(editorBatchProcessJobFile.getLocation().toFile());
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
			try {
				monitor.run(false, true, runnable);
				batchProcessJob = runnable.getBatchProcessJob();
			} catch(InvocationTargetException e) {
				logger.warn(e);
			} catch(InterruptedException e) {
				logger.warn(e);
			}
		} else {
			throw new PartInitException("The file could't be loaded.");
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		/*
		 * Save the file.
		 */
		if(editorBatchProcessJobFile != null) {
			BatchProcessJobWriter writer = new BatchProcessJobWriter();
			try {
				writer.writeBatchProcessJob(editorBatchProcessJobFile.getLocation().toFile(), batchProcessJob, monitor);
				isDirty = false;
				firePropertyChange(IEditorPart.PROP_DIRTY);
			} catch(FileNotFoundException e) {
				logger.warn(e);
			} catch(FileIsNotWriteableException e) {
				logger.warn(e);
			} catch(IOException e) {
				logger.warn(e);
			} catch(XMLStreamException e) {
				logger.warn(e);
			}
		}
	}

	@Override
	public void doSaveAs() {

		Display display = Display.getCurrent();
		Shell shell = display.getActiveShell();
		/*
		 * File dialog.
		 */
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Save the batch job");
		dialog.setFileName("BatchJob.obj");
		String fileName = dialog.open();
		/*
		 * If the file name is not null, try to export the batch job.
		 */
		if(fileName != null) {
			File file = new File(fileName);
			IRunnableWithProgress runnable = new BatchProcessExportRunnable(file, batchProcessJob);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
			try {
				monitor.run(true, true, runnable);
			} catch(InvocationTargetException e) {
				logger.warn(e);
			} catch(InterruptedException e) {
				logger.warn(e);
			}
		}
	}

	@Override
	public boolean isSaveAsAllowed() {

		return true;
	}

	// -------------------------------------------------private methods
	/**
	 * Sets the batch process job in all registered pages.
	 */
	private void setBatchProcessJobInPages() {

		for(IMultiEditorPage page : pages) {
			if(page != null) {
				page.setBatchProcessJob(batchProcessJob);
			}
		}
	}

	/**
	 * Creates the editor page.
	 * 
	 */
	private void createBatchProcessEditorPage() {

		batchProcessEditorPage = new BatchProcessEditorPage(this, getContainer());
		pages.add(batchProcessEditorPage);
		pagesMap.put(BATCHPROCESS_EDITOR_PAGE, batchProcessEditorPage.getPageIndex());
		setPageText(pagesMap.get(BATCHPROCESS_EDITOR_PAGE), "Batch Process Job Editor");
	}

	/**
	 * Create the input files page.
	 */
	private void createInputFilesPage() {

		InputEntriesPage page = new InputEntriesPage(this, getContainer());
		pages.add(page);
		pagesMap.put(INPUT_FILES_PAGE, page.getPageIndex());
		setPageText(pagesMap.get(INPUT_FILES_PAGE), "Input Files");
	}

	/**
	 * Create the process entries page.
	 */
	private void createProcessEntriesPage() {

		ProcessEntriesPage page = new ProcessEntriesPage(this, getContainer());
		pages.add(page);
		pagesMap.put(PROCESS_ENTRIES_PAGE, page.getPageIndex());
		setPageText(pagesMap.get(PROCESS_ENTRIES_PAGE), "Process Entries");
	}

	/**
	 * Create the output files page.
	 */
	private void createOutputFilesPage() {

		OutputEntriesPage page = new OutputEntriesPage(this, getContainer());
		pages.add(page);
		pagesMap.put(OUTPUT_FILES_PAGE, page.getPageIndex());
		setPageText(pagesMap.get(OUTPUT_FILES_PAGE), "Output Files");
	}

	/**
	 * Create the report entries page.
	 */
	private void createReportEntriesPage() {

		ReportEntriesPage page = new ReportEntriesPage(this, getContainer());
		pages.add(page);
		pagesMap.put(REPORT_ENTRIES_PAGE, page.getPageIndex());
		setPageText(pagesMap.get(REPORT_ENTRIES_PAGE), "Report Supplier");
	}

	@Override
	public void dispose() {

		for(IMultiEditorPage page : pages) {
			if(page != null) {
				page.dispose();
			}
		}
		super.dispose();
	}
	// -------------------------------------------------private methods
}