/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.AbstractMassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.processing.IMassSpectrumFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.filter.processing.MassSpectrumFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.IMassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.MassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.IXPassMassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class LowPassMassSpectrumFilter extends AbstractMassSpectrumFilter {

	private static final String DESCRIPTION = "Low Pass Mass Spectrum Filter";

	@Override
	public IMassSpectrumFilterProcessingInfo applyFilter(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings massSpectrumFilterSettings, IProgressMonitor monitor) {

		IMassSpectrumFilterProcessingInfo processingInfo = new MassSpectrumFilterProcessingInfo();
		processingInfo.addMessages(validate(massSpectra, massSpectrumFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Apply the filter.
		 */
		if(massSpectrumFilterSettings instanceof IXPassMassSpectrumFilterSettings) {
			//
			IXPassMassSpectrumFilterSettings xpassMassSpectrumFilterSettings = (IXPassMassSpectrumFilterSettings)massSpectrumFilterSettings;
			int numberLowest = xpassMassSpectrumFilterSettings.getNumberLowest();
			//
			for(IScanMSD massSpectrum : massSpectra) {
				List<IIon> ions = massSpectrum.getIons();
				Collections.sort(ions, new IonAbundanceComparator(SortOrder.ASC));
				List<IIon> ionsToRemove = new ArrayList<IIon>();
				int counter = 0;
				for(IIon ion : ions) {
					if(counter >= numberLowest) {
						ionsToRemove.add(ion);
					}
					counter++;
				}
				//
				ions.removeAll(ionsToRemove);
			}
			//
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The mass spectrum has been optimized successfully."));
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The filter settings instance is not a type of: " + IXPassMassSpectrumFilterSettings.class);
		}
		//
		IMassSpectrumFilterResult massSpectrumFilterResult = new MassSpectrumFilterResult(ResultStatus.OK, "The low pass filter has been applied successfully.");
		processingInfo.setMassSpectrumFilterResult(massSpectrumFilterResult);
		return processingInfo;
	}

	@Override
	public IMassSpectrumFilterProcessingInfo applyFilter(IScanMSD massSpectrum, IMassSpectrumFilterSettings massSpectrumFilterSettings, IProgressMonitor monitor) {

		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		massSpectra.add(massSpectrum);
		return applyFilter(massSpectra, massSpectrumFilterSettings, monitor);
	}

	@Override
	public IMassSpectrumFilterProcessingInfo applyFilter(IScanMSD massSpectrum, IProgressMonitor monitor) {

		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		massSpectra.add(massSpectrum);
		IMassSpectrumFilterSettings massSpectrumFilterSettings = PreferenceSupplier.getMassSpectrumFilterSettings();
		return applyFilter(massSpectra, massSpectrumFilterSettings, monitor);
	}

	@Override
	public IMassSpectrumFilterProcessingInfo applyFilter(List<IScanMSD> massSpectra, IProgressMonitor monitor) {

		IMassSpectrumFilterSettings massSpectrumFilterSettings = PreferenceSupplier.getMassSpectrumFilterSettings();
		return applyFilter(massSpectra, massSpectrumFilterSettings, monitor);
	}
}
