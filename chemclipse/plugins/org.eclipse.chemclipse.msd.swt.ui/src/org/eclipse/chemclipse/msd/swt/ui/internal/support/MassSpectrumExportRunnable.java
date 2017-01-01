/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class MassSpectrumExportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(MassSpectrumExportRunnable.class);
	private File data;
	private File file;
	private IMassSpectra massSpectra;
	private ISupplier supplier;

	public MassSpectrumExportRunnable(File file, IMassSpectra massSpectra, ISupplier supplier) {
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
			IMassSpectrumExportConverterProcessingInfo processingInfo = MassSpectrumConverter.convert(file, massSpectra, false, supplier.getId(), new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
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
