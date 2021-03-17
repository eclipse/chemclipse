/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.implementation.Peak;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.PeakModel;
import org.eclipse.chemclipse.model.implementation.Scan;
import org.eclipse.chemclipse.model.support.BackgroundAbundanceRange;

public class PeakMerger {

	public static IPeak mergePeaks(List<? extends IPeak> peaks, boolean mergeIdentificationTargets) {

		IPeak peak = null;
		if(peaks.size() >= 2) {
			/*
			 * Merge and create a new peak.
			 */
			float totalSignal = mergeTotalSignal(peaks);
			List<IIdentificationTarget> identificationTargets = mergeIdentificationTargets(peaks);
			IPeakIntensityValues peakIntensityValues = mergeIntensityValues(peaks);
			BackgroundAbundanceRange backgroundAbundanceRange = mergeBackgroundAbundanceRange(peaks);
			IScan peakMaximum = new Scan(totalSignal);
			float startBackgroundAbundance = backgroundAbundanceRange.getStartBackgroundAbundance();
			float stopBackgroundAbundance = backgroundAbundanceRange.getStopBackgroundAbundance();
			IPeakModel peakModel = new PeakModel(peakMaximum, peakIntensityValues, startBackgroundAbundance, stopBackgroundAbundance);
			peak = new Peak(peakModel);
			/*
			 * Targets
			 */
			if(mergeIdentificationTargets) {
				peak.getTargets().addAll(identificationTargets);
			}
		}
		//
		return peak;
	}

	public static IPeakIntensityValues mergeIntensityValues(List<? extends IPeak> peaks) {

		List<? extends IPeak> peaksSorted = createSortedPeakList(peaks);
		TreeMap<Integer, Float> retentionTimeIntensityMap = new TreeMap<>();
		/*
		 * Extract and merge the intensity profile of each peak.
		 */
		for(IPeak peak : peaksSorted) {
			IPeakModel peakModel = peak.getPeakModel();
			List<Integer> retentionTimes = peakModel.getRetentionTimes();
			for(int retentionTime : retentionTimes) {
				Float abundanceMerged = retentionTimeIntensityMap.get(retentionTime);
				Float abundancePeak = peakModel.getPeakAbundance(retentionTime);
				float abundance = (abundanceMerged != null) ? Math.max(abundanceMerged, abundancePeak) : abundancePeak;
				retentionTimeIntensityMap.put(retentionTime, abundance);
			}
		}
		//
		return create(retentionTimeIntensityMap);
	}

	public static BackgroundAbundanceRange mergeBackgroundAbundanceRange(List<? extends IPeak> peaks) {

		List<? extends IPeak> peaksSorted = createSortedPeakList(peaks);
		float startBackgroundAbundance = Float.MAX_VALUE;
		float stopBackgroundAbundance = Float.MIN_VALUE;
		/*
		 * Extract and merge the background profile of each peak.
		 */
		for(IPeak peak : peaksSorted) {
			IPeakModel peakModel = peak.getPeakModel();
			startBackgroundAbundance = Math.min(startBackgroundAbundance, peakModel.getBackgroundAbundance(peakModel.getStartRetentionTime()));
			stopBackgroundAbundance = Math.max(stopBackgroundAbundance, peakModel.getBackgroundAbundance(peakModel.getStopRetentionTime()));
		}
		/*
		 * Post validations
		 */
		if(startBackgroundAbundance == Float.MAX_VALUE) {
			startBackgroundAbundance = 0.0f;
		}
		//
		if(stopBackgroundAbundance == Float.MIN_VALUE) {
			stopBackgroundAbundance = 0.0f;
		}
		//
		return new BackgroundAbundanceRange(startBackgroundAbundance, stopBackgroundAbundance);
	}

	public static List<IIdentificationTarget> mergeIdentificationTargets(List<? extends IPeak> peaks) {

		List<IIdentificationTarget> identificationTargets = new ArrayList<>();
		//
		for(IPeak peak : peaks) {
			identificationTargets.addAll(peak.getTargets());
		}
		//
		return identificationTargets;
	}

	public static float mergeTotalSignal(List<? extends IPeak> peaks) {

		float totalSignal = Float.MIN_VALUE;
		//
		for(IPeak peak : peaks) {
			/*
			 * Peaks to merge
			 */
			IPeakModel peakModel = peak.getPeakModel();
			IScan scan = peakModel.getPeakMaximum();
			totalSignal = Math.max(totalSignal, scan.getTotalSignal());
		}
		//
		return totalSignal;
	}

	private static List<? extends IPeak> createSortedPeakList(List<? extends IPeak> peaks) {

		List<? extends IPeak> peaksSorted = new ArrayList<>(peaks);
		Collections.sort(peaksSorted, (p1, p2) -> Integer.compare(p1.getPeakModel().getStartRetentionTime(), p2.getPeakModel().getStartRetentionTime()));
		return peaksSorted;
	}

	/**
	 * The key is the retention time in milliseconds.
	 * The value is the peak abundance.
	 * 
	 * @param retentionTimeIntensityMap
	 * @return IPeakIntensityValues
	 */
	public static IPeakIntensityValues create(TreeMap<Integer, Float> retentionTimeIntensityMap) {

		float maxIntensity = retentionTimeIntensityMap.values().stream().max(Float::compare).get();
		IPeakIntensityValues peakIntensityValues = new PeakIntensityValues(maxIntensity);
		for(Map.Entry<Integer, Float> entry : retentionTimeIntensityMap.entrySet()) {
			peakIntensityValues.addIntensityValue(entry.getKey(), entry.getValue());
		}
		peakIntensityValues.normalize();
		//
		return peakIntensityValues;
	}
}
