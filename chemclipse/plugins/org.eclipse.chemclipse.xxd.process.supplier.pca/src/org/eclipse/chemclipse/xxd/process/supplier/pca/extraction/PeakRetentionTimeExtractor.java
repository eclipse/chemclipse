/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.extraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.model.statistics.RetentionTime;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.DescriptionOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Samples;

public class PeakRetentionTimeExtractor extends AbstractClassifierDescriptionExtractor {

	public Samples extractPeakData(Map<IDataInputEntry, IPeaks<?>> peaks, int retentionTimeWindow, DescriptionOption descriptionOption) {

		List<Sample> samplesList = new ArrayList<>();
		peaks.keySet().forEach(d -> samplesList.add(new Sample(d.getSampleName(), d.getGroupName())));
		Samples samples = new Samples(samplesList);
		//
		Map<String, IPeaks<?>> peakMap = new LinkedHashMap<>();
		peaks.forEach((dataInputEntry, peaksInput) -> {
			peakMap.put(dataInputEntry.getSampleName(), peaksInput);
		});
		//
		Map<String, SortedMap<Integer, IPeak>> extractPeaks = exctractPcaPeakMap(peakMap, retentionTimeWindow);
		List<Integer> extractedRetentionTimes = calculateCondensedRetentionTimes(extractPeaks);
		samples.getVariables().addAll(RetentionTime.create(extractedRetentionTimes));
		//
		setExtractData(extractPeaks, samples);
		setClassifierAndDescription(samples, descriptionOption);
		//
		return samples;
	}

	private List<Integer> calculateCondensedRetentionTimes(Map<String, SortedMap<Integer, IPeak>> extractPeaks) {

		Set<Integer> rententionTimes = new TreeSet<>();
		for(Map.Entry<String, SortedMap<Integer, IPeak>> extractPeak : extractPeaks.entrySet()) {
			for(Integer retentionTime : extractPeak.getValue().keySet()) {
				rententionTimes.add(retentionTime);
			}
		}
		return new ArrayList<>(rententionTimes);
	}

	private Map<String, SortedMap<Integer, IPeak>> exctractPcaPeakMap(Map<String, IPeaks<?>> peakMap, int retentionTimeWindow) {

		Map<String, TreeMap<Integer, IPeak>> pcaPeakRetentionTime = new LinkedHashMap<>();
		Map<String, SortedMap<Integer, IPeak>> pcaPeakCondenseRetentionTime = new LinkedHashMap<>();
		int totalCountPeak = 0;
		//
		for(Map.Entry<String, IPeaks<?>> peakEnry : peakMap.entrySet()) {
			String name = peakEnry.getKey();
			IPeaks<?> peaks = peakEnry.getValue();
			TreeMap<Integer, IPeak> peakTree = new TreeMap<>();
			for(IPeak peak : peaks.getPeaks()) {
				int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
				peakTree.put(retentionTime, peak);
			}
			totalCountPeak += peakTree.size();
			pcaPeakRetentionTime.put(name, peakTree);
		}
		//
		while(totalCountPeak != 0) {
			SortedMap<Double, Collection<Integer>> weightRetentionTime = setWeightRetentionTimes(pcaPeakRetentionTime, retentionTimeWindow);
			Iterator<Map.Entry<Double, Collection<Integer>>> it = weightRetentionTime.entrySet().iterator();
			TreeSet<Integer> condenseRetentionTimes = new TreeSet<>();
			while(it.hasNext() && (totalCountPeak != 0)) {
				Map.Entry<Double, Collection<Integer>> entry = it.next();
				Collection<Integer> retentionTimesMax = entry.getValue();
				for(Integer retentionTimeMax : retentionTimesMax) {
					Integer closestCondenseRetentionTime = getClosestCondensedRetentionTime(condenseRetentionTimes, retentionTimeMax);
					if(closestCondenseRetentionTime != null) {
						if(Math.abs(closestCondenseRetentionTime - retentionTimeMax) < (retentionTimeWindow)) {
							continue;
						}
					}
					condenseRetentionTimes.add(retentionTimeMax);
					for(Map.Entry<String, TreeMap<Integer, IPeak>> pcaPeakRetetntionTime : pcaPeakRetentionTime.entrySet()) {
						TreeMap<Integer, IPeak> peakTree = pcaPeakRetetntionTime.getValue();
						String name = pcaPeakRetetntionTime.getKey();
						IPeak closestPeak = getClosestPeak(peakTree, retentionTimeMax);
						if(closestPeak != null) {
							int peakRetentionTime = closestPeak.getPeakModel().getRetentionTimeAtPeakMaximum();
							int dist = Math.abs(retentionTimeMax - peakRetentionTime);
							if(dist <= retentionTimeWindow) {
								totalCountPeak--;
								peakTree.remove(peakRetentionTime);
								SortedMap<Integer, IPeak> extractPeaks = pcaPeakCondenseRetentionTime.get(name);
								if(extractPeaks == null) {
									extractPeaks = new TreeMap<>();
									extractPeaks.put(retentionTimeMax, closestPeak);
									pcaPeakCondenseRetentionTime.put(name, extractPeaks);
								} else {
									extractPeaks.put(retentionTimeMax, closestPeak);
								}
							}
						}
					}
				}
			}
		}
		//
		return pcaPeakCondenseRetentionTime;
	}

