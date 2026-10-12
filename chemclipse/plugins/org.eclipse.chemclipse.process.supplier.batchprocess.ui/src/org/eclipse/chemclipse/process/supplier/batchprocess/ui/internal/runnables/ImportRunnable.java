/*******************************************************************************
 * Copyright (c) 2011, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchProcessJob;
import org.eclipse.chemclipse.process.supplier.batchprocess.io.JobReader;
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
			batchProcessJob = jobReader.read(file);
		} catch(Exception e) {
			throw new InterruptedException("Failed to process the file: " + file.getPath() + ".");
		}
	}
}
