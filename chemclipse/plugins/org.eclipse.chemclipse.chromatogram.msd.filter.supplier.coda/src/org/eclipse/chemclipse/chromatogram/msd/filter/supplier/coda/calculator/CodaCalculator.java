/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator;

import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

// TODO JUnit
/**
 * This class implements the basic coda algorithm.
 * 
 * @author eselmeister
 */
public class CodaCalculator {

	private static final Logger logger = Logger.getLogger(CodaCalculator.class);

	/**
	 * Use it a singleton.
	 */
	private CodaCalculator() {
	}

	/**
	 * Calculates the mcq (mass chromatographic quality) value of the given mass
	 * fragment (ion).
	 * 
	 * @param extractedIonSignals
	 * @param windowSize
	 * @param ion
	 * @return float
	 */
	public static float getMCQValue(IExtractedIonSignals extractedIonSignals, WindowSize windowSize, int ion) {

		IExtractedIonSignal extractedIonSignal;
		int startScan = extractedIonSignals.getStartScan();
		int stopScan = extractedIonSignals.getStopScan();
		int width = stopScan - startScan + 1;
		double[] euclidianLengthValues = new double[width];
		double[] standardizedSmoothedValues = new double[width];
		float signal;
		/*
		 * Get the abundance from each scan of the given ion.
		 */
		for(int scan = startScan, counter = 0; scan <= stopScan; scan++, counter++) {
			try {
				extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
				signal = extractedIonSignal.getAbundance(ion);
			} catch(NoExtractedIonSignalStoredException e) {
				logger.warn(e);
				/*
				 * We do not really need this, but it is for better
				 * understanding.
				 */
				signal = 0.0f;
			}
			euclidianLengthValues[counter] = signal;
			standardizedSmoothedValues[counter] = signal;
		}
		/*
		 * Set to euclidian length.
		 */
		Calculations.scaleToEuclidianLength(euclidianLengthValues);
		/*
		 * Smooth and set to standardized length.
		 */
		Calculations.smooth(standardizedSmoothedValues, windowSize);
		Calculations.scaleToStandardizedLength(standardizedSmoothedValues);
		return calculateMCQValue(euclidianLengthValues, standardizedSmoothedValues, windowSize);
	}

	// --------------------------------------private methods
	/**
	 * Returns the calculated mcq value.
	 * 
	 * @param euclidianLengthValues
	 * @param standardizedSmoothedValues
	 * @param windowSize
	 * @return float
	 */
	private static float calculateMCQValue(double[] euclidianLengthValues, double[] standardizedSmoothedValues, WindowSize windowSize) {

		assert (windowSize != null) : "The window size must not be null.";
		float mcq = 0.0f;
		int lastScan = Calculations.getWindowReducedLength(euclidianLengthValues, windowSize.getSize());
		int scans = euclidianLengthValues.length;
		double sumSignals = 0.0f;
		/*
		 * Formulas see:
		 * "A noise and background reduction method for component detection in liquid chromatography mass spectrometry"
		 * , Windig, W. and Phalp, J. M. and Payne, A. W., 1996<br/> See
		 * formula: #9
		 */
		for(int i = 0; i < lastScan; i++) {
			sumSignals += euclidianLengthValues[i] * standardizedSmoothedValues[i];
		}
		mcq = (float)((1.0d / Math.sqrt((scans - windowSize.getSize()))) * sumSignals);
		/*
		 * Give a warning if something tries to run out of order.
		 */
		if(mcq < 0.0f || mcq > 1.0f) {
			logger.warn("There is something going wrong with the coda algorithm. The mcq value must be in between 0.0f and 1.0f.");
		}
		return mcq;
	}
	// --------------------------------------private methods
}
