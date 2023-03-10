/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.support;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class DatabaseExportRunnable implements IRunnableWithProgress {

	private File data;
	private File file;
	private IMassSpectra massSpectra;
	private ISupplier supplier;

	public DatabaseExportRunnable(File file, IMassSpectra massSpectra, ISupplier supplier) {

		this.file = file;
		this.massSpectra = massSpectra;
		this.supplier = supplier;
	}

	/**
	 * Returns the written chromatogram file or null.
	 */
	public File getData() {

		return data;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Export Mass Spectra", IProgressMonitor.UNKNOWN);
			IProcessingInfo<File> processingInfo = DatabaseConverter.convert(file, massSpectra, false, supplier.getId(), monitor);
			data = processingInfo.getProcessingResult();
		} finally {
			monitor.done();
		}
	}
}
