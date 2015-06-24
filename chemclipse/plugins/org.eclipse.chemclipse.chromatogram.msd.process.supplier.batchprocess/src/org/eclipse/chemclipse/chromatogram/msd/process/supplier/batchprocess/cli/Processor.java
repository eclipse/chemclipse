/*******************************************************************************
 * Copyright (c) 2010, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.core.BatchProcess;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io.BatchProcessJobReader;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.rcp.app.cli.AbstractCommandLineProcessor;
import org.eclipse.chemclipse.rcp.app.cli.ICommandLineProcessor;

public class Processor extends AbstractCommandLineProcessor implements ICommandLineProcessor {

	@Override
	public void process(String[] args) {

		IProgressMonitor monitor;
		IBatchProcessJob batchProcessJob;
		BatchProcessJobReader reader;
		File file;
		/*
		 * Import the batch process job and execute it.
		 */
		reader = new BatchProcessJobReader();
		String filePath = args[0].trim();
		file = new File(filePath);
		try {
			monitor = new NullProgressMonitor();
			batchProcessJob = reader.read(file, monitor);
			BatchProcess bp = new BatchProcess();
			bp.execute(batchProcessJob, monitor);
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
