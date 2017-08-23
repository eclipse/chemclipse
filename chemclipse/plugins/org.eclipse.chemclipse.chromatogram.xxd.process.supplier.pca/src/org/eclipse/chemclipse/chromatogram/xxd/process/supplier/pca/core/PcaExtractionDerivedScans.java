/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISlopes;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.SampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Slopes;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionDerivedScans implements IDataExtraction {

	private static final Logger logger = Logger.getLogger(PrincipleComponentProcessor.class);
	private List<IDataInputEntry> dataInputEntriesAll;
	private int retentionTimeWindow;

	/**
	 * Calculates the condensed retention time.
	 *
	 * @return int
	 */
	public PcaExtractionDerivedScans(List<IDataInputEntry> dataInputEntries, int retentionTimeWindow) {
		dataInputEntriesAll = dataInputEntries;
		this.retentionTimeWindow = retentionTimeWindow;
	}

	private int calculateCondensedRetentionTime(List<Integer> retentionTimes) {

		int retentionTimeCondensed = 0;
		if(retentionTimes != null) {
			int size = retentionTimes.size();
			if(size > 0) {
				int retentionTimeSum = 0;
				for(Integer rt : retentionTimes) {
					retentionTimeSum += rt;
				}
				retentionTimeCondensed = retentionTimeSum / size;
			}
		}
		return retentionTimeCondensed;
	}

	/**
	 * Calculates a list of condensed retention times. The condense factor is given by the retention time window.
	 *
	 * @param collectedRetentionTimes
	 * @return List<Integer>
	 */
	private List<Integer> calculateCondensedRetentionTimes(SortedSet<Integer> collectedRetentionTimes, int retentionTimeWindow) {

		List<Integer> extractedRetentionTimes = new ArrayList<Integer>();
		//
		int retentionTime;
		int retentionTimeMarker;
		List<Integer> retentionTimes = null;
		//
		Iterator<Integer> iterator = collectedRetentionTimes.iterator();
		if(iterator.hasNext()) {
			/*
			 * Initialize: Get the first marker.
			 */
			retentionTime = iterator.next();
			retentionTimeMarker = retentionTime + retentionTimeWindow;
			retentionTimes = new ArrayList<Integer>();
			retentionTimes.add(retentionTime);
			/*
			 * Parse all subsequent retention times.
			 */
			while(iterator.hasNext()) {
				retentionTime = iterator.next();
				/*
				 * Check the next retention time.
				 */
				if(retentionTime > retentionTimeMarker) {
					/*
					 * Condense the retention time list.
					 */
					int retentionTimeCondensed = calculateCondensedRetentionTime(retentionTimes);
					if(retentionTimeCondensed > 0) {
						extractedRetentionTimes.add(retentionTimeCondensed);
					}
					/*
					 * Initialize the next chunk.
					 */
					retentionTimeMarker = retentionTime + retentionTimeWindow;
					retentionTimes = new ArrayList<Integer>();
					retentionTimes.add(retentionTime);
				} else {
					/*
					 * Collect the retention time.
					 */
					retentionTimes.add(retentionTime);
				}
			}
		}
		/*
		 * Add the last condensed retention time.
		 */
		int retentionTimeCondensed = calculateCondensedRetentionTime(retentionTimes);
		if(retentionTimeCondensed > 0) {
			extractedRetentionTimes.add(retentionTimeCondensed);
		}
		return extractedRetentionTimes;
	}

	/**
	 * Collects all available retention times for scans.
	 * ADDED BY TEAM C
	 *
	 * @param scanMap
	 * @return SortedSet<Integer>
	 */
	private SortedSet<Integer> collectScanRetentionTimes(Map<String, ISlopes> scanMap) {

		SortedSet<Integer> collectedRetentionTimes = new TreeSet<Integer>();
		for(Map.Entry<String, ISlopes> scansEntry : scanMap.entrySet()) {
			ISlopes islopes = scansEntry.getValue();
			List<Integer> retentionTimes = islopes.getRetentionTimes();
			for(Integer retentionTime : retentionTimes) {
				collectedRetentionTimes.add(retentionTime);
			}
		}
		return collectedRetentionTimes;
	}

	/**
	 * Extracts the file name.
	 *
	 * @param file
	 * @param nameDefault
	 * @return String
	 */
	private String extractNameFromFile(File file, String nameDefault) {

		if(file != null) {
			String fileName = file.getName();
			if(fileName != "" && fileName != null) {
				/*
				 * Extract the file name.
				 */
				String[] parts = fileName.split("\\.");
				if(parts.length > 2) {
					StringBuilder builder = new StringBuilder();
					for(int i = 0; i < parts.length - 1; i++) {
						builder.append(parts[i]);
						builder.append(".");
					}
					String name = builder.toString();
					nameDefault = name.substring(0, name.length() - 1);
				} else {
					/*
					 * If there are not 2 parts, it's assumed that the file had no extension.
					 */
					if(parts.length == 2) {
						nameDefault = parts[0];
					}
				}
			}
		}
		return nameDefault;
	}

	/**
	 * Returns an array of area values of the peaks.
	 *
	 * @param peaks
	 * @param extractedRetentionTimes
	 * @param retentionTimeWindow
	 * @return double[]
	 */
	/**
	 * Extracts a PCA scan map.
	 * ADDED BY TEAM C
	 *
	 * @param scanMap
	 * @param extractedRetentionTimes
	 * @param retentionTimeWindow
	 * @return Map<String, double[]>
	 */
	private Map<String, double[]> extractPcaScanMap(Map<String, ISlopes> scanMap, List<Integer> extractedRetentionTimes, int retentionTimeWindow) {

		Map<String, double[]> pcaScanMap = new HashMap<String, double[]>();
		for(Map.Entry<String, ISlopes> ScansEntry : scanMap.entrySet()) {
			String name = ScansEntry.getKey();
			ISlopes scans = ScansEntry.getValue();
			double[] intensityValues = extractPcaScanSlopeValues(scans, extractedRetentionTimes, retentionTimeWindow);
			pcaScanMap.put(name, intensityValues);
		}
		return pcaScanMap;
	}

	/**
	 * Returns an array of slopes values at extracted retention times.
	 * ADDED BY TEAM C
	 *
	 * @param peaks
	 * @param extractedRetentionTimes
	 * @param retentionTimeWindow
	 * @return double[]
	 */
	private double[] extractPcaScanSlopeValues(ISlopes scans, List<Integer> extractedRetentionTimes, int retentionTimeWindow) {

		int retentionTimeDelta = retentionTimeWindow / 2;
		double[] slopeValues = new double[extractedRetentionTimes.size()];
		/*
		 * Try to get a slope value for each extracted retention time.
		 * If there is no peak, the value will be 0.
		 */
		int index = 0;
		for(int extractedRetentionTime : extractedRetentionTimes) {
			int leftRetentionTimeBound = extractedRetentionTime - retentionTimeDelta;
			int rightRetentionTimeBound = extractedRetentionTime + retentionTimeDelta;
			/*
			 * Check each scan
			 */
			List<Integer> retentionTimes = scans.getRetentionTimes();
			List<Float> slopes = scans.getSlopes();
			int current = 0;
			for(Float slope : slopes) {
				/*
				 * The retention time must be in the retention time window.
				 */
				int retentionTime = retentionTimes.get(current);
				if(retentionTime >= leftRetentionTimeBound && retentionTime <= rightRetentionTimeBound) {
					/*
					 * Store the slope
					 */
					slopeValues[index++] = slope;
				}
			}
		}
		return slopeValues;
	}

	/**
	 * Loads each file and tries to extract the scans.
	 * ADDED BY TEAM C
	 *
	 * @param scanFiles
	 * @param monitor
	 * @return Map<String, ISlopes>
	 */
	private Map<String, ISlopes> extractScans(List<File> scanFiles, IProgressMonitor monitor) {

		Map<String, ISlopes> scanMap = new HashMap<String, ISlopes>();
		for(File scanFile : scanFiles) {
			IChromatogramMSDImportConverterProcessingInfo processingInfo = ChromatogramConverterMSD.convert(scanFile, monitor);
			try {
				IChromatogramMSD chromatogram = processingInfo.getChromatogram();
				String name = extractNameFromFile(scanFile, "n.a.");
				Slopes slopes = new Slopes();
				List<Float> slopeList = new ArrayList<Float>();
				List<Integer> retentionTimes = new ArrayList<Integer>();
				float previousSignal = 0;
				int previousTime = 0;
				for(IScan scan : chromatogram.getScans()) {
					float currentSignal = scan.getTotalSignal();
					int currentTime = scan.getRetentionTime();
					float slope = (currentSignal - previousSignal) / (currentTime - previousTime);
					slopeList.add(slope);
					retentionTimes.add(currentTime);
					previousSignal = currentSignal;
					previousTime = currentTime;
				}
				slopes.setSlopes(slopeList);
				slopes.setRetentionTimes(retentionTimes);
				scanMap.put(name, slopes);
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		}
		return scanMap;
	}

	/**
	 * Sets the initial PCA result map.
	 * ADDED BY TEAM C
	 *
	 * @param scanMap
	 * @param pcaResults
	 */
	private void prepareScanPcaResults(Map<String, ISlopes> scanMap, IPcaResults pcaResults) {

		List<ISample> samples = pcaResults.getSampleList();
		List<IDataInputEntry> dataInputEntry = pcaResults.getDataInputEntries();
		samples.clear();
		for(Map.Entry<String, ISlopes> entry : scanMap.entrySet()) {
			ISample sample = new Sample(entry.getKey());
			dataInputEntry.forEach((input) -> {
				if(input.getName().equals(entry.getKey())) {
					sample.setGroupName(input.getGroupName());
				}
			});
			sample.getPcaResult().setSlopes(entry.getValue());
			samples.add(sample);
		}
	}

	@Override
	public IPcaResults process(IProgressMonitor monitor) {

		List<IDataInputEntry> dataInputEntries = IDataExtraction.removeFileSameName(dataInputEntriesAll);
		/*
		 * Initialize PCA Results
		 */
		IPcaResults pcaResults = new PcaResults(dataInputEntries);
		pcaResults.setRetentionTimeWindow(retentionTimeWindow);
		pcaResults.setExtractionType(DERIVED_SCAN);
		/*
		 * Extract data
		 */
		List<File> inputFiles = new ArrayList<File>();
		for(IDataInputEntry inputEntry : dataInputEntries) {
			inputFiles.add(new File(inputEntry.getInputFile()));
		}
		//
		/*
		 * Read Scans and prepare intensity values.
		 */
		monitor.subTask("Extract scan values");
		Map<String, ISlopes> scanMap = extractScans(inputFiles, monitor);
		monitor.subTask("Prepare scan values");
		prepareScanPcaResults(scanMap, pcaResults);
		SortedSet<Integer> collectedRetentionTimes = collectScanRetentionTimes(scanMap);
		List<Integer> extractedRetentionTimes = calculateCondensedRetentionTimes(collectedRetentionTimes, retentionTimeWindow);
		pcaResults.setExtractedRetentionTimes(extractedRetentionTimes);
		Map<String, double[]> pcaScanMap = extractPcaScanMap(scanMap, extractedRetentionTimes, retentionTimeWindow);
		/* Normalization added TEAM C */
		// normalizeScanIntensityValues(pcaScanMap);
		setDataSamples(pcaResults, pcaScanMap);
		IDataExtraction.createGroup(pcaResults);
		IDataExtraction.setSelectedRetentionTime(pcaResults);
		return pcaResults;
	}

	private void setDataSamples(IPcaResults pcaResults, Map<String, double[]> pcaScanMap) {

		pcaResults.getSampleList().forEach(s -> {
			String name = s.getName();
			double[] d = pcaScanMap.get(name);
			for(int i = 0; i < d.length; i++) {
				s.getSampleData().add(new SampleData(d[i]));
			}
		});
	}
}