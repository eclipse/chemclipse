/*******************************************************************************
 * Copyright (c) 2016, 2018 Matthias Mailänder.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.core;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.massspectrum.AbstractMassSpectrumImportConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.MassSpectrumImportConverterProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumImportConverter extends AbstractMassSpectrumImportConverter {

	@Override
	public IMassSpectrumImportConverterProcessingInfo convert(File file, IProgressMonitor monitor) {

		IMassSpectrumImportConverterProcessingInfo processingInfo = new MassSpectrumImportConverterProcessingInfo();
		processingInfo.addErrorMessage("CSV Mass Spectrum Import", "Mass spectrum import through CSV files isn't implemented yet.");
		return processingInfo;
	}
}
