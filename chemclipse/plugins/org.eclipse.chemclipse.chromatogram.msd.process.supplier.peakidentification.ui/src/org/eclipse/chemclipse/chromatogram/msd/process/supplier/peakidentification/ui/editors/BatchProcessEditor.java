/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors;

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

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io.PeakIdentificationBatchJobWriter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.runnables.BatchJobExportRunnable;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.runnables.BatchJobImportRunnable;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;

public class BatchProcessEditor extends MultiPageEditorPart {

	private static final Logger logger = Logger.getLogger(BatchProcessEditor.class);
	/*
	 * A reference to the file kept by the actual editor.
	 */
	private IFile editorBatchProcessJobFile;
	private IPeakIdentificationBatchJob peakIdentificationBatchJob;
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
	public static final String PEAK_INPUT_FILES_PAGE = "ifp";
	public static final String PEAK_OUTPUT_FILES_PAGE = "ofp";
	public static final String PEAK_IDENTIFICATION_RESULTS_PAGE = "prp";
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
	public void setFocus() {

		super.setFocus();
		int index = getActivePage();
		IMultiEditorPage multiEditorPage = pages.get(index);
		multiEditorPage.setFocus();
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
		createPeakIdentificationBatchProcessEditorPage();
		createPeakInputFilesPage();
		createPeakOutputFilesPage();
		createPeakIdentificationResultsPage();
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
			BatchJobImportRunnable runnable = new BatchJobImportRunnable(editorBatchProcessJobFile.getLocation().toFile());
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
			try {
				monitor.run(false, true, runnable);
				peakIdentificationBatchJob = runnable.getPeakIdentificationBatchJob();
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
			PeakIdentificationBatchJobWriter writer = new PeakIdentificationBatchJobWriter();
			try {
				writer.writeBatchProcessJob(editorBatchProcessJobFile.getLocation().toFile(), peakIdentificationBatchJob, monitor);
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
		dialog.setText("Save the identification batch job");
		dialog.setFileName("PeakIdentificationBatchJob.opi");
		String fileName = dialog.open();
		/*
		 * If the file name is not null, try to export the batch job.
		 */
		if(fileName != null) {
			File file = new File(fileName);
			IRunnableWithProgress runnable = new BatchJobExportRunnable(file, peakIdentificationBatchJob);
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

	/**
	 * Sets the batch process job in all registered pages.
	 */
	private void setBatchProcessJobInPages() {

		for(IMultiEditorPage page : pages) {
			if(page != null) {
				page.setPeakIdentificationBatchJob(peakIdentificationBatchJob);
			}
		}
	}

	/**
	 * Creates the editor page.
	 * 
	 */
	private void createPeakIdentificationBatchProcessEditorPage() {

		batchProcessEditorPage = new BatchProcessEditorPage(this, getContainer());
		pages.add(batchProcessEditorPage);
		pagesMap.put(BATCHPROCESS_EDITOR_PAGE, batchProcessEditorPage.getPageIndex());
		setPageText(pagesMap.get(BATCHPROCESS_EDITOR_PAGE), "Peak Identification Batch Editor");
	}

	/**
	 * Create the input files page.
	 */
	private void createPeakInputFilesPage() {

		PeakInputFilesPage page = new PeakInputFilesPage(this, getContainer());
		pages.add(page);
		pagesMap.put(PEAK_INPUT_FILES_PAGE, page.getPageIndex());
		setPageText(pagesMap.get(PEAK_INPUT_FILES_PAGE), "Peak Input Files");
	}

	/**
	 * Create the output files page.
	 */
	private void createPeakOutputFilesPage() {

		PeakOutputFilesPage page = new PeakOutputFilesPage(this, getContainer());
		pages.add(page);
		pagesMap.put(PEAK_OUTPUT_FILES_PAGE, page.getPageIndex());
		setPageText(pagesMap.get(PEAK_OUTPUT_FILES_PAGE), "Peak Output Files");
	}

	/**
	 * Create the results page.
	 */
	private void createPeakIdentificationResultsPage() {

		ResultsPage page = new ResultsPage(this, getContainer());
		pages.add(page);
		pagesMap.put(PEAK_IDENTIFICATION_RESULTS_PAGE, page.getPageIndex());
		setPageText(pagesMap.get(PEAK_IDENTIFICATION_RESULTS_PAGE), "Peak Identification Results");
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
}