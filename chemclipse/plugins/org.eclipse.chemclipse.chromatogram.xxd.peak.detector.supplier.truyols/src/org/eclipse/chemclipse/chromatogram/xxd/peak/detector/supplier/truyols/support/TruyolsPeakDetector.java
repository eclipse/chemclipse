/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.RawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.model.DerivativeOption;
import org.eclipse.chemclipse.model.core.IPeakModel;

/**
 * Peak detection following the derivative strategies described in
 * G. Vivó-Truyols et al., J. Chromatogr. A 1096 (2005) 133-145 (Part I),
 * with optional 3rd-derivative deconvolution of coeluting components
 * (Part II, J. Chromatogr. A 1096 (2005) 146-155, Section 2.2.2).
 *
 * The algorithm smooths the input signal with Savitzky-Golay and inspects
 * the 1st, 2nd and 3rd derivative to locate peak apexes. Peak boundaries
 * (start / stop) are always derived from the 1st-derivative return to
 * baseline (Fig. 1b of Part I) so the full peak extent is covered; when
 * deconvolution is enabled, each peak region is additionally split into
 * one sub-peak per 3rd-derivative sign change (apex).
 */
public final class TruyolsPeakDetector {

	private TruyolsPeakDetector() {

	}

	/**
	 * Detects peaks in the given signal array and returns raw peaks whose
	 * scan indices are expressed in the chromatogram coordinate system
	 * (i.e. {@code scanOffset + localIndex}).
	 *
	 * When {@code deconvolve} is true, each detected peak region is
	 * additionally scanned for coeluting components via the 3rd derivative
	 * and one sub-peak per apex is emitted (valley-to-valley internally,
	 * baseline recovery at the cluster edges).
	 */
	public static List<IRawPeak> detect(double[] signal, int scanOffset, DerivativeOption option, int windowSize, int polynomialOrder, double noiseFactor) {

		if(signal == null || signal.length < Math.max(IPeakModel.MINIMUM_SCANS, windowSize)) {
			return new ArrayList<>();
		}
		int width = odd(windowSize);
		int order = Math.min(Math.max(polynomialOrder, 2), width - 1);
		double[] d1 = SavitzkyGolayProcessor.smooth(signal, 1, order, width);
		double[] d2 = SavitzkyGolayProcessor.smooth(signal, 2, order, width);
		double[] d3 = SavitzkyGolayProcessor.smooth(signal, 3, order, width);
		double d1Threshold = noiseFactor * estimateNoise(d1);
		boolean splitCoelutions = (option == DerivativeOption.THIRD);
		switch(option) {
			case FIRST:
				return detectByFirstDerivative(d1, d2, d3, d1Threshold, splitCoelutions, scanOffset);
			case SECOND:
			case THIRD:
			default:
				return detectBySecondDerivative(d1, d2, d3, d1Threshold, noiseFactor, splitCoelutions, scanOffset);
		}
	}

	/**
	 * 1st-derivative strategy: locate each peak region by scanning d1 for the
	 * classic rise-apex-fall pattern (|d1| above threshold on both sides of a
	 * downward zero-crossing). The region is then expanded via d1 baseline
	 * recovery and, when requested, split into sub-peaks via d3 sign changes.
	 */
	private static List<IRawPeak> detectByFirstDerivative(double[] d1, double[] d2, double[] d3, double d1Threshold, boolean splitCoelutions, int scanOffset) {

		List<IRawPeak> peaks = new ArrayList<>();
		int n = d1.length;
		int i = 0;
		while(i < n) {
			while(i < n && d1[i] <= d1Threshold) {
				i++;
			}
			if(i >= n) {
				break;
			}
			while(i < n && d1[i] > 0.0d) {
				i++;
			}
			if(i >= n) {
				break;
			}
			int apex = i;
			while(i < n && d1[i] < -d1Threshold) {
				i++;
			}
			int regionStart = findPeakStart(d1, apex, d1Threshold);
			int regionStop = findPeakStop(d1, apex, d1Threshold);
			addPeaksFromRegion(peaks, d1, d2, d3, regionStart, regionStop, d1Threshold, splitCoelutions, scanOffset);
		}
		return peaks;
	}

	/**
	 * 2nd-derivative strategy (Section 2.2 of Part I, default): find contiguous
	 * regions where d2 is below -threshold; the apex is the minimum of d2
	 * within each region. When {@code splitCoelutions} is true, the region is
	 * instead split into one sub-peak per 3rd-derivative apex.
	 */
	private static List<IRawPeak> detectBySecondDerivative(double[] d1, double[] d2, double[] d3, double d1Threshold, double noiseFactor, boolean splitCoelutions, int scanOffset) {

		double d2Threshold = noiseFactor * estimateNoise(d2);
		List<IRawPeak> peaks = new ArrayList<>();
		int n = d2.length;
		int i = 0;
		while(i < n) {
			while(i < n && d2[i] >= -d2Threshold) {
				i++;
			}
			if(i >= n) {
				break;
			}
			int regionStart = i;
			while(i < n && d2[i] < -d2Threshold) {
				i++;
			}
			int regionStop = i - 1;
			addPeaksFromRegion(peaks, d1, d2, d3, regionStart, regionStop, d1Threshold, splitCoelutions, scanOffset);
		}
		return peaks;
	}

