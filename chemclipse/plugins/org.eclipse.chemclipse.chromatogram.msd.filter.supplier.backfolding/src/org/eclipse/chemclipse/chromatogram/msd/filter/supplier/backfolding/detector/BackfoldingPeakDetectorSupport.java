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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.detector;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.IBackfoldingPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.support.BackfoldingDetectorSlope;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.support.BackfoldingDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.support.IBackfoldingDetectorSlope;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.support.IBackfoldingDetectorSlopes;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IDetectorSlope;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.RawPeak;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.miscellaneous.Evaluation;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

// TODO JUnit
public class BackfoldingPeakDetectorSupport {

	private static float NORMALIZATION_BASE = 100000.0f;
	private static int CONSECUTIVE_SCAN_STEPS = 3;
	private static WindowSize MOVING_AVERAGE_WINDOW = WindowSize.WIDTH_5;
	private static double threshold = 0.005d;

	/**
	 * This class has only static methods.
	 */
	private BackfoldingPeakDetectorSupport() {
	}

	/**
	 * Returns a {@link IBackfoldingDetectorSlopes} instance.
	 * 
	 * @param totalIonSignals
	 * @param peakDetectorSettings
	 * @return {@link IBackfoldingDetectorSlopes}
	 */
	public static IBackfoldingDetectorSlopes getBackfoldingSlopes(ITotalScanSignals totalIonSignals, IPeakDetectorMSDSettings peakDetectorSettings) {

		setDetectorSettings(peakDetectorSettings);
		return getBackfoldingSlopes(totalIonSignals);
	}

	/**
	 * Returns the raw peaks detected in the slopes instance.
	 * 
	 * @param slopes
	 * @param peakDetectorSettings
	 * @return List<IRawPeak>
	 */
	public static List<IRawPeak> getRawPeaks(IBackfoldingDetectorSlopes slopes, IPeakDetectorMSDSettings peakDetectorSettings) {

		setDetectorSettings(peakDetectorSettings);
		return getRawPeaks(slopes);
	}

