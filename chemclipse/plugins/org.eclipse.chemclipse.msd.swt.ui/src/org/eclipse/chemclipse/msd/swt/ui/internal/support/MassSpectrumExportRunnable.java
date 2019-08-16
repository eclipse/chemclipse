/*******************************************************************************
 * Copyright (c) 2015, 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class MassSpectrumExportRunnable implements IRunnableWithProgress {

	@SuppressWarnings("unused")
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

	public File getData() {

		return data;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Export Mass Spectra", IProgressMonitor.UNKNOWN);
			IProcessingInfo<File> processingInfo = MassSpectrumConverter.convert(file, massSpectra, false, supplier.getId(), monitor);
			data = processingInfo.getProcessingResult();
		} finally {
			monitor.done();
		}
	}
}
