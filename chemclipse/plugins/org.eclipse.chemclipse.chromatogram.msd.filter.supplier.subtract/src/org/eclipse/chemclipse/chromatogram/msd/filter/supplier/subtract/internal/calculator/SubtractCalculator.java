/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.internal.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.ISubtractFilterSettingsMassSpectrum;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.PeakFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

public class SubtractCalculator {

	private static final Logger logger = Logger.getLogger(SubtractCalculator.class);
	private static final float NORMALIZATION_BASE = 100.0f;

	/**
	 * Subtracts the mass spectrum stored in the filter settings from each scan of the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param filterSettings
	 */
	@SuppressWarnings("rawtypes")
	public void subtractPeakMassSpectraFromChromatogramSelection(IChromatogramSelectionMSD chromatogramSelection, ChromatogramFilterSettings filterSettings) {

		/*
		 * Test if null.
		 */
		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null || filterSettings == null) {
			return;
		}
		/*
		 * The mass spectrum must be not null.
		 */
		IScanMSD massSpectrum = filterSettings.getSubtractMassSpectrum();
		if(massSpectrum == null) {
			logger.warn("The mass spectrum must be not null.");
			return;
		}
		/*
		 * Make a deep copy to prevent an unwanted modification
		 * of the original spectrum.
		 */
		boolean useNominalMasses = filterSettings.isUseNominalMasses();
		boolean useNormalize = filterSettings.isUseNormalize();
		Map<Double, Float> subtractMassSpectrumMap = getMassSpectrumMap(massSpectrum, useNominalMasses, useNormalize);
		/*
		 * Subtract the mass spectrum from each scan.
		 */
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		//
		for(int scanNumber = startScan; scanNumber <= stopScan; scanNumber++) {
			IScan scan = chromatogram.getScan(scanNumber);
			if(scan instanceof IVendorMassSpectrum) {
				/*
				 * Try to subtract the mass spectrum.
				 */
				IScanMSD targetMassSpectrum = (IVendorMassSpectrum)scan;
				adjustIntensityValues(targetMassSpectrum, subtractMassSpectrumMap, useNominalMasses, useNormalize);
			}
		}
		/*
		 * Delete the chromatogram and baseline integration results.
		 */
		chromatogram.getChromatogramIntegrationEntries().clear();
		chromatogram.getBackgroundIntegrationEntries().clear();
	}

	/**
	 * Subtracts the mass spectrum stored in the filter settings from each peak.
	 * 
	 * @param chromatogramSelection
	 * @param peakFilterSettings
	 */
	public void subtractPeakMassSpectra(List<IPeakMSD> peaks, PeakFilterSettings peakFilterSettings) {

		/*
		 * Test if null.
		 */
		if(peaks == null || peaks.size() == 0 || peakFilterSettings == null) {
			return;
		}
		/*
		 * The mass spectrum must be not null.
		 */
		IScanMSD massSpectrum = peakFilterSettings.getSubtractMassSpectrum();
		if(massSpectrum == null) {
			logger.warn("The mass spectrum must be not null.");
			return;
		}
		boolean useNominalMasses = peakFilterSettings.isUseNominalMasses();
		boolean useNormalize = peakFilterSettings.isNormalize();
		Map<Double, Float> subtractMassSpectrumMap = getMassSpectrumMap(massSpectrum, useNominalMasses, useNormalize);
		/*
		 * Subtract the mass spectrum from each peak.
		 */
		for(IPeakMSD peak : peaks) {
			IScanMSD targetMassSpectrum = peak.getExtractedMassSpectrum();
			adjustIntensityValues(targetMassSpectrum, subtractMassSpectrumMap, useNominalMasses, useNormalize);
			/*
			 * Delete the peak area. It needs to be recalculated.
			 */
			peak.getTargets().clear();
			peak.getIntegrationEntries().clear();
			peak.getQuantitationEntries().clear();
		}
	}

	public void subtractMassSpectra(List<IScanMSD> massSpectra, ISubtractFilterSettingsMassSpectrum subtractFilterSettings) {

		/*
		 * Test if null.
		 */
		if(massSpectra == null || massSpectra.size() == 0 || subtractFilterSettings == null) {
			return;
		}
		/*
		 * The mass spectrum must be not null.
		 */
		IScanMSD massSpectrum = subtractFilterSettings.getSubtractMassSpectrum();
		if(massSpectrum == null) {
			logger.warn("The mass spectrum must be not null.");
			return;
		}
		boolean useNominalMasses = subtractFilterSettings.isUseNominalMasses();
		boolean useNormalize = subtractFilterSettings.isNormalize();
		Map<Double, Float> subtractMassSpectrumMap = getMassSpectrumMap(massSpectrum, useNominalMasses, useNormalize);
		/*
		 * Subtract the mass spectrum from each peak.
		 */
		for(IScanMSD targetMassSpectrum : massSpectra) {
			adjustIntensityValues(targetMassSpectrum, subtractMassSpectrumMap, useNominalMasses, useNormalize);
			/*
			 * Delete the peak area. It needs to be recalculated.
			 */
			massSpectrum.getTargets().clear();
		}
	}

	/**
	 * Returns a map of the normalized mass spectrum.
	 * 
	 * @param massSpectrum
	 * @param useNominalMasses
	 * @return Map
	 */
	public Map<Double, Float> getMassSpectrumMap(IScanMSD massSpectrum, boolean useNominalMasses, boolean normalize) {

		Map<Double, Float> massSpectrumMap = new HashMap<Double, Float>();
		if(massSpectrum != null) {
			try {
				/*
				 * Normalize the mass spectrum?
				 */
				IScanMSD subtractMassSpectrum = massSpectrum.makeDeepCopy();
				if(normalize) {
					subtractMassSpectrum.normalize(NORMALIZATION_BASE);
				}
				//
				if(useNominalMasses) {
					/*
					 * Use only nominal m/z values.
					 */
					IExtractedIonSignal extractedIonSignal = subtractMassSpectrum.getExtractedIonSignal();
					int startIon = extractedIonSignal.getStartIon();
					int stopIon = extractedIonSignal.getStopIon();
					for(int ion = startIon; ion <= stopIon; ion++) {
						float abundance = extractedIonSignal.getAbundance(ion);
						if(abundance > 0) {
							massSpectrumMap.put((double)ion, abundance);
						}
					}
				} else {
					/*
					 * Keep the m/z values as they are.
					 */
					for(IIon ion : subtractMassSpectrum.getIons()) {
						massSpectrumMap.put(ion.getIon(), ion.getAbundance());
					}
				}
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		}
		return massSpectrumMap;
	}

	/**
	 * Uses the mass spectrum map to calculate the intensity that shall be subtracted
	 * from the given mass spectrum.
	 * 
	 * @param targetMassSpectrum
	 * @param subtractMassSpectrumMap
	 * @param useNominalMasses
	 */
	public void adjustIntensityValues(IScanMSD targetMassSpectrum, Map<Double, Float> subtractMassSpectrumMap, boolean useNominalMasses, boolean useNormalize) {

		/*
		 * Values must be not null.
		 */
		if(targetMassSpectrum == null || subtractMassSpectrumMap == null) {
			return;
		}
		//
		List<IIon> ionsToRemove = new ArrayList<IIon>();
		for(IIon ion : targetMassSpectrum.getIons()) {
			/*
			 * Get the nominal mass if needed.
			 */
			double mz;
			if(useNominalMasses) {
				mz = AbstractIon.getIon(ion.getIon());
			} else {
				mz = ion.getIon();
			}
			//
			Float subtractIntensity = subtractMassSpectrumMap.get(mz);
			if(subtractIntensity != null && subtractIntensity > 0) {
				/*
				 * Normalization:
				 * If the intensity is 100, than remove the signal.
				 * Otherwise, remove only a part of its abundance,
				 * depending on the subtract mass spectrum map.
				 * No Normalization:
				 * Remove the total intensity.
				 */
				try {
					float abundance = ion.getAbundance();
					float abundanceAdjusted;
					if(useNormalize) {
						abundanceAdjusted = abundance - ((subtractIntensity / NORMALIZATION_BASE) * abundance);
					} else {
						abundanceAdjusted = abundance - subtractIntensity;
					}
					/*
					 * Ion abundance must be not lower or equal than zero.
					 */
					if(abundanceAdjusted <= 0) {
						ionsToRemove.add(ion);
					} else {
						ion.setAbundance(abundanceAdjusted);
					}
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				}
			}
		}
		/*
		 * Remove selected ions.
		 * Their abundance has been calculated as zero or below.
		 */
		for(IIon ion : ionsToRemove) {
			targetMassSpectrum.removeIon(ion);
		}
	}
}
