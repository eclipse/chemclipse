/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AnalysisSupportException;
import org.eclipse.chemclipse.model.support.AnalysisSupport;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.support.IIonUniquenessValues;
import org.eclipse.chemclipse.msd.model.core.support.IonUniquenessValues;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;

/**
 * This class offers several static methods to modify an {@link IExtractedIonSignals} instance.
 * 
 * @author eselmeister
 */
public class ExtractedIonSignalsModifier {

	private static final Logger logger = Logger.getLogger(ExtractedIonSignalsModifier.class);
	private static final int SELECTION_PARTS = 10;
	private static final int MIN_SELECTION_WIDTH = 10;

	/**
	 * Use the class as a singleton.
	 */
	private ExtractedIonSignalsModifier() {
	}

	/**
	 * Calculates the ion uniqueness.<br/>
	 * See
	 * "An integrated method for spectrum extraction and compound identification from gas chromatography/mass spectrometry data "
	 * , S.E. Stein, 1999
	 * 
	 * @throws AnalysisSupportException
	 */
	public static IIonUniquenessValues calculateIonUniqueness(IExtractedIonSignals extractedIonSignals) throws AnalysisSupportException {

		validateChromatogram(extractedIonSignals);
		IScanRange scanRange = calculateScanRange(extractedIonSignals);
		int segmentWidth = scanRange.getWidth();
		AnalysisSupport analysisSupport = new AnalysisSupport(scanRange, segmentWidth);
		List<IAnalysisSegment> analysisSegments = analysisSupport.getAnalysisSegments();
		int startIon = extractedIonSignals.getStartIon();
		int stopIon = extractedIonSignals.getStopIon();
		/*
		 * Calculate the uniqueness value for each ion.
		 */
		float uniquenessValue;
		IIonUniquenessValues ionUniquenessValues = new IonUniquenessValues();
		for(IAnalysisSegment analysisSegment : analysisSegments) {
			for(int ion = startIon; ion <= stopIon; ion++) {
				uniquenessValue = calculateIonUniquenessValue(analysisSegment, ion, extractedIonSignals);
				ionUniquenessValues.add(ion, uniquenessValue);
			}
		}
		return ionUniquenessValues;
	}

	/**
	 * This method adjusts threshold transitions in the given {@link IExtractedIonSignals} instance. See
	 * "An integrated method for spectrum extraction and compound identification from gas chromatography/mass spectrometry data "
	 * , S.E. Stein, 1999
	 * 
	 * @param extractedIonSignals
	 * @throws AnalysisSupportException
	 * @throws NoExtractedIonSignalStoredException
	 */
	public static void adjustThresholdTransitions(IExtractedIonSignals extractedIonSignals) throws AnalysisSupportException {

		validateChromatogram(extractedIonSignals);
		IChromatogramMSD chromatogram = extractedIonSignals.getChromatogram();
		IScanRange scanRange = calculateScanRange(extractedIonSignals);
		int segmentWidth = calculateSegmentWidth(scanRange);
		AnalysisSupport analysisSupport = new AnalysisSupport(scanRange, segmentWidth);
		List<IAnalysisSegment> analysisSegments = analysisSupport.getAnalysisSegments();
		/*
		 * At = preSetThresholdAbundanceValue
		 */
		float preSetThresholdAbundanceValue = chromatogram.getMinIonAbundance();
		int startIon = extractedIonSignals.getStartIon();
		int stopIon = extractedIonSignals.getStopIon();
		/*
		 * Correct in each scan the ion ion intensity values.<br/> The
		 * correction will be calculated inside of segment of 13 scans.
		 */
		for(IAnalysisSegment analysisSegment : analysisSegments) {
			for(int ion = startIon; ion <= stopIon; ion++) {
				adjustIonIntensities(analysisSegment, ion, preSetThresholdAbundanceValue, extractedIonSignals);
			}
		}
	}

	// --------------------------------------------------------private methods
	private static int calculateSegmentWidth(IScanRange scanRange) {

		/*
		 * Divide the selection into 10 parts if possible.
		 */
		int selectionWidth = scanRange.getWidth();
		int segmentWidth = selectionWidth / SELECTION_PARTS;
		/*
		 * Test first if the segmentWidth is lower than the minimum selection
		 * width. If not correct it. Test afterwards if the segment width is
		 * higher than the selection width. If yes correct it.
		 */
		if(segmentWidth < MIN_SELECTION_WIDTH) {
			segmentWidth = MIN_SELECTION_WIDTH;
		}
		if(segmentWidth > selectionWidth) {
			segmentWidth = selectionWidth;
		}
		return segmentWidth;
	}

	private static IScanRange calculateScanRange(IExtractedIonSignals extractedIonSignals) throws AnalysisSupportException {

		/*
		 * Initialize the segments. The scanRange will be divided into parts of
		 * 10 scans (SEGMENT_WIDTH).
		 */
		int startScan = extractedIonSignals.getStartScan();
		int stopScan = extractedIonSignals.getStopScan();
		IScanRange scanRange = new ScanRange(startScan, stopScan);
		return scanRange;
	}

	private static void validateChromatogram(IExtractedIonSignals extractedIonSignals) throws AnalysisSupportException {

		validateExtractedIonSignals(extractedIonSignals);
		IChromatogramMSD chromatogram = extractedIonSignals.getChromatogram();
		if(chromatogram == null) {
			throw new AnalysisSupportException("There is no chromatogram stored in the extracted ion signals instance.");
		}
	}

