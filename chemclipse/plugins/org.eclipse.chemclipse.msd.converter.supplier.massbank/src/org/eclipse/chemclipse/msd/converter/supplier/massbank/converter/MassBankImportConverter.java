/*******************************************************************************
 * Copyright (c) 2014, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extend for massspectrum database useage
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.massbank.converter;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.msd.converter.database.IDatabaseImportConverter;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.massspectrum.IMassSpectrumImportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.massbank.io.MassBankReader;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassBankImportConverter implements IDatabaseImportConverter, IMassSpectrumImportConverter {

	private static final String DESCRIPTION = "MassBank MassSpectrum Import";

	@Override
	public IProcessingInfo<IMassSpectra> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		try {
			IMassSpectraReader massSpectraReader = new MassBankReader();
			IMassSpectra massSpectra = massSpectraReader.read(file, monitor);
			if(massSpectra != null && massSpectra.size() > 0) {
				processingInfo.setProcessingResult(massSpectra);
			} else {
				processingInfo.addErrorMessage(DESCRIPTION, "No mass spectra are stored in" + file.getAbsolutePath());
			}
		} catch(IOException e) {
			processingInfo.addErrorMessage(DESCRIPTION, "Error reading file: " + file.getAbsolutePath(), e);
		}
		return processingInfo;
	}
}
