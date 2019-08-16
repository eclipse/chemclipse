/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractMassSpectrumFilter implements IMassSpectrumFilter {

	private static final String DESCRIPTION = "Mass Spectrum Filter";

	@Override
	public IProcessingInfo validate(IScanMSD massSpectrum, IMassSpectrumFilterSettings massSpectrumFilterSettings) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(validateMassSpectrum(massSpectrum));
		processingInfo.addMessages(validateFilterSettings(massSpectrumFilterSettings));
		return processingInfo;
	}

	@Override
	public IProcessingInfo validate(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings massSpectrumFilterSettings) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(validateMassSpectra(massSpectra));
		processingInfo.addMessages(validateFilterSettings(massSpectrumFilterSettings));
		return processingInfo;
	}

	/**
	 * Validates the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return {@link IProcessingInfo}
	 */
	private IProcessingInfo validateMassSpectrum(IScanMSD massSpectrum) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(massSpectrum == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The mass spectrum is not valid.");
		}
		return processingInfo;
	}

	/**
	 * Validates the mass spectra.
	 * 
	 * @param massSpectra
	 * @return {@link IProcessingInfo}
	 */
	private IProcessingInfo validateMassSpectra(List<IScanMSD> massSpectra) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(massSpectra == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The mass spectrum list is not valid.");
		}
		return processingInfo;
	}

	/**
	 * Validates that the filter settings are not null.
	 * 
	 * @param massSpectrumFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	private IProcessingInfo validateFilterSettings(IMassSpectrumFilterSettings massSpectrumFilterSettings) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(massSpectrumFilterSettings == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The filter settings are not valid.");
		}
		return processingInfo;
	}
}
