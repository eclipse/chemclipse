/*******************************************************************************
 * Copyright (c) 2011, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.runnables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.core.PeakIdentificationBatchProcess;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io.PeakIdentificationBatchJobReader;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.report.IPeakIdentificationBatchProcessReport;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors.ResultsPage;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors.ResultsPage.SelectionUpdateListener;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class BatchRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(BatchRunnable.class);
	private File file;
	private String filePath;
	private PeakIdentificationBatchJobReader reader;

	public BatchRunnable(String filePath) {
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
			final IProcessingInfo<IPeakIdentificationBatchProcessReport> processingInfo = batchProcess.execute(peakIdentificationBatchJob, monitor);
			try {
				final IPeakIdentificationBatchProcessReport report = processingInfo.getProcessingResult();
				ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
				/*
				 * Update the peak results page
				 */
				SelectionUpdateListener selectionUpdateListener = new ResultsPage.SelectionUpdateListener();
				selectionUpdateListener.update(report.getPeaks(), true);
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