/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.internal.core.support;

import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.exceptions.FilterException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.model.support.SegmentValidator;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.implementation.CombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.support.CombinedMassSpectrumCalculator;
import org.eclipse.chemclipse.msd.model.support.ICombinedMassSpectrumCalculator;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.core.runtime.IProgressMonitor;

public class CalculatorSupport {

	private static final Logger logger = Logger.getLogger(CalculatorSupport.class);
	private static final float NORMALIZATION_FACTOR = 1000.0f;
	private SegmentValidator segmentValidator;

	public CalculatorSupport() {
		segmentValidator = new SegmentValidator();
	}

	/**
	 * Calculates whether the segment will be accepted or not.
	 * 
	 * @param values
	 * @param mean
	 * @return boolean
	 */
	public boolean acceptSegment(double[] values, double mean) {

		return segmentValidator.acceptSegment(values, mean);
	}

	/**
	 * Calculate a noise mass spectrum from the given segment.
	 * 
	 * @return
	 */
	public ICombinedMassSpectrumCalculator getCombinedMassSpectrumCalculator(IAnalysisSegment analysisSegment, IExtractedIonSignals extractedIonSignals) {

		IExtractedIonSignal extractedIonSignal;
		ICombinedMassSpectrumCalculator combinedMassSpectrumCalculator = new CombinedMassSpectrumCalculator();
		for(int scan = analysisSegment.getStartScan(); scan <= analysisSegment.getStopScan(); scan++) {
			try {
				extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
				/*
				 * Add the abundance for each ion in the signal to summed signal.
				 */
				for(int ion = extractedIonSignal.getStartIon(); ion <= extractedIonSignal.getStopIon(); ion++) {
					combinedMassSpectrumCalculator.addIon(ion, extractedIonSignal.getAbundance(ion));
				}
			} catch(NoExtractedIonSignalStoredException e) {
				logger.warn(e);
			}
		}
		return combinedMassSpectrumCalculator;
	}

	/*
	 * Returns a combined mass spectrum.
	 */
	public ICombinedMassSpectrum getNoiseMassSpectrum(ICombinedMassSpectrumCalculator combinedMassSpectrumCalculator, IMarkedIons ionsToPreserve, IProgressMonitor monitor) {

		/*
		 * Remove the ions to preserve.
		 */
		combinedMassSpectrumCalculator.removeIons(ionsToPreserve);
		/*
		 * Normalize the mass spectrum.
		 */
		combinedMassSpectrumCalculator.normalize(NORMALIZATION_FACTOR);
		/*
		 * Add all calculated combined ion values to the combined mass
		 * spectrum.
		 */
		float abundance;
		ICombinedMassSpectrum noiseMassSpectrum = new CombinedMassSpectrum();
		IIon noiseIon;
		Map<Integer, Double> ions = combinedMassSpectrumCalculator.getValues();
		for(Integer ion : ions.keySet()) {
			/*
			 * Check the abundance.
			 */
			abundance = ions.get(ion).floatValue();
			if(abundance > IIon.ZERO_INTENSITY) {
				try {
					noiseIon = new Ion(ion, abundance);
					noiseMassSpectrum.addIon(noiseIon);
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
		}
		return noiseMassSpectrum;
	}

	/**
	 * Checks the scan range.
	 * 
	 * @param scanRange
	 * @throws FilterException
	 */
	public void checkScanRange(ScanRange scanRange, int segmentWidth) throws FilterException {

		/*
		 * Check the scan range.
		 */
		if(scanRange == null || scanRange.getWidth() <= segmentWidth) {
			throw new FilterException("The selected scan width is lower than the segment width.");
		}
	}
}
