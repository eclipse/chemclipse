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
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.AbstractMassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.processing.IMassSpectrumFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.filter.processing.MassSpectrumFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.IMassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.MassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.calculator.FilterSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.settings.ISnipMassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public class MassSpectrumFilter extends AbstractMassSpectrumFilter {

	private static final String DESCRIPTION = "SNIP Filter Mass Spectra";

	@Override
	public IMassSpectrumFilterProcessingInfo applyFilter(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings massSpectrumFilterSettings, IProgressMonitor monitor) {

		IMassSpectrumFilterProcessingInfo processingInfo = new MassSpectrumFilterProcessingInfo();
		processingInfo.addMessages(validate(massSpectra, massSpectrumFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Apply the SNIP algorithm to the mass spectrum.
		 */
		if(massSpectrumFilterSettings instanceof ISnipMassSpectrumFilterSettings) {
			//
			ISnipMassSpectrumFilterSettings snipMassSpectrumFilterSettings = (ISnipMassSpectrumFilterSettings)massSpectrumFilterSettings;
			FilterSupplier filterSupplier = new FilterSupplier();
			//
			int iterations = snipMassSpectrumFilterSettings.getIterations();
			int transitions = snipMassSpectrumFilterSettings.getTransitions();
			double magnificationFactor = snipMassSpectrumFilterSettings.getMagnificationFactor();
			filterSupplier.applySnipFilter(massSpectra, iterations, transitions, magnificationFactor, monitor);
			//
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The mass spectrum has been optimized successfully."));
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The filter settings instance is not a type of: " + ISnipMassSpectrumFilterSettings.class);
		}
		//
		IMassSpectrumFilterResult massSpectrumFilterResult = new MassSpectrumFilterResult(ResultStatus.OK, "The SNIP filter has been applied successfully.");
		processingInfo.setMassSpectrumFilterResult(massSpectrumFilterResult);
		return processingInfo;
	}

	// ----------------------------------------------------CONVENIENT METHODS
	@Override
	public IMassSpectrumFilterProcessingInfo applyFilter(IScanMSD massSpectrum, IMassSpectrumFilterSettings massSpectrumFilterSettings, IProgressMonitor monitor) {

		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		massSpectra.add(massSpectrum);
		return applyFilter(massSpectra, massSpectrumFilterSettings, monitor);
	}

	@Override
	public IMassSpectrumFilterProcessingInfo applyFilter(IScanMSD massSpectrum, IProgressMonitor monitor) {

		IMassSpectrumFilterSettings massSpectrumFilterSettings = PreferenceSupplier.getMassSpectrumFilterSettings();
		return applyFilter(massSpectrum, massSpectrumFilterSettings, monitor);
	}

	@Override
	public IMassSpectrumFilterProcessingInfo applyFilter(List<IScanMSD> massSpectra, IProgressMonitor monitor) {

		IMassSpectrumFilterSettings massSpectrumFilterSettings = PreferenceSupplier.getMassSpectrumFilterSettings();
		return applyFilter(massSpectra, massSpectrumFilterSettings, monitor);
	}
}
