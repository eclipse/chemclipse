/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.SampleData;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionScans implements IDataExtraction {

	private int beginRetentionTimeMax;
	private List<IDataInputEntry> dataInputEntriesAll;
	private int endRetentionTimeMin;
	private int extractionType;
	final private int MAX_SIZE = 9000;
	private int retentionTimeWindow;
	private int scanInterval;
	private boolean similarChromatogram; // chromatograms have to have some retention time in first scan and same scan interval
	private boolean useDefoultProperties;

	public PcaExtractionScans(int retentionTimeWindow, List<IDataInputEntry> dataInputEntries, int extractionType, boolean useDefoultProperties) {
		this.retentionTimeWindow = retentionTimeWindow;
		this.dataInputEntriesAll = dataInputEntries;
		this.extractionType = extractionType;
		this.useDefoultProperties = useDefoultProperties;
	}

	private Map<String, TreeMap<Integer, Float>> extractScans(List<IDataInputEntry> inputFiles, IProgressMonitor monitor) {

		similarChromatogram = true;
		beginRetentionTimeMax = Integer.MAX_VALUE;
		endRetentionTimeMin = 0;
		Map<String, TreeMap<Integer, Float>> scanMap = new HashMap<>();
		boolean firstChromatogram = true;
		for(IDataInputEntry input : inputFiles) {
			IChromatogramMSDImportConverterProcessingInfo processingInfo = ChromatogramConverterMSD.convert(new File(input.getInputFile()), monitor);
			IChromatogramMSD chromatogram = processingInfo.getChromatogram();
			String name = input.getName();
			TreeMap<Integer, Float> extractScans = new TreeMap<>();
			List<IScan> scans = chromatogram.getScans();
			int scanInterval = chromatogram.getScanInterval();
			/*
			 * extract scans
			 */
			for(IScan scan : scans) {
				float currentSignal = scan.getTotalSignal();
				int currentTime = scan.getRetentionTime();
				extractScans.put(currentTime, currentSignal);
			}
			if(!firstChromatogram) {
				int begin = scans.get(0).getRetentionTime();
				int end = scans.get(scans.size() - 1).getRetentionTime();
				if(scanInterval != this.scanInterval || beginRetentionTimeMax != begin) {
					similarChromatogram = false;
					similarChromatogram = false;
				}
				if(begin > beginRetentionTimeMax) {
					beginRetentionTimeMax = begin;
				}
				if(end < endRetentionTimeMin) {
					endRetentionTimeMin = end;
				}
			} else {
				firstChromatogram = false;
				beginRetentionTimeMax = scans.get(0).getRetentionTime();
				endRetentionTimeMin = scans.get(scans.size() - 1).getRetentionTime();
				this.scanInterval = scanInterval;
			}
			scanMap.put(name, extractScans);
		}
		return scanMap;
	}

	private Float getClosestScans(TreeMap<Integer, Float> peakTree, int retentionTime) {

		Map.Entry<Integer, Float> peakRetentionTimeCeil = peakTree.ceilingEntry(retentionTime);
		Map.Entry<Integer, Float> peakRetentionTimeFlour = peakTree.floorEntry(retentionTime);
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

	private void interpolation(IPcaResults pcaResults, Map<String, TreeMap<Integer, Float>> extractScans, UnivariateInterpolator interpolator) {

		for(ISample sample : pcaResults.getSampleList()) {
			List<ISampleData> data = sample.getSampleData();
			TreeMap<Integer, Float> scans = extractScans.get(sample.getName());
			double[] retetnionTime = new double[scans.size()];
			double[] scanValues = new double[scans.size()];
			int j = 0;
			Iterator<Entry<Integer, Float>> it = scans.entrySet().iterator();
			while(it.hasNext()) {
				Entry<Integer, Float> entry = it.next();
				retetnionTime[j] = entry.getKey();
				scanValues[j] = entry.getValue();
				j++;
			}
			UnivariateFunction fun = interpolator.interpolate(retetnionTime, scanValues);
			for(int i = beginRetentionTimeMax; i <= endRetentionTimeMin; i += retentionTimeWindow) {
				Double value = fun.value(i);
				ISampleData d = new SampleData(value);
				data.add(d);
			}
		}
		setRetentionTime(pcaResults);
	}

	private IPcaResults prepareResults(List<IDataInputEntry> entries) {

		IPcaResults pcaResults = new PcaResults(entries);
		for(IDataInputEntry entry : entries) {
			pcaResults.getSampleList().add(new Sample(entry.getName()));
		}
		return pcaResults;
	}

	@Override
	public IPcaResults process(IProgressMonitor monitor) {

		List<IDataInputEntry> dataInputEntries = IDataExtraction.removeFileSameName(dataInputEntriesAll);
		/*
		 * Initialize PCA Results
		 */
		IPcaResults pcaResults = prepareResults(dataInputEntries);
		pcaResults.setRetentionTimeWindow(retentionTimeWindow);
		pcaResults.setExtractionType(extractionType);
		/*
		 * Extract data
		 */
		Map<String, TreeMap<Integer, Float>> extractScans = extractScans(dataInputEntries, monitor);
		if(similarChromatogram && useDefoultProperties) {
			this.retentionTimeWindow = this.scanInterval;
		}
		int size = ((endRetentionTimeMin - beginRetentionTimeMax) / retentionTimeWindow);
		if(size > MAX_SIZE) {
			retentionTimeWindow = (endRetentionTimeMin - beginRetentionTimeMax) / MAX_SIZE;
			similarChromatogram = false;
		}
		endRetentionTimeMin = ((endRetentionTimeMin - beginRetentionTimeMax) / retentionTimeWindow) * retentionTimeWindow;
		if(similarChromatogram && useDefoultProperties) {
			useDefoultProperties(pcaResults, extractScans);
		} else {
			switch(extractionType) {
				case CLOSEST_SCAN:
					setClosestScan(pcaResults, extractScans);
					break;
				case LINEAR_INTERPOLATION_SCAN:
					interpolation(pcaResults, extractScans, new LinearInterpolator());
					break;
			}
		}
		pcaResults.setRetentionTimeWindow(retentionTimeWindow);
		/*
		 * Set selected retention Time
		 */
		IDataExtraction.setSelectedRetentionTime(pcaResults);
		/*
		 * create Groups
		 */
		IDataExtraction.createGroup(pcaResults);
		return pcaResults;
	}

	private void setClosestScan(IPcaResults pcaResults, Map<String, TreeMap<Integer, Float>> extractScans) {

		for(ISample sample : pcaResults.getSampleList()) {
			List<ISampleData> data = sample.getSampleData();
			TreeMap<Integer, Float> scans = extractScans.get(sample.getName());
			for(int i = beginRetentionTimeMax; i <= endRetentionTimeMin; i += retentionTimeWindow) {
				Float value = getClosestScans(scans, i);
				ISampleData d = new SampleData(value);
				data.add(d);
			}
		}
		setRetentionTime(pcaResults);
	}

	private void setRetentionTime(IPcaResults pcaResults) {

		List<Integer> retentionTime = new ArrayList<>();
		for(int i = beginRetentionTimeMax; i <= endRetentionTimeMin; i += retentionTimeWindow) {
			retentionTime.add(i);
		}
		pcaResults.setExtractedRetentionTimes(retentionTime);
	}

	private void useDefoultProperties(IPcaResults pcaResults, Map<String, TreeMap<Integer, Float>> extractScans) {

		Set<Integer> retentionTimes = extractScans.entrySet().iterator().next().getValue().keySet();
		for(ISample sample : pcaResults.getSampleList()) {
			Iterator<Integer> it = retentionTimes.iterator();
			List<ISampleData> data = sample.getSampleData();
			TreeMap<Integer, Float> scans = extractScans.get(sample.getName());
			while(it.hasNext()) {
				Integer time = it.next();
				Float value = scans.get(time);
				ISampleData d;
				if(value != null) {
					d = new SampleData(value);
				} else {
					value = getClosestScans(scans, time);
					d = new SampleData(value);
				}
				data.add(d);
			}
		}
		pcaResults.setExtractedRetentionTimes(new ArrayList<>(retentionTimes));
	}
}
