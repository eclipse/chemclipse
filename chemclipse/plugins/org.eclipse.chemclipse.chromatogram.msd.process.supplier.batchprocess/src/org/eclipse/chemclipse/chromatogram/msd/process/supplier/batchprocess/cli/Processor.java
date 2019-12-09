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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.core.BatchProcess;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io.JobReader;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.rcp.app.cli.AbstractCommandLineProcessor;
import org.eclipse.chemclipse.rcp.app.cli.ICommandLineProcessor;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class Processor extends AbstractCommandLineProcessor implements ICommandLineProcessor {

	private static final Logger logger = Logger.getLogger(Processor.class);

	@Override
	public void process(String[] args) {

		/*
		 * Import the batch process job and execute it.
		 */
		JobReader reader = new JobReader();
		String filePath = args[0].trim();
		File file = new File(filePath);
		try {
			IProgressMonitor monitor = new NullProgressMonitor();
			logger.info("Read batch process");
			BatchProcessJob batchProcessJob = reader.read(file, monitor);
			logger.info("Execute batch process");
			BatchProcess bp = new BatchProcess(new DataType[]{DataType.CSD, DataType.MSD, DataType.WSD}, new ProcessTypeSupport());
			bp.execute(batchProcessJob, monitor);
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
