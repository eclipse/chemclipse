/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.massspectrum;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.core.IExportConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

/**
 * @author eselmeister
 */
public interface IMassSpectrumExportConverter extends IExportConverter {

	/**
	 * Exports the mass spectrum to the given file.
	 * 
	 * @param file
	 * @param massSpectrum
	 * @param append
	 * @param monitor
	 * @return {@link IMassSpectrumExportConverterProcessingInfo}
	 */
	IMassSpectrumExportConverterProcessingInfo convert(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor);

	/**
	 * Exports the mass spectra to the given file.
	 * 
	 * @param file
	 * @param massSpectra
	 * @param append
	 * @param monitor
	 * @return {@link IMassSpectrumExportConverterProcessingInfo}
	 */
	IMassSpectrumExportConverterProcessingInfo convert(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor);

	/**
	 * Checks the mass spectrum instance and throws an exception if the mass
	 * spectrum is null.
	 * 
	 * @param massSpectrum
	 * @return {@link IMassSpectrumExportConverterProcessingInfo}
	 */
	IMassSpectrumExportConverterProcessingInfo validate(IScanMSD massSpectrum);

	/**
	 * Checks the mass spectra instance and throws an exception if the mass
	 * spectrum is null.
	 * 
	 * @param massSpectrum
	 * @return {@link IMassSpectrumExportConverterProcessingInfo}
	 */
	IMassSpectrumExportConverterProcessingInfo validate(IMassSpectra massSpectra);
}
