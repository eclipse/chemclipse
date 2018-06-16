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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.runnables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io.PeakIdentificationBatchJobWriter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class BatchJobExportRunnable implements IRunnableWithProgress {

	private File file;
	private IPeakIdentificationBatchJob peakIdentificationBatchJob;

	public BatchJobExportRunnable(File file, IPeakIdentificationBatchJob peakIdentificationBatchJob) {
		this.file = file;
		this.peakIdentificationBatchJob = peakIdentificationBatchJob;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		PeakIdentificationBatchJobWriter writer = new PeakIdentificationBatchJobWriter();
		try {
			writer.writeBatchProcessJob(file, peakIdentificationBatchJob, monitor);
		} catch(FileNotFoundException e) {
			throw new InterruptedException("The file " + file.getPath() + " couldn't be found.");
		} catch(FileIsNotWriteableException e) {
			throw new InterruptedException("The file " + file.getPath() + " is not writable.");
		} catch(IOException e) {
			throw new InterruptedException("The file " + file.getPath() + " makes problems.");
		} catch(XMLStreamException e) {
			throw new InterruptedException("There is a problem writing the file " + file.getPath());
		}
	}
}
