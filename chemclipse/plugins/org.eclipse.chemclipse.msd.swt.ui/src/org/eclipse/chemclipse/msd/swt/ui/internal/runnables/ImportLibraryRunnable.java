/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ImportLibraryRunnable implements IRunnableWithProgress {

	private File file;
	private IMassSpectra massSpectra;

	public ImportLibraryRunnable(File file) {
		this.file = file;
	}

	public IMassSpectra getMassSpectra() {

		return massSpectra;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		IMassSpectrumImportConverterProcessingInfo processingInfo = MassSpectrumConverter.convert(file, monitor);
		massSpectra = processingInfo.getMassSpectra();
	}
}
