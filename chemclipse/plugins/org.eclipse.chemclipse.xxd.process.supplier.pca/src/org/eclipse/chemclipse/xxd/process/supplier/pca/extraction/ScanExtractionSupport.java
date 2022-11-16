/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.extraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.statistics.RetentionTime;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Samples;
import org.eclipse.core.runtime.IProgressMonitor;

public class ScanExtractionSupport {

	public enum ExtractionType {
		CLOSEST_SCAN, LINEAR_INTERPOLATION_SCAN;
	}

	private int beginRetentionTimeMax;
	private int endRetentionTimeMin;
	private ExtractionType extractionType;
	private int maximalNumberScans;
	private int retentionTimeWindow;
	private boolean useDefaultProperties;

	public ScanExtractionSupport(int retentionTimeWindow, int maximalNumberScans, ExtractionType extractionType, boolean useDefaultProperties) {

		this.retentionTimeWindow = retentionTimeWindow;
		this.extractionType = extractionType;
		this.useDefaultProperties = useDefaultProperties;
		this.maximalNumberScans = maximalNumberScans;
	}

	public Samples process(Map<IDataInputEntry, Collection<IScan>> dataInput, IProgressMonitor monitor) {

		/*
		 * Initialize PCA Results
		 */
		List<Sample> samplesList = new ArrayList<>();
		dataInput.keySet().forEach(d -> samplesList.add(new Sample(d.getSampleName(), d.getGroupName())));
		Samples samples = new Samples(samplesList);
		/*
		 * Extract data
		 */
		Map<String, NavigableMap<Integer, Float>> extractScans = extractScans(dataInput, monitor);
		//
		boolean similarChromatogram = true;
		if(useDefaultProperties) {
			Collection<NavigableMap<Integer, Float>> dataSet = extractScans.values();
			boolean isFirst = true;
			Set<Integer> retentionTimes = new HashSet<>();
			int retentionTimeSize = retentionTimes.size();
			for(NavigableMap<Integer, Float> data : dataSet) {
				if(isFirst) {
					retentionTimes.addAll(data.keySet());
					isFirst = false;
				} else {
					if(data.size() != retentionTimes.size()) {
						similarChromatogram = false;
						break;
					}
					long dataSiza = data.keySet().stream().filter(time -> retentionTimes.contains(time)).count();
					if(dataSiza != retentionTimeSize) {
						similarChromatogram = false;
						break;
					}
				}
			}
		}
		int size = ((endRetentionTimeMin - beginRetentionTimeMax) / retentionTimeWindow);
		if(size > maximalNumberScans) {
			retentionTimeWindow = (endRetentionTimeMin - beginRetentionTimeMax) / maximalNumberScans;
			similarChromatogram = false;
		}
		if(similarChromatogram && useDefaultProperties) {
			useDefaultProperties(samples, extractScans);
		} else {
			switch(extractionType) {
				case CLOSEST_SCAN:
					setClosestScan(samples, extractScans);
					break;
				case LINEAR_INTERPOLATION_SCAN:
					interpolation(samples, extractScans, new LinearInterpolator());
					break;
			}
		}
		return samples;
	}

	private Map<String, NavigableMap<Integer, Float>> extractScans(Map<IDataInputEntry, Collection<IScan>> inputs, IProgressMonitor monitor) {

		beginRetentionTimeMax = 0;
		endRetentionTimeMin = Integer.MAX_VALUE;
		Map<String, NavigableMap<Integer, Float>> scanMap = new HashMap<>();
		for(Entry<IDataInputEntry, Collection<IScan>> input : inputs.entrySet()) {
			TreeMap<Integer, Float> extractScans = new TreeMap<>();
			Collection<IScan> scans = input.getValue();
			/*
			 * extract scans
			 */
			for(IScan scan : scans) {
				float currentSignal = scan.getTotalSignal();
				int currentTime = scan.getRetentionTime();
				extractScans.put(currentTime, currentSignal);
			}
			int firstRetentionTime = extractScans.firstKey();
			int lastRetetnionTime = extractScans.lastKey();
			if(firstRetentionTime > beginRetentionTimeMax) {
				beginRetentionTimeMax = firstRetentionTime;
			}
			if(lastRetetnionTime < endRetentionTimeMin) {
				endRetentionTimeMin = lastRetetnionTime;
			}
			scanMap.put(input.getKey().getSampleName(), extractScans);
		}
		return scanMap;
	}

