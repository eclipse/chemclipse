/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.List;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.core.support.PeakMerger;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.support.BackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.msd.model.support.CombinedMassSpectrumCalculator;

public class PeakMergerMSD extends PeakMerger {

	public static IPeakMSD mergePeaks(List<? extends IPeakMSD> peaks, CalculationType calculationType, boolean mergeIdentificationTargets) {

		IPeakMSD peakMSD = null;
		if(peaks.size() >= 2) {
			/*
			 * Merge and create a new peak.
			 */
			float totalSignal = mergeTotalSignal(peaks);
			List<IIdentificationTarget> identificationTargets = mergeIdentificationTargets(peaks);
			IPeakIntensityValues peakIntensityValues = mergeIntensityValues(peaks);
			BackgroundAbundanceRange backgroundAbundanceRange = mergeBackgroundAbundanceRange(peaks);
			IScanMSD massSpectrum = mergeMassSpectra(peaks, calculationType);
			massSpectrum.adjustTotalSignal(totalSignal);
			IPeakMassSpectrum peakMaximum = new PeakMassSpectrum(massSpectrum);
			float startBackgroundAbundance = backgroundAbundanceRange.getStartBackgroundAbundance();
			float stopBackgroundAbundance = backgroundAbundanceRange.getStopBackgroundAbundance();
			IPeakModelMSD peakModelMSD = new PeakModelMSD(peakMaximum, peakIntensityValues, startBackgroundAbundance, stopBackgroundAbundance);
			peakMSD = new PeakMSD(peakModelMSD);
			/*
			 * Targets
			 */
			if(mergeIdentificationTargets) {
				peakMSD.getTargets().addAll(identificationTargets);
			}
		}
		//
		return peakMSD;
	}

	private static IScanMSD mergeMassSpectra(List<? extends IPeakMSD> peaks, CalculationType calculationType) {

		CombinedMassSpectrumCalculator massSpectrumCalculator = new CombinedMassSpectrumCalculator();
		IMarkedIons excludedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
		//
		for(IPeakMSD peak : peaks) {
			/*
			 * Merge the mass spectra
			 */
			IPeakModelMSD peakModel = peak.getPeakModel();
			IScanMSD scanMSD = peakModel.getPeakMassSpectrum();
			massSpectrumCalculator.addIons(scanMSD.getIons(), excludedIons);
		}
		//
		return massSpectrumCalculator.createMassSpectrum(calculationType);
	}
}