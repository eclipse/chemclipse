/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
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

import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.CombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

public class FilterSupport {

	private static final Logger logger = Logger.getLogger(FilterSupport.class);
	public static final float NORMALIZATION_FACTOR = 1000.0f;

	/**
	 * Returns a combined mass spectrum using the chromatogram selection.
	 * May return null.
	 * 
	 * @param chromatogramSelection
	 * @param excludedIons
	 * @return {@link IScanMSD}
	 */
	public static IScanMSD getCombinedMassSpectrum(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons excludedIons, boolean useNormalize) {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			return null;
		}
		//
		excludedIons = validateExcludedIons(excludedIons);
		/*
		 * Use the scan range from the start and stop scan.
		 */
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		ICombinedMassSpectrumCalculator massSpectrumCalculator = new CombinedMassSpectrumCalculator();
		/*
		 * Merge all selected scans.<br/> All scan intensities will be added.
		 * Afterwards, they will be normalized.<br/> It would be also possible
		 * to add the intensity of each scan, normalize and add the next
		 * intensity.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			massSpectrumCalculator.addIons(chromatogram.getSupplierScan(scan).getIons(), excludedIons);
		}
		/*
		 * Normalized or normal summed values.
		 */
		return getMassSpectrum(massSpectrumCalculator, useNormalize);
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
	public static IScanMSD getCombinedMassSpectrum(IScanMSD massSpectrum1, IScanMSD massSpectrum2, IMarkedIons excludedIons, boolean useNormalize) {

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
		ICombinedMassSpectrumCalculator massSpectrumCalculator = new CombinedMassSpectrumCalculator();
		//
		if(massSpectrum1 == null) {
			addIonsToCalculator(massSpectrum2, excludedIons, massSpectrumCalculator, useNormalize);
		} else if(massSpectrum2 == null) {
			addIonsToCalculator(massSpectrum1, excludedIons, massSpectrumCalculator, useNormalize);
		} else {
			addIonsToCalculator(massSpectrum1, excludedIons, massSpectrumCalculator, useNormalize);
			addIonsToCalculator(massSpectrum2, excludedIons, massSpectrumCalculator, useNormalize);
		}
		/*
		 * Normalized or normal summed values.
		 */
		return getMassSpectrum(massSpectrumCalculator, useNormalize);
	}

	private static void addIonsToCalculator(IScanMSD massSpectrum, IMarkedIons excludedIons, ICombinedMassSpectrumCalculator massSpectrumCalculator, boolean useNormalize) {

		IScanMSD massSpectrumCalculated = getCalculatedMassSpectrum(massSpectrum, excludedIons, useNormalize);
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
	public static IScanMSD getCalculatedMassSpectrum(IScanMSD massSpectrum, IMarkedIons excludedIons, boolean useNormalize) {

		if(massSpectrum == null) {
			return null;
		}
		excludedIons = validateExcludedIons(excludedIons);
		//
		ICombinedMassSpectrumCalculator massSpectrumCalculator = new CombinedMassSpectrumCalculator();
		massSpectrumCalculator.addIons(massSpectrum.getIons(), excludedIons);
		return getMassSpectrum(massSpectrumCalculator, useNormalize);
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
			excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.EXCLUDE);
		}
		return excludedIons;
	}

	/**
	 * Normalizes the mass spectrum values.
	 * 
	 * @param massSpectrumCalculator
	 * @return IMassSpectrum
	 */
	private static IScanMSD getMassSpectrum(ICombinedMassSpectrumCalculator massSpectrumCalculator, boolean useNormalize) {

		if(useNormalize) {
			massSpectrumCalculator.normalize(NORMALIZATION_FACTOR);
		}
		Map<Integer, Double> ions = massSpectrumCalculator.getValues();
		return getMassSpectrum(ions);
	}

	/**
	 * Returns a mass spectrum using the ion/abundance list.
	 * 
	 * @param ions
	 * @return {@link IScanMSD}
	 */
	private static IScanMSD getMassSpectrum(Map<Integer, Double> ions) {

		/*
		 * Set start and stop retention time, retention index.
		 */
		IScanMSD combinedMassSpectrum = new CombinedMassSpectrum();
		/*
		 * Add all calculated combined ion values to the combined mass
		 * spectrum.
		 */
		float abundance;
		for(Integer ion : ions.keySet()) {
			/*
			 * Check the abundance.
			 */
			abundance = ions.get(ion).floatValue();
			if(abundance > IIon.ZERO_INTENSITY) {
				try {
					IIon combinedIon = new Ion(ion, abundance);
					combinedMassSpectrum.addIon(combinedIon);
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
		}
		return combinedMassSpectrum;
	}
}