	private static void validateExtractedIonSignals(IExtractedIonSignals extractedIonSignals) throws AnalysisSupportException {

		/*
		 * Do nothing in such a case.
		 */
		if(extractedIonSignals == null) {
			throw new AnalysisSupportException("The extracted ion signals object is null.");
		}
	}

	private static float calculateIonUniquenessValue(IAnalysisSegment analysisSegment, int ion, IExtractedIonSignals extractedIonSignals) {

		int startScan = analysisSegment.getStartScan();
		int stopScan = analysisSegment.getStopScan();
		int width = analysisSegment.getSegmentWidth();
		float result = 0.0f;
		// Use float to get a higher precision.
		int transitions = calculateTransitions(startScan, stopScan, extractedIonSignals, ion);
		float valuesNotZero = width - transitions;
		if(width > 0) {
			result = valuesNotZero / width;
		}
		return result;
	}

	/**
	 * Adjusts the ion values.
	 * 
	 * @param analysisSegment
	 * @param ion
	 * @param preSetThresholdAbundanceValue
	 * @param extractedIonSignals
	 * @throws NoExtractedIonSignalStoredException
	 */
	private static void adjustIonIntensities(IAnalysisSegment analysisSegment, int ion, float preSetThresholdAbundanceValue, IExtractedIonSignals extractedIonSignals) {

		int startScan = analysisSegment.getStartScan();
		int stopScan = analysisSegment.getStopScan();
		// Use double to get a higher precision.
		double transitions = calculateTransitions(startScan, stopScan, extractedIonSignals, ion);
		int width = analysisSegment.getSegmentWidth();
		if(width > 0) {
			double transitionFraction = transitions / width;
			/*
			 * If there was no transition, do not change anything.
			 */
			if(transitionFraction > 0) {
				float adjustedNonZeroAbundanceValue = (float)(preSetThresholdAbundanceValue * Math.sqrt(transitionFraction));
				setNewAbundanceValue(analysisSegment, extractedIonSignals, ion, adjustedNonZeroAbundanceValue);
			}
		}
	}

	/**
	 * Sets all zero abundance values to the new abundance.
	 * 
	 * @param analysisSegment
	 * @param excludedIons
	 * @param ion
	 * @param adjustedNonZeroAbundanceValue
	 */
	private static void setNewAbundanceValue(IAnalysisSegment analysisSegment, IExtractedIonSignals extractedIonSignals, int ion, float adjustedNonZeroAbundanceValue) {

		int startScan = analysisSegment.getStartScan();
		int stopScan = analysisSegment.getStopScan();
		IExtractedIonSignal extractedIonSignal;
		float actualSignal = 0.0f;
		/*
		 * Take a look at those abundance values which are zero and correct them
		 * to adjustedNonZeroAbundanceValue.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			try {
				extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
				actualSignal = extractedIonSignal.getAbundance(ion);
				if(actualSignal == 0.0f) {
					extractedIonSignal.setAbundance(ion, adjustedNonZeroAbundanceValue, true);
				}
			} catch(NoExtractedIonSignalStoredException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * Calculates the amount of transitions where the abundance of a certain
	 * ion falls tos zero.
	 * 
	 * @param startScan
	 * @param stopScan
	 * @param extractedIonSignals
	 * @param ion
	 * @return int
	 * @throws NoExtractedIonSignalStoredException
	 */
	private static int calculateTransitions(int startScan, int stopScan, IExtractedIonSignals extractedIonSignals, int ion) {

		IExtractedIonSignal extractedIonSignal;
		float actualSignal = 0.0f;
		int transitions = 0;
		/*
		 * There could occur a small failure if the first check throws an
		 * exception. It should not, but if there is perhaps something other
		 * wrong.
		 */
		boolean isZero = true;
		try {
			isZero = isZero(extractedIonSignals, startScan, ion);
		} catch(NoExtractedIonSignalStoredException e) {
			logger.warn(e);
		}
		/*
		 * Increase the start scan, because the first scan (ion abundance) has
		 * been evaluated to determine if the first scan is zero or not.
		 */
		for(int scan = ++startScan; scan <= stopScan; scan++) {
			try {
				extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
				actualSignal = extractedIonSignal.getAbundance(ion);
				/*
				 * Count the transitions. Switch between zero and non zero
				 * values. It is important to check if a value drops to zero and
				 * not the other way round. Why? If the last scan in the given
				 * range is zero, it would not be counted as for the next scan
				 * must be a positive value, but there is no next scan. As for
				 * that count the transitions in !isZero.
				 */
				if(!isZero) {
					if(actualSignal == 0.0f) {
						transitions++;
						isZero = true;
					}
				}
				if(actualSignal > 0.0f) {
					isZero = false;
				}
			} catch(NoExtractedIonSignalStoredException e) {
				logger.warn(e);
			}
		}
		return transitions;
	}

	/**
	 * Determines whether the signal is zero or not.
	 * 
	 * @return boolean
	 * @throws NoExtractedIonSignalStoredException
	 */
	private static boolean isZero(IExtractedIonSignals extractedIonSignals, int scan, int ion) throws NoExtractedIonSignalStoredException {

		IExtractedIonSignal extractedIonSignal;
		boolean isZero = false;
		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		if(extractedIonSignal.getAbundance(ion) == 0.0f) {
			isZero = true;
		}
		return isZero;
	}
	// --------------------------------------------------------private methods
}
