/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.runnables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.core.PeakIdentificationBatchProcess;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io.PeakIdentificationBatchJobReader;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.processing.IPeakIdentificationProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.report.IPeakIdentificationBatchProcessReport;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors.PeakIdentificationResultsPage;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors.PeakIdentificationResultsPage.SelectionUpdateListener;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;

public class PeakIdentificationBatchRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(PeakIdentificationBatchRunnable.class);
	private File file;
	private String filePath;
	private PeakIdentificationBatchJobReader reader;

	public PeakIdentificationBatchRunnable(String filePath) {

		this.filePath = filePath;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		reader = new PeakIdentificationBatchJobReader();
		file = new File(filePath);
		try {
			monitor.beginTask("Peak Identification Batch Process", IProgressMonitor.UNKNOWN);
			IPeakIdentificationBatchJob peakIdentificationBatchJob = reader.read(file, monitor);
			PeakIdentificationBatchProcess batchProcess = new PeakIdentificationBatchProcess();
			final IPeakIdentificationProcessingInfo processingInfo = batchProcess.execute(peakIdentificationBatchJob, monitor);
			try {
				final IPeakIdentificationBatchProcessReport report = processingInfo.getPeakIdentificationBatchProcessReport();
				/*
				 * Update the peak results view.
				 */
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						/*
						 * Show the processing view if error messages occurred.
						 */
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {

								ProcessingInfoViewSupport.showErrorInfoReminder(processingInfo);
							}
						});
						ProcessingInfoViewSupport.updateProcessingInfoView(processingInfo);
						/*
						 * Update the peak results page
						 */
						SelectionUpdateListener selectionUpdateListener = new PeakIdentificationResultsPage.SelectionUpdateListener();
						selectionUpdateListener.update(report.getPeaks(), true);
					}
				});
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(FileIsNotReadableException e) {
			logger.warn(e);
		} catch(FileIsEmptyException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
	}
}