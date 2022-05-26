/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.support;

import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

public class FilterSupport {

	public static final float NORMALIZATION_FACTOR = 1000.0f;

	/**
	 * Returns a combined mass spectrum using the chromatogram selection.
	 * May return null.
	 * 
	 * @param chromatogramSelection
	 * @param excludedIons
	 * @return {@link IScanMSD}
	 */
	public static IScanMSD getCombinedMassSpectrum(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons excludedIons, boolean useNormalize, CalculationType calculationType, boolean usePeaksInsteadOfScans) {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			return null;
		}
		//
		excludedIons = validateExcludedIons(excludedIons);
		/*
		 * Use the scan range from the start and stop scan.
		 */
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		int startScan = chromatogram.getScanNumber(startRetentionTime);
		int stopScan = chromatogram.getScanNumber(stopRetentionTime);
		CombinedMassSpectrumCalculator massSpectrumCalculator = new CombinedMassSpectrumCalculator();
		/*
		 * Merge all selected scans/peaks.<br/> All scan intensities will be added.
		 * Afterwards, they will be normalized.<br/> It would be also possible
		 * to add the intensity of each scan, normalize and add the next
		 * intensity.
		 */
		if(usePeaksInsteadOfScans) {
			for(IChromatogramPeakMSD peakMSD : chromatogram.getPeaks(startRetentionTime, stopRetentionTime)) {
				if(peakMSD.isActiveForAnalysis()) {
					IPeakModelMSD peakModelMSD = peakMSD.getPeakModel();
					massSpectrumCalculator.addIons(peakModelMSD.getPeakMassSpectrum().getIons(), excludedIons);
				}
			}
		} else {
			for(int scan = startScan; scan <= stopScan; scan++) {
				massSpectrumCalculator.addIons(chromatogram.getSupplierScan(scan).getIons(), excludedIons);
			}
		}
		/*
		 * Normalized or normal summed values.
		 */
		return getMassSpectrum(massSpectrumCalculator, useNormalize, calculationType);
	}

	/**
	 * Returns a normalized combined mass spectrum of the two given mass spectra.
	 * May return null.
	 * 
	 * @param massSpectrum1
	 * @param massSpectrum2
	 * @param excludedIons
	 * @return
	 */
	public static IScanMSD getCombinedMassSpectrum(IScanMSD massSpectrum1, IScanMSD massSpectrum2, IMarkedIons excludedIons, boolean useNormalize, CalculationType calculationType) {

		/*
		 * Both mass spectrum 1 and 2 shall be not null.
		 */
		if(massSpectrum1 == null && massSpectrum2 == null) {
			return null;
		}
		/*
		 * If one of the mass spectra is null, take only the valid one to calculate the normalized mass spectrum.
		 */
		excludedIons = validateExcludedIons(excludedIons);
		CombinedMassSpectrumCalculator massSpectrumCalculator = new CombinedMassSpectrumCalculator();
		//
		if(massSpectrum1 == null) {
			addIonsToCalculator(massSpectrum2, excludedIons, massSpectrumCalculator, useNormalize, calculationType);
		} else if(massSpectrum2 == null) {
			addIonsToCalculator(massSpectrum1, excludedIons, massSpectrumCalculator, useNormalize, calculationType);
		} else {
			addIonsToCalculator(massSpectrum1, excludedIons, massSpectrumCalculator, useNormalize, calculationType);
			addIonsToCalculator(massSpectrum2, excludedIons, massSpectrumCalculator, useNormalize, calculationType);
		}
		/*
		 * Normalized or normal summed values.
		 */
		return getMassSpectrum(massSpectrumCalculator, useNormalize, calculationType);
	}

	private static void addIonsToCalculator(IScanMSD massSpectrum, IMarkedIons excludedIons, CombinedMassSpectrumCalculator massSpectrumCalculator, boolean useNormalize, CalculationType calculationType) {

		IScanMSD massSpectrumCalculated = getCalculatedMassSpectrum(massSpectrum, excludedIons, useNormalize, calculationType);
		massSpectrumCalculator.addIons(massSpectrumCalculated.getIons(), excludedIons);
	}

	/**
	 * Returns a normalized or normal mass spectrum.
	 * May return null.
	 * 
	 * @param massSpectrum
	 * @param excludedIons
	 * @return {@link IScanMSD}
	 */
	public static IScanMSD getCalculatedMassSpectrum(IScanMSD massSpectrum, IMarkedIons excludedIons, boolean useNormalize, CalculationType calculationType) {

		if(massSpectrum == null) {
			return null;
		}
		excludedIons = validateExcludedIons(excludedIons);
		//
		CombinedMassSpectrumCalculator massSpectrumCalculator = new CombinedMassSpectrumCalculator();
		massSpectrumCalculator.addIons(massSpectrum.getIons(), excludedIons);
		return getMassSpectrum(massSpectrumCalculator, useNormalize, calculationType);
	}

	/**
	 * Validates the excluded ions.
	 * 
	 * @param excludedIons
	 * @return {@link IMarkedIons}
	 */
	private static IMarkedIons validateExcludedIons(IMarkedIons excludedIons) {

		/*
		 * Test excludedIons.<br/> If null create a new instance.
		 */
		if(excludedIons == null) {
			excludedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
		}
		return excludedIons;
	}

	/**
	 * Normalizes the mass spectrum values.
	 * 
	 * @param massSpectrumCalculator
	 * @return IMassSpectrum
	 */
	private static IScanMSD getMassSpectrum(CombinedMassSpectrumCalculator combinedMassSpectrumCalculator, boolean useNormalize, CalculationType calculationType) {

		ICombinedMassSpectrum noiseMassSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(calculationType);
		if(useNormalize) {
			noiseMassSpectrum.normalize(NORMALIZATION_FACTOR);
		}
		return noiseMassSpectrum;
	}
}
