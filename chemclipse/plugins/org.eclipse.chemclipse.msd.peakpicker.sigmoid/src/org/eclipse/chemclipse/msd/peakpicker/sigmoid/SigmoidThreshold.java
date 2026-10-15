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
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.peakpicker.sigmoid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.chemclipse.msd.peakpicker.sigmoid.settings.SigmoidThresholdPeakPickerFilterSettings;

/**
 * Picks local maxima above an m/z dependent threshold as described for MicrobeMS in:
 * <p>
 * P. Lasch. MicrobeMS - A MATLAB Toolbox for Microbial Identification Based on Mass Spectrometry.
 * bioRxiv 2026. https://doi.org/10.64898/2026.05.08.723807
 * <p>
 * and at https://wiki-ms.microbe-ms.com/index.php?title=Peak_Detection
 * <p>
 * T(x) = baseline(x) + I_m * s / (1 + exp(k * (x - x_c))) + c * noise(x), with k = exp(-l)
 * <p>
 * I_m is the mean height of the 13 most intense local maxima in the m/z range, ignoring the top 3.
 * The decreasing sigmoid accounts for the higher sensitivity of MALDI-TOF MS at low m/z.
 */
public final class SigmoidThreshold {

	private static final int IGNORED_MOST_INTENSE = 3;
	private static final int AVERAGED_MOST_INTENSE = 13;

	public record PickedPeak(int index, double threshold) {
	}

	/**
	 * @param scalingFactor
	 *            the requested one, or the one adjusted to obtain the number of peaks
	 */
	public record Result(List<PickedPeak> peaks, double scalingFactor) {
	}

	/**
	 * @param criticalScaling
	 *            the scaling factor at which the threshold reaches the peak
	 */
	private record Candidate(int index, double criticalScaling, double sigmoid) {
	}

	private SigmoidThreshold() {

	}

	/**
	 * All arrays are indexed by data point, sorted by ascending m/z.
	 */
	public static Result pick(double[] mz, double[] intensity, double[] baseline, double[] noise, SigmoidThresholdPeakPickerFilterSettings settings) {

		int[] maxima = localMaxima(mz, intensity, settings.getStartMZ(), settings.getStopMZ(), settings.getResolution());
		double meanIntensity = meanIntensity(Arrays.stream(maxima).mapToDouble(i -> intensity[i] - baseline[i]).toArray());
		if(meanIntensity <= 0) {
			return new Result(List.of(), settings.getScalingFactor());
		}

		List<Candidate> candidates = new ArrayList<>();
		for(int i : maxima) {
			double margin = intensity[i] - baseline[i] - settings.getNoiseFactor() * noise[i];
			if(margin > 0) {
				double sigmoid = meanIntensity * sigmoid(mz[i], settings.getCenterMZ(), settings.getSlope());
				candidates.add(new Candidate(i, margin / sigmoid, sigmoid));
			}
		}

		double scalingFactor = settings.getScalingFactor();
		int numberOfPeaks = settings.getNumberOfPeaks();
		if(numberOfPeaks > 0) {
			/*
			 * Scaling the threshold until the desired number of peaks remains
			 * is the same as keeping the peaks that survive the highest scaling.
			 */
			candidates.sort(Comparator.comparingDouble(Candidate::criticalScaling).reversed());
			if(candidates.size() > numberOfPeaks) {
				scalingFactor = candidates.get(numberOfPeaks).criticalScaling();
				candidates = new ArrayList<>(candidates.subList(0, numberOfPeaks));
			} else {
				scalingFactor = 0;
			}
			candidates.sort(Comparator.comparingInt(Candidate::index));
		} else {
			double s = scalingFactor;
			candidates.removeIf(candidate -> candidate.criticalScaling() <= s);
		}

		List<PickedPeak> peaks = new ArrayList<>();
		for(Candidate candidate : candidates) {
			int i = candidate.index();
			double sigmoid = candidate.sigmoid() == 0 ? 0 : scalingFactor * candidate.sigmoid();
			peaks.add(new PickedPeak(i, baseline[i] + sigmoid + settings.getNoiseFactor() * noise[i]));
		}
		return new Result(peaks, scalingFactor);
	}

	/**
	 * Generalized logistic function decreasing from 1 to 0 with its inflection point at the center.
	 */
	static double sigmoid(double x, double center, double slope) {

		return 1.0d / (1.0d + Math.exp(Math.exp(-slope) * (x - center)));
	}

	/**
	 * Mean of the 13 largest values, ignoring the 3 largest ones.
	 * Falls back to all values if there are too few of them.
	 */
	static double meanIntensity(double[] heights) {

		if(heights.length == 0) {
			return 0;
		}
		double[] sorted = heights.clone();
		Arrays.sort(sorted);
		int to = sorted.length;
		if(to > IGNORED_MOST_INTENSE) {
			to -= IGNORED_MOST_INTENSE;
		}
		int from = Math.max(0, sorted.length - AVERAGED_MOST_INTENSE);
		return Arrays.stream(sorted, from, to).average().orElse(0);
	}

	/**
	 * A local maximum exceeds the points to its left and is not exceeded by those to its right
	 * within m/z &plusmn; m/z / resolution, so a plateau yields a single maximum.
	 * The window widens with m/z like the peaks of a time-of-flight analyzer.
	 * Points at the spectrum boundaries are no apex.
	 */
	static int[] localMaxima(double[] mz, double[] intensity, double startMZ, double stopMZ, int resolution) {

		int n = intensity.length;
		List<Integer> maxima = new ArrayList<>();
		for(int i = 1; i < n - 1; i++) {
			if(mz[i] < startMZ || mz[i] > stopMZ) {
				continue;
			}
			if(isLocalMaximum(mz, intensity, i, mz[i] / resolution)) {
				maxima.add(i);
			}
		}
		return maxima.stream().mapToInt(Integer::intValue).toArray();
	}

	private static boolean isLocalMaximum(double[] mz, double[] intensity, int i, double tolerance) {

		double apex = intensity[i];
		for(int j = i - 1; j >= 0 && mz[i] - mz[j] <= tolerance; j--) {
			if(intensity[j] >= apex) {
				return false;
			}
		}
		for(int j = i + 1; j < intensity.length && mz[j] - mz[i] <= tolerance; j++) {
			if(intensity[j] > apex) {
				return false;
			}
		}
		return true;
	}
}
