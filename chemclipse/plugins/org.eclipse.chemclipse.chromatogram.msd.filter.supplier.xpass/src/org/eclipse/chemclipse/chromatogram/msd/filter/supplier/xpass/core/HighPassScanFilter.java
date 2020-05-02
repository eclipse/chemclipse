/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add generics, remove obsolete methods
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.AbstractMassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.IMassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.MassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.HighPassFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class HighPassScanFilter extends AbstractMassSpectrumFilter {

	private static final String DESCRIPTION = "High Pass Ion(s)";

	@Override
	public IProcessingInfo<IMassSpectrumFilterResult> applyFilter(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings filterSettings, IProgressMonitor monitor) {

		if(filterSettings == null) {
			filterSettings = PreferenceSupplier.getHighPassFilterSettings();
		}
		IProcessingInfo<IMassSpectrumFilterResult> processingInfo = validate(massSpectra, filterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(filterSettings instanceof HighPassFilterSettings) {
				/*
				 * Filter
				 */
				HighPassFilterSettings settings = (HighPassFilterSettings)filterSettings;
				int number = settings.getNumberHighest();
				for(IScanMSD massSpectrum : massSpectra) {
					XPassFilter.applyHighPass(massSpectrum, number);
				}
				//
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The mass spectrum has been optimized successfully."));
				IMassSpectrumFilterResult massSpectrumFilterResult = new MassSpectrumFilterResult(ResultStatus.OK, "The high pass filter has been applied successfully.");
				processingInfo.setProcessingResult(massSpectrumFilterResult);
			} else {
				processingInfo.addErrorMessage(DESCRIPTION, "The filter settings instance is not a type of: " + HighPassFilterSettings.class);
			}
		}
		//
		return processingInfo;
	}
}
