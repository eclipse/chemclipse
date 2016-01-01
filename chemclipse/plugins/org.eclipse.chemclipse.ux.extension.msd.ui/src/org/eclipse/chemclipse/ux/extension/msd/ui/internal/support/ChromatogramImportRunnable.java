/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.support;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.logging.core.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ChromatogramImportRunnable.class);
	private File file;
	private ChromatogramSelectionMSD chromatogramSelection;

	public ChromatogramImportRunnable(File file, ChromatogramSelectionMSD chromatogramSelection) {
		this.file = file;
		this.chromatogramSelection = chromatogramSelection;
	}

	public ChromatogramSelectionMSD getChromatogramSelection() {

		return chromatogramSelection;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Import Chromatogram", IProgressMonitor.UNKNOWN);
			IChromatogramMSDImportConverterProcessingInfo processingInfo = ChromatogramConverterMSD.convert(file, monitor);
			IChromatogramMSD chromatogram = processingInfo.getChromatogram();
			chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
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
