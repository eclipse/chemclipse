/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.runnables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.core.BatchProcess;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io.BatchProcessJobReader;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class BatchProcessRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(BatchProcessRunnable.class);
	private IBatchProcessJob batchProcessJob;
	private File file;
	private String filePath;
	private BatchProcessJobReader reader;

	public BatchProcessRunnable(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		reader = new BatchProcessJobReader();
		file = new File(filePath);
		try {
			monitor.beginTask("Batch Process", IProgressMonitor.UNKNOWN);
			batchProcessJob = reader.read(file, monitor);
			BatchProcess bp = new BatchProcess();
			IProcessingInfo processingInfo = bp.execute(batchProcessJob, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
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
