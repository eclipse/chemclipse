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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.wizards;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io.JobWriter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.wizards.AbstractFileWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class BatchProcessJobWizard extends AbstractFileWizard {

	private static final Logger logger = Logger.getLogger(BatchProcessJobWizard.class);

	public BatchProcessJobWizard() {
		super("BatchJob", ".obj");
	}

	@Override
	public void doFinish(IProgressMonitor monitor) throws CoreException {

		final IFile file = super.prepareProject(monitor);
		//
		try {
			BatchProcessJob batchProcessJob = new BatchProcessJob();
			JobWriter batchProcessJobWriter = new JobWriter();
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
