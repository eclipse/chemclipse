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
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.model.statistics.RetentionIndex;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.ValueOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.DescriptionOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Samples;

public class PeakRetentionIndexExtractor extends AbstractClassifierDescriptionExtractor {

	public Samples extractPeakData(Map<IDataInputEntry, IPeaks<IPeak>> peaks, int retentionIndexWindow, DescriptionOption descriptionOption, ValueOption valueOption) {

		List<Sample> samplesList = new ArrayList<>();
		peaks.keySet().forEach(d -> samplesList.add(new Sample(d.getSampleName(), d.getGroupName())));
		Samples samples = new Samples(samplesList);
		//
		Map<String, IPeaks<?>> peakMap = new LinkedHashMap<>();
		peaks.forEach((dataInputEntry, peaksInput) -> peakMap.put(dataInputEntry.getSampleName(), peaksInput));
		//
		Map<String, SortedMap<Integer, IPeak>> extractPeaks = exctractPcaPeakMap(peakMap, retentionIndexWindow);
		List<Integer> extractedRetentionIndices = calculateCondensedRetentionIndices(extractPeaks);
		samples.getVariables().addAll(RetentionIndex.create(extractedRetentionIndices));
		//
		setExtractData(extractPeaks, samples, valueOption);
		setClassifierAndDescription(samples, descriptionOption);
		//
		return samples;
	}

	private List<Integer> calculateCondensedRetentionIndices(Map<String, SortedMap<Integer, IPeak>> extractPeaks) {

		Set<Integer> rententionIndices = new TreeSet<>();
		for(Map.Entry<String, SortedMap<Integer, IPeak>> extractPeak : extractPeaks.entrySet()) {
			for(Integer retentionIndex : extractPeak.getValue().keySet()) {
				rententionIndices.add(retentionIndex);
			}
		}
		return new ArrayList<>(rententionIndices);
	}

	private Map<String, SortedMap<Integer, IPeak>> exctractPcaPeakMap(Map<String, IPeaks<?>> peakMap, int retentionIndexWindow) {

		Map<String, TreeMap<Integer, IPeak>> pcaPeakRetentionIndex = new LinkedHashMap<>();
		Map<String, SortedMap<Integer, IPeak>> pcaPeakCondenseRetentionIndex = new LinkedHashMap<>();
		int totalCountPeak = 0;
		//
		for(Map.Entry<String, IPeaks<?>> peakEnry : peakMap.entrySet()) {
			String name = peakEnry.getKey();
			IPeaks<?> peaks = peakEnry.getValue();
			TreeMap<Integer, IPeak> peakTree = new TreeMap<>();
			for(IPeak peak : peaks.getPeaks()) {
				int retentionIndex = Math.round(peak.getPeakModel().getPeakMaximum().getRetentionIndex());
				if(retentionIndex > 0) {
					peakTree.put(retentionIndex, peak);
				}
			}
			totalCountPeak += peakTree.size();
			pcaPeakRetentionIndex.put(name, peakTree);
		}
		//
		while(totalCountPeak != 0) {
			SortedMap<Double, Collection<Integer>> weightRetentionIndex = setWeightRetentionIndices(pcaPeakRetentionIndex, retentionIndexWindow);
			Iterator<Map.Entry<Double, Collection<Integer>>> it = weightRetentionIndex.entrySet().iterator();
			TreeSet<Integer> condenseRetentionIndices = new TreeSet<>();
			while(it.hasNext() && (totalCountPeak != 0)) {
				Map.Entry<Double, Collection<Integer>> entry = it.next();
				Collection<Integer> retentionIndicesMax = entry.getValue();
				for(Integer retentionIndexMax : retentionIndicesMax) {
					Integer closestCondenseRetentionIndex = getClosestCondensedRetentionIndex(condenseRetentionIndices, retentionIndexMax);
					if(closestCondenseRetentionIndex != null && Math.abs(closestCondenseRetentionIndex - retentionIndexMax) < (retentionIndexWindow)) {
						continue;
					}
					condenseRetentionIndices.add(retentionIndexMax);
					for(Map.Entry<String, TreeMap<Integer, IPeak>> pcaPeakRetetntionIndex : pcaPeakRetentionIndex.entrySet()) {
						TreeMap<Integer, IPeak> peakTree = pcaPeakRetetntionIndex.getValue();
						String name = pcaPeakRetetntionIndex.getKey();
						IPeak closestPeak = getClosestPeak(peakTree, retentionIndexMax);
						if(closestPeak != null) {
							int peakRetentionIndex = Math.round(closestPeak.getPeakModel().getPeakMaximum().getRetentionIndex());
							int dist = Math.abs(retentionIndexMax - peakRetentionIndex);
							if(dist <= retentionIndexWindow) {
								totalCountPeak--;
								peakTree.remove(peakRetentionIndex);
								SortedMap<Integer, IPeak> extractPeaks = pcaPeakCondenseRetentionIndex.get(name);
								if(extractPeaks == null) {
									extractPeaks = new TreeMap<>();
									extractPeaks.put(retentionIndexMax, closestPeak);
									pcaPeakCondenseRetentionIndex.put(name, extractPeaks);
								} else {
									extractPeaks.put(retentionIndexMax, closestPeak);
								}
							}
						}
					}
				}
			}
		}
		//
		return pcaPeakCondenseRetentionIndex;
	}

