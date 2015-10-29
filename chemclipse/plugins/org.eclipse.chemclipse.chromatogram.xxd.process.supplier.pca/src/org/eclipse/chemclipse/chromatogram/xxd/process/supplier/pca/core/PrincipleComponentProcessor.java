/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.peak.IPeakImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ejml.example.PrincipalComponentAnalysis;

public class PrincipleComponentProcessor {

	private static final Logger logger = Logger.getLogger(PrincipleComponentProcessor.class);
	private static final double NORMALIZATION_FACTOR = 1000;

	public PcaResults process(List<IDataInputEntry> dataInputEntries, int retentionTimeWindow, int numberOfPrincipleComponents, IProgressMonitor monitor) {// , int typeOfExtraction) {

		// PATRICK/KEVIN: added extraction type as local variable for now
		int typeOfExtraction = 0; // 0 = peaks, 1 = scans
		/*
		 * Initialize PCA Results
		 * I've added a comment.
		 */
		PcaResults pcaResults = new PcaResults(dataInputEntries);
		pcaResults.setRetentionTimeWindow(retentionTimeWindow);
		pcaResults.setNumberOfPrincipleComponents(numberOfPrincipleComponents);
		/*
		 * Extract data
		 */
		List<File> inputFiles = new ArrayList<File>();
		for(IDataInputEntry inputEntry : dataInputEntries) {
			inputFiles.add(new File(inputEntry.getInputFile()));
		}
		//
		if(typeOfExtraction == 0) {
			/*
			 * Read Peaks and prepare intensity values.
			 */
			monitor.subTask("Extract peak values");
			Map<String, IPeaks> peakMap = extractPeaks(inputFiles, monitor);
			monitor.subTask("Prepare peak values");
			preparePcaResults(peakMap, pcaResults);
			SortedSet<Integer> collectedRetentionTimes = collectRetentionTimes(peakMap);
			List<Integer> extractedRetentionTimes = calculateCondensedRetentionTimes(collectedRetentionTimes, retentionTimeWindow);
			int sampleSize = extractedRetentionTimes.size();
			pcaResults.setExtractedRetentionTimes(extractedRetentionTimes);
			Map<String, double[]> pcaPeakMap = extractPcaPeakMap(peakMap, extractedRetentionTimes, retentionTimeWindow);
			normalizeIntensityValues(pcaPeakMap);
			/*
			 * Run PCA
			 */
			monitor.subTask("Run PCA");
			PrincipalComponentAnalysis principleComponentAnalysis = initializePCA(pcaPeakMap, sampleSize, numberOfPrincipleComponents);
			List<double[]> basisVectors = getBasisVectors(principleComponentAnalysis, numberOfPrincipleComponents);
			pcaResults.setBasisVectors(basisVectors);
			setEigenSpaceAndErrorValues(principleComponentAnalysis, pcaPeakMap, pcaResults);
		} else if(typeOfExtraction == 1) {
			/*
			 * Read Scans and prepare intensity values.
			 */
			monitor.subTask("Extract scan values");
			@SuppressWarnings("unused")
			Map<String, List<Float>> scanMap = extractScans(inputFiles, monitor);
			monitor.subTask("Prepare scan values");
			// PATRICK/KEVIN TODO: convert scanMap to pcaResults
		}
		/*
		 * Return result.
		 */
		return pcaResults;
	}

	/**
	 * Re-evaluates the PCA results.
	 * 
	 * @param pcaResults
	 */
	public void reEvaluate(PcaResults pcaResults) {

		int numberOfPrincipleComponents = pcaResults.getNumberOfPrincipleComponents();
		int numSamples = 0;
		int sampleSize = 0; // Needs to be the same size for each sample.
		//
		Map<ISample, IPcaResult> resultMap = pcaResults.getPcaResultMap();
		for(Map.Entry<ISample, IPcaResult> entry : resultMap.entrySet()) {
			if(entry.getKey().isSelected()) {
				numSamples++;
				sampleSize = entry.getValue().getSampleData().length;
			}
		}
		//
		PrincipalComponentAnalysis principleComponentAnalysis = new PrincipalComponentAnalysis();
		principleComponentAnalysis.setup(numSamples, sampleSize);
		/*
		 * Add the samples.
		 */
		for(Map.Entry<ISample, IPcaResult> entry : resultMap.entrySet()) {
			if(entry.getKey().isSelected()) {
				double[] sampleData = entry.getValue().getSampleData();
				principleComponentAnalysis.addSample(sampleData);
			}
		}
		/*
		 * Compute the basis for the number of principle components.
		 */
		principleComponentAnalysis.computeBasis(numberOfPrincipleComponents);
		List<double[]> basisVectors = getBasisVectors(principleComponentAnalysis, numberOfPrincipleComponents);
		pcaResults.setBasisVectors(basisVectors);
		/*
		 * Re-evaluate the eigen space and error membership.
		 */
		for(Map.Entry<ISample, IPcaResult> entry : resultMap.entrySet()) {
			if(entry.getKey().isSelected()) {
				//
				IPcaResult pcaResult = entry.getValue();
				double[] sampleData = pcaResult.getSampleData();
				double[] eigenSpace = principleComponentAnalysis.sampleToEigenSpace(sampleData);
				double errorMemberShip = principleComponentAnalysis.errorMembership(sampleData);
				//
				pcaResult.setEigenSpace(eigenSpace);
				pcaResult.setErrorMemberShip(errorMemberShip);
			}
		}
	}

