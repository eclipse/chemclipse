/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.AbstractMassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.IMassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.MassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.calculator.FilterSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ISavitzkyGolayMassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumFilter extends AbstractMassSpectrumFilter {

	private static final String DESCRIPTION = "Savitzky-Golay Mass Spectra Smoother";

	@Override
	public IProcessingInfo applyFilter(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings massSpectrumFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(validate(massSpectra, massSpectrumFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		if(massSpectrumFilterSettings instanceof ISavitzkyGolayMassSpectrumFilterSettings) {
			//
			ISavitzkyGolayMassSpectrumFilterSettings savitzkyGolayMassSpectrumFilterSettings = (ISavitzkyGolayMassSpectrumFilterSettings)massSpectrumFilterSettings;
			FilterSupplier filterSupplier = new FilterSupplier();
			//
			int derivative = savitzkyGolayMassSpectrumFilterSettings.getDerivative();
			int order = savitzkyGolayMassSpectrumFilterSettings.getOrder();
			int width = savitzkyGolayMassSpectrumFilterSettings.getWidth();
			filterSupplier.applySavitzkyGolayFilter(massSpectra, derivative, order, width, monitor);
			//
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The mass spectrum has been successfully smoothed."));
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The filter settings instance is not of type: " + ISavitzkyGolayMassSpectrumFilterSettings.class);
		}
		//
		IMassSpectrumFilterResult massSpectrumFilterResult = new MassSpectrumFilterResult(ResultStatus.OK, "The Savitzky-Golay filter has been applied successfully.");
		processingInfo.setProcessingResult(massSpectrumFilterResult);
		return processingInfo;
	}

	// Convenient Methods
	@Override
	public IProcessingInfo applyFilter(IScanMSD massSpectrum, IMassSpectrumFilterSettings massSpectrumFilterSettings, IProgressMonitor monitor) {

		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		massSpectra.add(massSpectrum);
		return applyFilter(massSpectra, massSpectrumFilterSettings, monitor);
	}

	@Override
	public IProcessingInfo applyFilter(IScanMSD massSpectrum, IProgressMonitor monitor) {

		IMassSpectrumFilterSettings massSpectrumFilterSettings = PreferenceSupplier.getMassSpectrumFilterSettings();
		return applyFilter(massSpectrum, massSpectrumFilterSettings, monitor);
	}

	@Override
	public IProcessingInfo applyFilter(List<IScanMSD> massSpectra, IProgressMonitor monitor) {

		IMassSpectrumFilterSettings massSpectrumFilterSEttings = PreferenceSupplier.getMassSpectrumFilterSettings();
		return applyFilter(massSpectra, massSpectrumFilterSEttings, monitor);
	}
}
