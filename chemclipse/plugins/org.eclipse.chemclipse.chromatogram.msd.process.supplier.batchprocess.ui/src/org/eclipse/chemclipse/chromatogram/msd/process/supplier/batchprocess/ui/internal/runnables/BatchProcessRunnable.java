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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.core.BatchProcess;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class BatchProcessRunnable implements IRunnableWithProgress {

	private BatchProcessJob batchProcessJob;
	private IProcessingInfo processingInfo;

	public BatchProcessRunnable(BatchProcessJob batchProcessJob) {
		this.batchProcessJob = batchProcessJob;
	}

	public IProcessingInfo getProcessingInfo() {

		return processingInfo;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		BatchProcess batchProcess = new BatchProcess();
		processingInfo = batchProcess.execute(batchProcessJob, monitor);
	}
}
