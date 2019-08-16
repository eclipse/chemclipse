/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.ascii.core;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.massspectrum.AbstractMassSpectrumExportConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumExportConverter extends AbstractMassSpectrumExportConverter {

	@Override
	public IProcessingInfo convert(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) {

		return getProcessingInfo();
	}

	@Override
	public IProcessingInfo convert(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) {

		return getProcessingInfo();
	}

	private IProcessingInfo getProcessingInfo() {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addErrorMessage("ASCII Library", "The ASCII converter has no capabilities to export a library.");
		return processingInfo;
	}
}
