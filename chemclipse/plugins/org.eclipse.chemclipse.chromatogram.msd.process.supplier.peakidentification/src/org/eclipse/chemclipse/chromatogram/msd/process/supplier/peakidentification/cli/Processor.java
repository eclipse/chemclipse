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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.core.IPeakIdentificationBatchProcess;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.core.PeakIdentificationBatchProcess;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io.PeakIdentificationBatchJobReader;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.rcp.app.cli.AbstractCommandLineProcessor;
import org.eclipse.chemclipse.rcp.app.cli.ICommandLineProcessor;

public class Processor extends AbstractCommandLineProcessor implements ICommandLineProcessor {

	@Override
	public void process(String[] args) {

		IProgressMonitor monitor;
		IPeakIdentificationBatchJob peakIdentificationBatchJob;
		PeakIdentificationBatchJobReader reader;
		File file;
		/*
		 * Import the batch process job and execute it.
		 */
		reader = new PeakIdentificationBatchJobReader();
		String filePath = args[0].trim();
		file = new File(filePath);
		try {
			monitor = new NullProgressMonitor();
			peakIdentificationBatchJob = reader.read(file, monitor);
			IPeakIdentificationBatchProcess batchProcess = new PeakIdentificationBatchProcess();
			batchProcess.execute(peakIdentificationBatchJob, monitor);
		} catch(FileNotFoundException e) {
			System.out.println(e);
		} catch(FileIsNotReadableException e) {
			System.out.println(e);
		} catch(FileIsEmptyException e) {
			System.out.println(e);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
}