	/**
	 * Shared peak-building step. Given a peak region expressed in signal
	 * coordinates, resolve one or more apexes (optionally via d3-sign-change
	 * deconvolution), expand the outermost edges via the 1st-derivative
	 * baseline recovery, and anchor internal boundaries at the d2 valleys
	 * (local maxima of d2) between consecutive apexes.
	 */
	private static void addPeaksFromRegion(List<IRawPeak> peaks, double[] d1, double[] d2, double[] d3, int regionStart, int regionStop, double d1Threshold, boolean splitCoelutions, int scanOffset) {

		if(regionStart >= regionStop) {
			return;
		}
		List<Integer> apexes = splitCoelutions ? findApexesByThirdDerivative(d3, regionStart, regionStop) : Collections.emptyList();
		if(apexes.isEmpty()) {
			apexes = List.of(findMinIndex(d2, regionStart, regionStop));
		}
		for(int k = 0; k < apexes.size(); k++) {
			int apex = apexes.get(k);
			int start = (k == 0) ? findPeakStart(d1, apex, d1Threshold) : valleyBetween(d2, apexes.get(k - 1), apex);
			int stop = (k == apexes.size() - 1) ? findPeakStop(d1, apex, d1Threshold) : valleyBetween(d2, apex, apexes.get(k + 1));
			addPeak(peaks, start, apex, stop, scanOffset);
		}
	}

	/**
	 * Walk left from the apex until the 1st derivative recovers to the
	 * baseline (|d1| drops back below the noise threshold). This includes
	 * the full rising shoulder of the peak, not just the inflection point.
	 */
	private static int findPeakStart(double[] d1, int apex, double d1Threshold) {

		int i = apex;
		while(i > 0 && d1[i] <= d1Threshold) {
			i--;
		}
		while(i > 0 && d1[i] > d1Threshold) {
			i--;
		}
		return i;
	}

	/**
	 * Walk right from the apex until the 1st derivative recovers to the
	 * baseline (|d1| drops back below the noise threshold).
	 */
	private static int findPeakStop(double[] d1, int apex, double d1Threshold) {

		int i = apex;
		int last = d1.length - 1;
		while(i < last && d1[i] >= -d1Threshold) {
			i++;
		}
		while(i < last && d1[i] < -d1Threshold) {
			i++;
		}
		return i;
	}

	/**
	 * Apexes correspond to downward zero-crossings of the 3rd derivative
	 * (d3 switching from positive to negative) — the same analytical condition
	 * that defines minima of d2.
	 */
	private static List<Integer> findApexesByThirdDerivative(double[] d3, int fromInclusive, int toInclusive) {

		List<Integer> apexes = new ArrayList<>();
		for(int i = fromInclusive; i < toInclusive; i++) {
			if(d3[i] > 0.0d && d3[i + 1] <= 0.0d) {
				apexes.add(i + 1);
			}
		}
		return apexes;
	}

	private static int findMinIndex(double[] values, int fromInclusive, int toInclusive) {

		int minIndex = fromInclusive;
		double min = values[fromInclusive];
		for(int i = fromInclusive + 1; i <= toInclusive; i++) {
			if(values[i] < min) {
				min = values[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

	private static int valleyBetween(double[] d2, int apexA, int apexB) {

		int maxIndex = apexA;
		double max = d2[apexA];
		for(int i = apexA + 1; i <= apexB; i++) {
			if(d2[i] > max) {
				max = d2[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	private static void addPeak(List<IRawPeak> peaks, int start, int apex, int stop, int scanOffset) {

		if(start < apex && apex < stop && (stop - start + 1) >= IPeakModel.MINIMUM_SCANS) {
			peaks.add(new RawPeak(start + scanOffset, apex + scanOffset, stop + scanOffset));
		}
	}

	/**
	 * Noise estimator from Section 2.2 of Part I: for each inner point compute
	 * h_i = |d[i] - (d[i-1] + d[i+1]) / 2|, and take the median.
	 */
	private static double estimateNoise(double[] d) {

		int n = d.length;
		if(n < 3) {
			return 0.0d;
		}
		double[] residuals = new double[n - 2];
		for(int i = 1; i < n - 1; i++) {
			residuals[i - 1] = Math.abs(d[i] - 0.5d * (d[i - 1] + d[i + 1]));
		}
		Arrays.sort(residuals);
		int mid = residuals.length / 2;
		return (residuals.length % 2 == 0) ? 0.5d * (residuals[mid - 1] + residuals[mid]) : residuals[mid];
	}

	private static int odd(int value) {

		return (value % 2 == 0) ? value + 1 : value;
	}
}