	// ---------------------------------------------------private methods
	/**
	 * Sets the appropriate threshold value for this extension point.
	 */
	private static void setDetectorSettings(IPeakDetectorMSDSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IBackfoldingPeakDetectorSettings) {
			IBackfoldingPeakDetectorSettings backfoldingPeakDetectorSettings = (IBackfoldingPeakDetectorSettings)peakDetectorSettings;
			/*
			 * The threshold value depends on the actual calculation.<br/> The
			 * threshold defines the slope sensitivity.
			 */
			switch(backfoldingPeakDetectorSettings.getThreshold()) {
				case OFF:
					threshold = 0.0005d;
					break;
				case LOW:
					threshold = 0.005d;
					break;
				case MEDIUM:
					threshold = 0.05d;
					break;
				case HIGH:
					threshold = 0.5d;
					break;
				default:
					threshold = 0.005d;
					break;
			}
		}
	}

	/**
	 * Initializes the slope values.
	 * 
	 * @param chromatogramSelection
	 * @return {@link IFirstDerivativeDetectorSlopes}
	 */
	private static IBackfoldingDetectorSlopes getBackfoldingSlopes(ITotalScanSignals totalIonSignals) {

		TotalScanSignalsModifier.normalize(totalIonSignals, NORMALIZATION_BASE);
		/*
		 * Get the start and stop scan of the chromatogram selection.
		 */
		int startScan = totalIonSignals.getStartScan();
		int stopScan = totalIonSignals.getStopScan();
		ITotalScanSignal s1, s2;
		IPoint p1, p2;
		IBackfoldingDetectorSlope slope;
		IBackfoldingDetectorSlopes slopes = new BackfoldingDetectorSlopes(totalIonSignals);
		/*
		 * Fill the slope list.
		 */
		for(int scan = startScan; scan < stopScan; scan++) {
			s1 = totalIonSignals.getTotalScanSignal(scan);
			s2 = totalIonSignals.getNextTotalScanSignal(scan);
			if(s1 != null && s2 != null) {
				p1 = new Point(s1.getRetentionTime(), s1.getTotalSignal());
				p2 = new Point(s2.getRetentionTime(), s2.getTotalSignal());
				slope = new BackfoldingDetectorSlope(p1, p2, s1.getRetentionTime());
				slopes.add(slope);
			}
		}
		slopes.calculateMovingAverage(MOVING_AVERAGE_WINDOW);
		return slopes;
	}

	/**
	 * Marks the peaks with start, stop and max.
	 * 
	 * @param slopeList
	 */
	private static List<IRawPeak> getRawPeaks(IBackfoldingDetectorSlopes slopes) {

		int peakStart, peakStop, peakMaximum;
		int size = slopes.size();
		/*
		 * It should be also possible to detect peaks in a selected retention
		 * time area of the chromatogram.<br/> The value for scan in the for
		 * loop is by default 1 (detector array), but the slopes are storing
		 * start and end point of selection (scans).<br/> E.g. the selection is
		 * from scan 850 to scan 1000, then the loop starts at >
		 * slopes.getDetectorSlope(1 + 849);
		 */
		int scanOffset = slopes.getStartScan() - 1;
		IRawPeak rawPeak;
		List<IRawPeak> rawPeaks = new ArrayList<IRawPeak>();
		for(int i = 1; i <= size - CONSECUTIVE_SCAN_STEPS; i++) {
			/*
			 * Get the scan numbers without offset.<br/> Why? To not get out of
			 * borders of the slopes list.
			 */
			peakStart = detectPeakStart(slopes, i, scanOffset);
			peakMaximum = detectPeakMaximum(slopes, peakStart, scanOffset);
			peakStop = detectPeakStop(slopes, peakMaximum, scanOffset);
			/*
			 * Begin the detection of the next peak at the end of the actual
			 * peak.
			 */
			i = peakStop;
			/*
			 * Adjust the peak to their real positions (scan numbers) in the
			 * chromatogram.<br/> Keep in mind, the slopes list starts at
			 * position and not at the position of the scan.
			 */
			peakStart += scanOffset;
			peakMaximum += scanOffset;
			peakStop += scanOffset;
			rawPeak = new RawPeak(peakStart, peakMaximum, peakStop);
			if(isValidRawPeak(rawPeak)) {
				/*
				 * Add the retention time additionally.
				 */
				rawPeak.setRetentionTimeAtMaximum(slopes.getDetectorSlope(peakMaximum).getRetentionTime());
				rawPeaks.add(rawPeak);
			}
		}
		return rawPeaks;
	}

	/**
	 * Checks if the peak is a valid raw peak.<br/>
	 * For example if it contains not less than the needed amount of scans.
	 * 
	 * @param rawPeak
	 * @return boolean
	 */
	private static boolean isValidRawPeak(IRawPeak rawPeak) {

		boolean isValid = false;
		int width = rawPeak.getStopScan() - rawPeak.getStartScan() + 1;
		if(width >= IPeakModelMSD.MINIMUM_SCANS) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * Detects the peak start.
	 * 
	 * @param slope
	 * @param startScan
	 * @param scanOffset
	 * @return int
	 */
	private static int detectPeakStart(IBackfoldingDetectorSlopes slopes, int startScan, int scanOffset) {

		int size = slopes.size();
		int peakStart = size - 1;
		IDetectorSlope slope;
		double[] values = new double[CONSECUTIVE_SCAN_STEPS];
		exitloop:
		for(int scan = startScan; scan <= size - CONSECUTIVE_SCAN_STEPS; scan++) {
			slope = slopes.getDetectorSlope(scan + scanOffset);
			if(slope.getSlope() > threshold) {
				/*
				 * Get the actual and the next slope values.
				 */
				for(int j = 0; j < CONSECUTIVE_SCAN_STEPS; j++) {
					values[j] = slopes.getDetectorSlope(scan + j + scanOffset).getSlope();
				}
				if(Evaluation.valuesAreGreaterThanThreshold(values, threshold) && Evaluation.valuesAreIncreasing(values)) {
					peakStart = scan;
					break exitloop;
				}
			}
		}
		return peakStart;
	}

	/**
	 * Detects the peak maxima.<br/>
	 * The peak start and stops needs to be detected previously.
	 * 
	 * @param slope
	 * @param startScan
	 * @param scanOffset
	 * @return int
	 */
	private static int detectPeakMaximum(IBackfoldingDetectorSlopes slopes, int startScan, int scanOffset) {

		int size = slopes.size();
		IDetectorSlope slope;
		int peakMaximum = startScan;
		exitloop:
		for(int scan = startScan; scan <= size - CONSECUTIVE_SCAN_STEPS; scan++) {
			slope = slopes.getDetectorSlope(scan + scanOffset);
			if(slope.getSlope() < 0.0d) {
				peakMaximum = scan;
				break exitloop;
			}
		}
		return peakMaximum;
	}

	/**
	 * Detects the peak stops.
	 * 
	 * @param slope
	 * @param startScan
	 * @param scanOffset
	 * @return int
	 */
	private static int detectPeakStop(IBackfoldingDetectorSlopes slopes, int startScan, int scanOffset) {

		int size = slopes.size();
		int peakStop = size - CONSECUTIVE_SCAN_STEPS;
		IDetectorSlope slope;
		exitloop:
		for(int scan = startScan; scan <= size - CONSECUTIVE_SCAN_STEPS; scan++) {
			slope = slopes.getDetectorSlope(scan + scanOffset);
			if(slope.getSlope() > 0.0d) {
				peakStop = scan;
				break exitloop;
			}
		}
		return peakStop;
	}
	// ---------------------------------------------------private methods
}