	private Float getClosestScans(NavigableMap<Integer, Float> peakTree, int retentionTime) {

		Map.Entry<Integer, Float> peakRetentionTimeCeil = peakTree.ceilingEntry(retentionTime);
		Map.Entry<Integer, Float> peakRetentionTimeFloor = peakTree.floorEntry(retentionTime);
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

	private void interpolation(Samples samples, Map<String, NavigableMap<Integer, Float>> extractScans, UnivariateInterpolator interpolator) {

		for(Sample sample : samples.getSampleList()) {
			List<PeakSampleData> data = sample.getSampleData();
			NavigableMap<Integer, Float> scans = extractScans.get(sample.getSampleName());
			double[] retentionTime = new double[scans.size()];
			double[] scanValues = new double[scans.size()];
			int j = 0;
			Iterator<Entry<Integer, Float>> it = scans.entrySet().iterator();
			while(it.hasNext()) {
				Entry<Integer, Float> entry = it.next();
				retentionTime[j] = entry.getKey();
				scanValues[j] = entry.getValue();
				j++;
			}
			UnivariateFunction univariateFunction = interpolator.interpolate(retentionTime, scanValues);
			for(int i = beginRetentionTimeMax; i <= endRetentionTimeMin; i += retentionTimeWindow) {
				double value = univariateFunction.value(i);
				PeakSampleData peakSampleData = new PeakSampleData(value, null);
				data.add(peakSampleData);
			}
		}
		setRetentionTime(samples);
	}

	private void setClosestScan(Samples samples, Map<String, NavigableMap<Integer, Float>> extractScans) {

		for(Sample sample : samples.getSampleList()) {
			List<PeakSampleData> sampleData = sample.getSampleData();
			NavigableMap<Integer, Float> scans = extractScans.get(sample.getSampleName());
			for(int i = beginRetentionTimeMax; i <= endRetentionTimeMin; i += retentionTimeWindow) {
				Float value = getClosestScans(scans, i);
				PeakSampleData peakSampleData = new PeakSampleData(value, null);
				sampleData.add(peakSampleData);
			}
		}
		setRetentionTime(samples);
	}

	private void setRetentionTime(Samples samples) {

		List<Integer> retentionTime = new ArrayList<>();
		for(int i = beginRetentionTimeMax; i <= endRetentionTimeMin; i += retentionTimeWindow) {
			retentionTime.add(i);
		}
		samples.getVariables().addAll(RetentionTime.create(retentionTime));
	}

	private void useDefaultProperties(Samples samples, Map<String, NavigableMap<Integer, Float>> extractScans) {

		Set<Integer> retentionTimesSet = extractScans.entrySet().iterator().next().getValue().keySet();
		List<Integer> retentionTimes = new ArrayList<>(retentionTimesSet);
		Collections.sort(retentionTimes);
		for(Sample sample : samples.getSampleList()) {
			Iterator<Integer> iterator = retentionTimes.iterator();
			List<PeakSampleData> data = sample.getSampleData();
			NavigableMap<Integer, Float> scans = extractScans.get(sample.getSampleName());
			while(iterator.hasNext()) {
				Integer time = iterator.next();
				Float value = scans.get(time);
				PeakSampleData peakSampleData;
				if(value != null) {
					peakSampleData = new PeakSampleData(value, null);
				} else {
					value = getClosestScans(scans, time);
					peakSampleData = new PeakSampleData(value, null);
				}
				data.add(peakSampleData);
			}
		}
		samples.getVariables().addAll(RetentionTime.create(retentionTimes));
	}
}