/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramOverviewImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ChromatogramOverviewImportRunnable.class);
	private IChromatogramOverview chromatogramOverview;
	private File chromatogram;

	public ChromatogramOverviewImportRunnable(File chromatogram) {
		this.chromatogram = chromatogram;
	}

	/**
	 * Returns the chromatogram overview. But, use the class as a runnable.
	 * 
	 * @return
	 */
	public IChromatogramOverview getChromatogramOverview() {

		return chromatogramOverview;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Import Chromatogram Overview", IProgressMonitor.UNKNOWN);
			IProcessingInfo processingInfo = ChromatogramConverterMSD.getInstance().convertOverview(chromatogram, monitor);
			chromatogramOverview = processingInfo.getProcessingResult(IChromatogramOverview.class);
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
