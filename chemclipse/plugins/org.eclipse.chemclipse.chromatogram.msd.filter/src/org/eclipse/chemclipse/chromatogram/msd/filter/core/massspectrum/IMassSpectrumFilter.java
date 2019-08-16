/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public interface IMassSpectrumFilter {

	/**
	 * Applies the filter to the selected mass spectrum using the settings.
	 * 
	 * @param massSpectrum
	 * @param massSpectrumFilterSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo applyFilter(IScanMSD massSpectrum, IMassSpectrumFilterSettings massSpectrumFilterSettings, IProgressMonitor monitor);

	/**
	 * Applies the filter to the selected mass spectrum.
	 * 
	 * @param massSpectrum
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo applyFilter(IScanMSD massSpectrum, IProgressMonitor monitor);

	/**
	 * Applies the filter to the selected mass spectra using the settings.
	 * 
	 * @param massSpectra
	 * @param massSpectrumFilterSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo applyFilter(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings massSpectrumFilterSettings, IProgressMonitor monitor);

	/**
	 * Applies the filter to the selected mass spectra.
	 * 
	 * @param massSpectra
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo applyFilter(List<IScanMSD> massSpectra, IProgressMonitor monitor);

	/**
	 * Validates the mass spectrum and the settings.
	 * 
	 * @param massSpectrum
	 * @param massSpectrumFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(IScanMSD massSpectrum, IMassSpectrumFilterSettings massSpectrumFilterSettings);

	/**
	 * Validates the mass spectra and the settings.
	 * 
	 * @param massSpectra
	 * @param massSpectrumFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings massSpectrumFilterSettings);
}