	private Integer getClosestCondensedRetentionTime(TreeSet<Integer> condensedRetentionTimes, int retentionTime) {

		Integer peakRetentionTimeCeil = condensedRetentionTimes.ceiling(retentionTime);
		Integer peakRetentionTimeFloor = condensedRetentionTimes.floor(retentionTime);
		if(peakRetentionTimeCeil != null && peakRetentionTimeFloor != null) {
			if((peakRetentionTimeCeil - retentionTime) < (retentionTime - peakRetentionTimeFloor)) {
				return peakRetentionTimeCeil;
			} else {
				return peakRetentionTimeFloor;
			}
		} else if(peakRetentionTimeCeil != null) {
			return peakRetentionTimeCeil;
		} else if(peakRetentionTimeFloor != null) {
			return peakRetentionTimeFloor;
		}
		return null;
	}

	private IPeak getClosestPeak(TreeMap<Integer, IPeak> peakTree, int retentionTime) {

		Map.Entry<Integer, IPeak> peakRetentionTimeCeil = peakTree.ceilingEntry(retentionTime);
		Map.Entry<Integer, IPeak> peakRetentionTimeFloor = peakTree.floorEntry(retentionTime);
		if(peakRetentionTimeCeil != null && peakRetentionTimeFloor != null) {
			if((peakRetentionTimeCeil.getKey() - retentionTime) < (retentionTime - peakRetentionTimeFloor.getKey())) {
				return peakRetentionTimeCeil.getValue();
			} else {
				return peakRetentionTimeFloor.getValue();
			}
		} else if(peakRetentionTimeCeil != null) {
			return peakRetentionTimeCeil.getValue();
		} else if(peakRetentionTimeFloor != null) {
			return peakRetentionTimeFloor.getValue();
		}
		return null;
	}

	private void setExtractData(Map<String, SortedMap<Integer, IPeak>> extractData, Samples samples) {

		List<IVariable> extractedRetentionTimes = samples.getVariables();
		//
		for(Sample sample : samples.getSampleList()) {
			Iterator<IVariable> it = extractedRetentionTimes.iterator();
			SortedMap<Integer, IPeak> extractPeak = extractData.get(sample.getSampleName());
			while(it.hasNext()) {
				IVariable variable = it.next();
				if(variable instanceof RetentionTime retentionTime) {
					IPeak peak = extractPeak.get(retentionTime.getRetentionTime());
					if(peak != null) {
						PeakSampleData sampleData = new PeakSampleData(peak.getIntegratedArea(), peak);
						sampleData.setPeak(peak);
						sample.getSampleData().add(sampleData);
					} else {
						PeakSampleData sampleData = new PeakSampleData();
						sample.getSampleData().add(sampleData);
					}
				}
			}
		}
	}

	private SortedMap<Double, Collection<Integer>> setWeightRetentionTimes(Map<String, TreeMap<Integer, IPeak>> pcaPeakRetetntionTimeMap, int retentionTimeWindow) {

		Map<Integer, Double> peakSum = new LinkedHashMap<>();
		int step = 1;
		//
		if(retentionTimeWindow > 400) {
			step = retentionTimeWindow / 400;
			retentionTimeWindow = (retentionTimeWindow / step / 2) * step * 2;
		}
		//
		for(TreeMap<Integer, IPeak> peaks : pcaPeakRetetntionTimeMap.values()) {
			for(Integer retentionTime : peaks.keySet()) {
				retentionTime = (retentionTime / step) * step;
				for(int i = retentionTime - retentionTimeWindow / 2; i <= retentionTime + retentionTimeWindow / 2; i += step) {
					int dis = Math.abs(i - retentionTime);
					double value = 1.0 / ((dis + 1) * (dis + 1));
					Double actualValue = peakSum.get(i);
					if(actualValue == null) {
						peakSum.put(i, value);
					} else {
						peakSum.put(i, value + actualValue);
					}
				}
			}
		}
		//
		SortedMap<Double, Collection<Integer>> sortedWeightRetentionTimes = new TreeMap<>((o1, o2) -> -Double.compare(o1, o2));
		peakSum.forEach((k, v) -> {
			if(!(v < 1)) {
				Collection<Integer> retentionTimes = sortedWeightRetentionTimes.get(v);
				if(retentionTimes == null) {
					retentionTimes = new LinkedList<>();
					retentionTimes.add(k);
					sortedWeightRetentionTimes.put(v, retentionTimes);
				} else {
					retentionTimes.add(k);
				}
			}
		});
		//
		return sortedWeightRetentionTimes;
	}
}