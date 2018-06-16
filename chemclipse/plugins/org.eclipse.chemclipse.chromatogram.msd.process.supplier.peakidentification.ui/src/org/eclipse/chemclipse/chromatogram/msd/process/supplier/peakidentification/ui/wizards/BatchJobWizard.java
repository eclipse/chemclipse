/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.wizards;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io.IPeakIdentificationBatchJobWriter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io.PeakIdentificationBatchJobWriter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.PeakIdentificationBatchJob;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.wizards.AbstractFileWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class BatchJobWizard extends AbstractFileWizard {

	private static final Logger logger = Logger.getLogger(BatchJobWizard.class);

	public BatchJobWizard() {
		super("PeakIdentificationBatchJob", ".opi");
	}

	@Override
	public void doFinish(IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Create Peak Identification Batch Job", IProgressMonitor.UNKNOWN);
		final IFile file = super.prepareProject(monitor);
		/*
		 * Initialize a simple peak identification batch process job.
		 */
		IPeakIdentificationBatchJob batchProcessJob = new PeakIdentificationBatchJob(file.getName());
		batchProcessJob.setOverrideReport(false);
		batchProcessJob.setReportFolder(file.getProject().getLocation().toString());
		/*
		 * Write the batch process job to the given file.
		 */
		try {
			IPeakIdentificationBatchJobWriter batchProcessJobWriter = new PeakIdentificationBatchJobWriter();
			batchProcessJobWriter.writeBatchProcessJob(file.getLocation().toFile(), batchProcessJob, monitor);
		} catch(FileNotFoundException e1) {
			logger.warn(e1);
		} catch(FileIsNotWriteableException e1) {
			logger.warn(e1);
		} catch(IOException e1) {
			logger.warn(e1);
		} catch(XMLStreamException e1) {
			logger.warn(e1);
		}
		/*
		 * Refresh
		 */
		super.refreshWorkspace(monitor);
		super.runOpenEditor(file, monitor);
	}
}