	private Integer getClosestCondensedRetentionIndex(TreeSet<Integer> condensedRetentionIndices, int retentionIndex) {

		Integer peakRetentionIndexCeil = condensedRetentionIndices.ceiling(retentionIndex);
		Integer peakRetentionIndexFloor = condensedRetentionIndices.floor(retentionIndex);
		if(peakRetentionIndexCeil != null && peakRetentionIndexFloor != null) {
			if((peakRetentionIndexCeil - retentionIndex) < (retentionIndex - peakRetentionIndexFloor)) {
				return peakRetentionIndexCeil;
			} else {
				return peakRetentionIndexFloor;
			}
		} else if(peakRetentionIndexCeil != null) {
			return peakRetentionIndexCeil;
		} else if(peakRetentionIndexFloor != null) {
			return peakRetentionIndexFloor;
		}
		return null;
	}

	private IPeak getClosestPeak(TreeMap<Integer, IPeak> peakTree, int retentionIndex) {

		Map.Entry<Integer, IPeak> peakRetentionIndexCeil = peakTree.ceilingEntry(retentionIndex);
		Map.Entry<Integer, IPeak> peakRetentionIndexFloor = peakTree.floorEntry(retentionIndex);
		if(peakRetentionIndexCeil != null && peakRetentionIndexFloor != null) {
			if((peakRetentionIndexCeil.getKey() - retentionIndex) < (retentionIndex - peakRetentionIndexFloor.getKey())) {
				return peakRetentionIndexCeil.getValue();
			} else {
				return peakRetentionIndexFloor.getValue();
			}
		} else if(peakRetentionIndexCeil != null) {
			return peakRetentionIndexCeil.getValue();
		} else if(peakRetentionIndexFloor != null) {
			return peakRetentionIndexFloor.getValue();
		}
		return null;
	}

	private void setExtractData(Map<String, SortedMap<Integer, IPeak>> extractData, Samples samples, ValueOption valueOption) {

		List<IVariable> extractedRetentionIndices = samples.getVariables();
		boolean useQuantitationValue = ValueOption.CONCENTRATION.equals(valueOption);
		//
		for(Sample sample : samples.getSampleList()) {
			Iterator<IVariable> it = extractedRetentionIndices.iterator();
			SortedMap<Integer, IPeak> extractPeak = extractData.get(sample.getSampleName());
			if(extractPeak != null) {
				while(it.hasNext()) {
					IVariable variable = it.next();
					if(variable instanceof RetentionIndex retentionIndex) {
						IPeak peak = extractPeak.get(retentionIndex.getRetentionIndex());
						if(peak != null) {
							double value = 0.0d;
							if(useQuantitationValue) {
								/*
								 * Concentration
								 */
								List<IQuantitationEntry> quantitationEntries = peak.getQuantitationEntries();
								if(!quantitationEntries.isEmpty()) {
									value = quantitationEntries.get(0).getConcentration();
								}
							} else {
								/*
								 * Area
								 */
								value = peak.getIntegratedArea();
							}
							//
							PeakSampleData sampleData = new PeakSampleData(value, peak);
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
	}

	private SortedMap<Double, Collection<Integer>> setWeightRetentionIndices(Map<String, TreeMap<Integer, IPeak>> pcaPeakRetentionIndexMap, int retentionIndexWindow) {

		Map<Integer, Double> peakSum = new LinkedHashMap<>();
		int step = 1;
		if(retentionIndexWindow > 50) {
			step = retentionIndexWindow / 50;
			retentionIndexWindow = (retentionIndexWindow / step / 2) * step * 2;
		}
		for(TreeMap<Integer, IPeak> peaks : pcaPeakRetentionIndexMap.values()) {
			for(Integer retentionIndex : peaks.keySet()) {
				retentionIndex = (retentionIndex / step) * step;
				for(int i = retentionIndex - retentionIndexWindow / 2; i <= retentionIndex + retentionIndexWindow / 2; i += step) {
					int dis = Math.abs(i - retentionIndex);
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
		SortedMap<Double, Collection<Integer>> sortedWeightRetentionIndices = new TreeMap<>((o1, o2) -> -Double.compare(o1, o2));
		peakSum.forEach((k, v) -> {
			if(!(v < 1)) {
				Collection<Integer> retentionIndices = sortedWeightRetentionIndices.get(v);
				if(retentionIndices == null) {
					retentionIndices = new LinkedList<>();
					retentionIndices.add(k);
					sortedWeightRetentionIndices.put(v, retentionIndices);
				} else {
					retentionIndices.add(k);
				}
			}
		});
		return sortedWeightRetentionIndices;
	}
}