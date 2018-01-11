/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.support;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.converter.processing.database.IDatabaseImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class DatabaseImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(DatabaseImportRunnable.class);
	private File file;
	private IMassSpectra massSpectra;

	public DatabaseImportRunnable(File file) {
		this.file = file;
	}

	public IMassSpectra getMassSpectra() {

		return massSpectra;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Import Database", IProgressMonitor.UNKNOWN);
			IDatabaseImportConverterProcessingInfo processingInfo = DatabaseConverter.convert(file, monitor);
			massSpectra = processingInfo.getMassSpectra();
		} catch(Exception e) {
			/*
			 * Exceptions: FileNotFoundException
			 * NoChromatogramConverterAvailableException
			 * FileIsNotReadableException FileIsEmptyException
			 * ChromatogramIsNullException
			 */
			logger.warn(e);
		} finally {
			monitor.done();
		}
	}
}
