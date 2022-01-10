/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.io.JobReader;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ImportRunnable implements IRunnableWithProgress {

	private File file;
	private BatchProcessJob batchProcessJob = null;

	public ImportRunnable(File file) {

		this.file = file;
	}

	public BatchProcessJob getBatchProcessJob() {

		return batchProcessJob;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			JobReader jobReader = new JobReader();
			batchProcessJob = jobReader.read(file, monitor);
		} catch(Exception e) {
			throw new InterruptedException("Failed to process the file: " + file.getPath() + ".");
		}
	}
}