	/**
	 * Sets the initial PCA result map.
	 * 
	 * @param peakMap
	 * @param pcaResults
	 */
	private void preparePcaResults(Map<String, IPeaks> peakMap, PcaResults pcaResults) {

		Map<ISample, IPcaResult> pcaResultMap = pcaResults.getPcaResultMap();
		for(Map.Entry<String, IPeaks> entry : peakMap.entrySet()) {
			/*
			 * PCA result
			 */
			PcaResult pcaResult = new PcaResult();
			pcaResult.setPeaks(entry.getValue());
			pcaResultMap.put(new Sample(entry.getKey()), pcaResult);
		}
	}

	/**
	 * Initializes the PCA analysis.
	 * 
	 * @param pcaPeakMap
	 * @param numSamples
	 * @param sampleSize
	 * @param numberOfPrincipleComponents
	 * @return PrincipleComponentAnalysis
	 */
	private PrincipalComponentAnalysis initializePCA(Map<String, double[]> pcaPeakMap, int sampleSize, int numberOfPrincipleComponents) {

		/*
		 * Initialize the PCA analysis.
		 */
		int numSamples = pcaPeakMap.size();
		PrincipalComponentAnalysis principleComponentAnalysis = new PrincipalComponentAnalysis();
		principleComponentAnalysis.setup(numSamples, sampleSize);
		/*
		 * Add the samples.
		 */
		for(Map.Entry<String, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			principleComponentAnalysis.addSample(sampleData);
		}
		/*
		 * Compute the basis for the number of principle components.
		 */
		principleComponentAnalysis.computeBasis(numberOfPrincipleComponents);
		return principleComponentAnalysis;
	}

	private List<double[]> getBasisVectors(PrincipalComponentAnalysis principleComponentAnalysis, int numberOfPrincipleComponents) {

		/*
		 * Print the basis vectors.
		 */
		List<double[]> basisVectors = new ArrayList<double[]>();
		for(int principleComponent = 0; principleComponent < numberOfPrincipleComponents; principleComponent++) {
			double[] basisVector = principleComponentAnalysis.getBasisVector(principleComponent);
			basisVectors.add(basisVector);
		}
		return basisVectors;
	}

	private void setEigenSpaceAndErrorValues(PrincipalComponentAnalysis principleComponentAnalysis, Map<String, double[]> pcaPeakMap, PcaResults pcaResults) {

		/*
		 * Set the eigen space and error membership values.
		 */
		for(Map.Entry<String, double[]> entry : pcaPeakMap.entrySet()) {
			/*
			 * Get the sample result.
			 */
			IPcaResult pcaResult = pcaResults.getPcaResultMap().get(new Sample(entry.getKey()));
			//
			double[] sampleData = entry.getValue();
			double[] eigenSpace = principleComponentAnalysis.sampleToEigenSpace(sampleData);
			double errorMemberShip = principleComponentAnalysis.errorMembership(sampleData);
			//
			pcaResult.setSampleData(sampleData);
			pcaResult.setEigenSpace(eigenSpace);
			pcaResult.setErrorMemberShip(errorMemberShip);
		}
	}

	/**
	 * All extracted intensity values will be normalized.
	 * 
	 * @param pcaPeakMap
	 */
	private void normalizeIntensityValues(Map<String, double[]> pcaPeakMap) {

		/*
		 * Get the highest values
		 */
		double sampleMax = 0;
		for(Map.Entry<String, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			double sampleDataMax = Calculations.getMax(sampleData);
			sampleMax = (sampleDataMax > sampleMax) ? sampleDataMax : sampleMax;
		}
		/*
		 * Normalize the array if the maximum value is > 0.
		 */
		if(sampleMax > 0) {
			/*
			 * Normalize the values.
			 */
			for(Map.Entry<String, double[]> entry : pcaPeakMap.entrySet()) {
				double[] sampleData = entry.getValue();
				for(int i = 0; i < sampleData.length; i++) {
					double value = sampleData[i];
					if(value > 0) {
						double normalizedValue = (NORMALIZATION_FACTOR / sampleMax) * value;
						sampleData[i] = normalizedValue;
					}
				}
				entry.setValue(sampleData);
			}
		}
	}

