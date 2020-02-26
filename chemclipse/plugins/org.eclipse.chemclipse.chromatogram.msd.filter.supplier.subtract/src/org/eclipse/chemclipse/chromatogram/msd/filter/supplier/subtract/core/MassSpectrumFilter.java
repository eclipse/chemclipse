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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.AbstractMassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.IMassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.MassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.calculator.SubtractCalculator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumFilter extends AbstractMassSpectrumFilter {

	private static final String DESCRIPTION = "Subtract Filter Mass Spectrum";

	@Override
	public IProcessingInfo<IMassSpectrumFilterResult> applyFilter(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings filterSettings, IProgressMonitor monitor) {

		if(filterSettings == null) {
			filterSettings = PreferenceSupplier.getMassSpectrumFilterSettings();
		}
		IProcessingInfo<IMassSpectrumFilterResult> processingInfo = validate(massSpectra, filterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(filterSettings instanceof MassSpectrumFilterSettings) {
				MassSpectrumFilterSettings massSpectrumFilterSettings = (MassSpectrumFilterSettings)filterSettings;
				SubtractCalculator subtractCalculator = new SubtractCalculator();
				subtractCalculator.subtractMassSpectra(massSpectra, massSpectrumFilterSettings);
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The mass spectrum has been subtracted successfully."));
				IMassSpectrumFilterResult massSpectrumFilterResult = new MassSpectrumFilterResult(ResultStatus.OK, "The subtract filter has been applied successfully.");
				processingInfo.setProcessingResult(massSpectrumFilterResult);
			} else {
				processingInfo.addErrorMessage(DESCRIPTION, "The filter settings instance is not a type of: " + MassSpectrumFilterSettings.class);
			}
		}
		//
		return processingInfo;
	}
}
