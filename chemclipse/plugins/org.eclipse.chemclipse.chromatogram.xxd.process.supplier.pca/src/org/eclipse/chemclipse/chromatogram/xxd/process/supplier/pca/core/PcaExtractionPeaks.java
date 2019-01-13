/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.statistics.RetentionTime;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionPeaks implements IDataExtraction {

	private List<IDataInputEntry> dataInputEntriesAll;
	private int extractionType;
	private int retentionTimeWindow;

	public PcaExtractionPeaks(List<IDataInputEntry> dataInputEntriesAll, int retentionTimeWindow, int extractionType) {
		this.retentionTimeWindow = retentionTimeWindow;
		this.dataInputEntriesAll = dataInputEntriesAll;
		this.extractionType = extractionType;
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

	private Map<String, SortedMap<Integer, IPeak>> exctractPcaPeakMap(Map<String, IPeaks> peakMap, int retentionTimeWindow) {

		Map<String, TreeMap<Integer, IPeak>> pcaPeakRetentionTime = new HashMap<>();
		Map<String, SortedMap<Integer, IPeak>> pcaPeakCondenseRetentionTime = new HashMap<>();
		int totalCountPeak = 0;
		for(Map.Entry<String, IPeaks> peakEnry : peakMap.entrySet()) {
			String name = peakEnry.getKey();
			IPeaks peaks = peakEnry.getValue();
			TreeMap<Integer, IPeak> peakTree = new TreeMap<>();
			for(IPeak peak : peaks.getPeaks()) {
				int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
				peakTree.put(retentionTime, peak);
			}
			totalCountPeak += peakTree.size();
			pcaPeakRetentionTime.put(name, peakTree);
		}
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
		return pcaPeakCondenseRetentionTime;
	}

	private void extractPeakData(Samples samples, int retentionTimeWindow, IProgressMonitor monitor) {

		/*
		 * Extract data
		 */
		monitor.subTask("Extract peak values");
		Map<String, IPeaks> peakMap = extractPeaks(dataInputEntriesAll, monitor);
		monitor.subTask("Prepare peak values");
		Map<String, SortedMap<Integer, IPeak>> extractPeaks = exctractPcaPeakMap(peakMap, retentionTimeWindow);
		List<Integer> extractedRetentionTimes = calculateCondensedRetentionTimes(extractPeaks);
		samples.getVariables().addAll(RetentionTime.create(extractedRetentionTimes));
		setExtractData(extractPeaks, samples);
	}

	private Map<String, IPeaks> extractPeaks(List<IDataInputEntry> peakinpitFiles, IProgressMonitor monitor) {

		Map<String, IPeaks> peakMap = new HashMap<String, IPeaks>();
		for(IDataInputEntry peakFile : peakinpitFiles) {
			try {
				/*
				 * Try to catch exceptions if wrong files have been selected.
				 */
				IProcessingInfo processingInfo = PeakConverterMSD.convert(new File(peakFile.getInputFile()), monitor);
				IPeaks peaks = processingInfo.getProcessingResult(IPeaks.class);
				String name = peakFile.getName();
				peakMap.put(name, peaks);
			} catch(Exception e) {
			}
		}
		return peakMap;
	}

	private Integer getClosestCondensedRetentionTime(TreeSet<Integer> condensedRetentionTimes, int retentionTime) {

		Integer peakRetentionTimeCeil = condensedRetentionTimes.ceiling(retentionTime);
		Integer peakRetentionTimeFlour = condensedRetentionTimes.floor(retentionTime);
		if(peakRetentionTimeCeil != null && peakRetentionTimeFlour != null) {
			if((peakRetentionTimeCeil - retentionTime) < (retentionTime - peakRetentionTimeFlour)) {
				return peakRetentionTimeCeil;
			} else {
				return peakRetentionTimeFlour;
			}
		} else if(peakRetentionTimeCeil != null) {
			return peakRetentionTimeCeil;
		} else if(peakRetentionTimeFlour != null) {
			return peakRetentionTimeFlour;
		}
		return null;
	}

	private IPeak getClosestPeak(TreeMap<Integer, IPeak> peakTree, int retentionTime) {

		Map.Entry<Integer, IPeak> peakRetentionTimeCeil = peakTree.ceilingEntry(retentionTime);
		Map.Entry<Integer, IPeak> peakRetentionTimeFlour = peakTree.floorEntry(retentionTime);
		if(peakRetentionTimeCeil != null && peakRetentionTimeFlour != null) {
			if((peakRetentionTimeCeil.getKey() - retentionTime) < (retentionTime - peakRetentionTimeFlour.getKey())) {
				return peakRetentionTimeCeil.getValue();
			} else {
				return peakRetentionTimeFlour.getValue();
			}
		} else if(peakRetentionTimeCeil != null) {
			return peakRetentionTimeCeil.getValue();
		} else if(peakRetentionTimeFlour != null) {
			return peakRetentionTimeFlour.getValue();
		}
		return null;
	}

	@Override
	public Samples process(IProgressMonitor monitor) {

		/*
		 * Initialize PCA Results
		 */
		Samples samples = new Samples(dataInputEntriesAll);
		if(!(extractionType == EXTRACT_PEAK || extractionType == EXTRACT_PEAK_CUMULATION)) {
			extractionType = EXTRACT_PEAK;
		}
		/*
		 * Extract data
		 */
		switch(extractionType) {
			case EXTRACT_PEAK:
				extractPeakData(samples, retentionTimeWindow, monitor);
				break;
		}
		setRetentionTimeDescription(samples);
		return samples;
	}

	private void setExtractData(Map<String, SortedMap<Integer, IPeak>> extractData, Samples samples) {

		List<RetentionTime> extractedRetentionTimes = samples.getVariables();
		for(Sample sample : samples.getSampleList()) {
			Iterator<RetentionTime> it = extractedRetentionTimes.iterator();
			SortedMap<Integer, IPeak> extractPeak = extractData.get(sample.getName());
			while(it.hasNext()) {
				int retentionTime = it.next().getRetentionTime();
				IPeak peak = extractPeak.get(retentionTime);
				if(peak != null) {
					PeakSampleData sampleData = new PeakSampleData(peak.getIntegratedArea());
					sampleData.setPeak(peak);
					sample.getSampleData().add(sampleData);
				} else {
					PeakSampleData sampleData = new PeakSampleData();
					sample.getSampleData().add(sampleData);
				}
			}
		}
	}

	private SortedMap<Double, Collection<Integer>> setWeightRetentionTimes(Map<String, TreeMap<Integer, IPeak>> pcaPeakRetetntionTimeMap, int retentionTimeWindow) {

		Map<Integer, Double> peakSum = new HashMap<>();
		int step = 1;
		if(retentionTimeWindow > 400) {
			step = retentionTimeWindow / 400;
			retentionTimeWindow = (retentionTimeWindow / step / 2) * step * 2;
		}
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
		return sortedWeightRetentionTimes;
	}
}