	/**
	 * Extracts a PCA peak map.
	 * 
	 * @param peakMap
	 * @param extractedRetentionTimes
	 * @param retentionTimeWindow
	 * @return Map<String, double[]>
	 */
	private Map<String, double[]> extractPcaPeakMap(Map<String, IPeaks> peakMap, List<Integer> extractedRetentionTimes, int retentionTimeWindow) {

		Map<String, double[]> pcaPeakMap = new HashMap<String, double[]>();
		for(Map.Entry<String, IPeaks> peaksEntry : peakMap.entrySet()) {
			String name = peaksEntry.getKey();
			IPeaks peaks = peaksEntry.getValue();
			double[] intensityValues = extractPcaPeakIntensityValues(peaks, extractedRetentionTimes, retentionTimeWindow);
			pcaPeakMap.put(name, intensityValues);
		}
		return pcaPeakMap;
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
	 * Calculates the condensed retention time.
	 * 
	 * @return int
	 */
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
	 * Loads each file and tries to extract the scans.
	 * 
	 * @param scanFiles
	 * @param monitor
	 * @return Map<String, List<Float>>
	 */
	private Map<String, List<Float>> extractScans(List<File> scanFiles, IProgressMonitor monitor) {

		Map<String, List<Float>> scanMap = new HashMap<String, List<Float>>();
		for(File scanFile : scanFiles) {
			IChromatogramMSDImportConverterProcessingInfo processingInfo = ChromatogramConverterMSD.convert(scanFile, monitor);
			try {
				IChromatogramMSD chromatogram = processingInfo.getChromatogram();
				String name = extractNameFromFile(scanFile, "n.a.");
				List<Float> slopes = new ArrayList<Float>();
				float previousSignal = 0;
				for(IScan scan : chromatogram.getScans()) {
					float currentSignal = scan.getTotalSignal();
					float slope = currentSignal - previousSignal; // intensities
					// scan.getRetentionTime()); // times
					slopes.add(slope);
					previousSignal = currentSignal;
				}
				scanMap.put(name, slopes);
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		}
		return scanMap;
	}

	/**
	 * Loads each file and tries to extract the peaks.
	 * 
	 * @param peakFiles
	 * @param monitor
	 * @return Map<String, IPeaks>
	 */
	private Map<String, IPeaks> extractPeaks(List<File> peakFiles, IProgressMonitor monitor) {

		Map<String, IPeaks> peakMap = new HashMap<String, IPeaks>();
		for(File peakFile : peakFiles) {
			IPeakImportConverterProcessingInfo processingInfo = PeakConverterMSD.convert(peakFile, monitor);
			try {
				IPeaks peaks = processingInfo.getPeaks();
				String name = extractNameFromFile(peakFile, "n.a.");
				peakMap.put(name, peaks);
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		}
		return peakMap;
	}

	/**
	 * Collects all available retention times.
	 * 
	 * @param peakMap
	 * @return SortedSet<Integer>
	 */
	private SortedSet<Integer> collectRetentionTimes(Map<String, IPeaks> peakMap) {

		SortedSet<Integer> collectedRetentionTimes = new TreeSet<Integer>();
		for(Map.Entry<String, IPeaks> peaksEntry : peakMap.entrySet()) {
			IPeaks peaks = peaksEntry.getValue();
			for(IPeak peak : peaks.getPeaks()) {
				if(peak instanceof IPeakMSD) {
					IPeakMSD peakMSD = (IPeakMSD)peak;
					int retentionTime = peakMSD.getPeakModel().getRetentionTimeAtPeakMaximum();
					collectedRetentionTimes.add(retentionTime);
				}
			}
		}
		return collectedRetentionTimes;
	}

	/**
	 * Returns an array of area values of the peaks.
	 * 
	 * @param peaks
	 * @param extractedRetentionTimes
	 * @param retentionTimeWindow
	 * @return double[]
	 */
	private double[] extractPcaPeakIntensityValues(IPeaks peaks, List<Integer> extractedRetentionTimes, int retentionTimeWindow) {

		int retentionTimeDelta = retentionTimeWindow / 2;
		double[] intensityValues = new double[extractedRetentionTimes.size()];
		/*
		 * Try to get an intensity value for each extracted retention time.
		 * If there is no peak, the value will be 0.
		 */
		int index = 0;
		for(int extractedRetentionTime : extractedRetentionTimes) {
			double abundance = 0;
			int leftRetentionTimeBound = extractedRetentionTime - retentionTimeDelta;
			int rightRetentionTimeBound = extractedRetentionTime + retentionTimeDelta;
			/*
			 * Check each peak.
			 */
			for(IPeak peak : peaks.getPeaks()) {
				/*
				 * Try to get the retention time.
				 */
				if(peak instanceof IPeakMSD) {
					/*
					 * The retention time must be in the retention time window.
					 */
					IPeakMSD peakMSD = (IPeakMSD)peak;
					int retentionTime = peakMSD.getPeakModel().getRetentionTimeAtPeakMaximum();
					if(retentionTime >= leftRetentionTimeBound && retentionTime <= rightRetentionTimeBound) {
						abundance += peakMSD.getIntegratedArea();
					}
				}
			}
			/*
			 * Store the extracted abundance.
			 */
			intensityValues[index++] = abundance;
		}
		return intensityValues;
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
}
