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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.nmr.converter.core.ScanConverterNMR;
import org.eclipse.chemclipse.nmr.model.core.IMeasurementNMR;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ScanNMRImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ScanNMRImportRunnable.class);
	//
	private File file;
	private IMeasurementNMR measurementNMR = null;

	public ScanNMRImportRunnable(File file) {
		this.file = file;
	}

	public IMeasurementNMR getScanNMR() {

		return measurementNMR;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Import Scan", IProgressMonitor.UNKNOWN);
			IProcessingInfo processingInfo = ScanConverterNMR.convert(file, monitor);
			measurementNMR = processingInfo.getProcessingResult(IMeasurementNMR.class);
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			monitor.done();
		}
	}
}
