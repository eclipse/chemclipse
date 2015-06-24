/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.support;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class MassSpectrumExportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(MassSpectrumExportRunnable.class);
	private File data;
	private File file;
	private IScanMSD massSpectrum;
	private ISupplier supplier;

	public MassSpectrumExportRunnable(File file, IScanMSD massSpectrum, ISupplier supplier) {

		this.file = file;
		this.massSpectrum = massSpectrum;
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
			monitor.beginTask("Export Mass Spectrum", IProgressMonitor.UNKNOWN);
			IMassSpectrumExportConverterProcessingInfo processingInfo = MassSpectrumConverter.convert(file, massSpectrum, false, supplier.getId(), new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			try {
				data = processingInfo.getFile();
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}
}
