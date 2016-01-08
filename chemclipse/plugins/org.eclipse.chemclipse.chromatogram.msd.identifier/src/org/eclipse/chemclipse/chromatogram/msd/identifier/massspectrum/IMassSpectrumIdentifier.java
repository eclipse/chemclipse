/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IMassSpectraIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMassSpectrumIdentifier {

	/**
	 * Identifies a mass spectrum.
	 * 
	 * @param massSpectrum
	 * @param massSpectrumIdentifierSettings
	 * @param monitor
	 * @return {@link IMassSpectraIdentifierProcessingInfo}
	 */
	IMassSpectraIdentifierProcessingInfo identify(IScanMSD massSpectrum, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor);

	/**
	 * Identifies a mass spectrum.
	 * 
	 * @param massSpectrum
	 * @param monitor
	 * @return {@link IMassSpectraIdentifierProcessingInfo}
	 */
	IMassSpectraIdentifierProcessingInfo identify(IScanMSD massSpectrum, IProgressMonitor monitor);

	/**
	 * Identifies a list of mass spectra.
	 * 
	 * @param massSpectra
	 * @param massSpectrumIdentifierSettings
	 * @param monitor
	 * @return {@link IMassSpectraIdentifierProcessingInfo}
	 */
	IMassSpectraIdentifierProcessingInfo identify(List<? extends IScanMSD> massSpectra, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor);

	/**
	 * Identifies a list of mass spectra.
	 * 
	 * @param massSpectra
	 * @param monitor
	 * @return {@link IMassSpectraIdentifierProcessingInfo}
	 */
	IMassSpectraIdentifierProcessingInfo identify(List<? extends IScanMSD> massSpectra, IProgressMonitor monitor);
}
