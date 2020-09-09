/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Janos Binder - supporting optional peaks
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.Peaks;
import org.eclipse.chemclipse.msd.converter.io.IPeakReader;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IPeakIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.PeakIon;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ELUReader implements IPeakReader {

	/*
	 * We could try to add additional filter options in this class.
	 */
	private static final Logger logger = Logger.getLogger(ELUReader.class);
	//
	private static final Pattern HITS_PATTERN = Pattern.compile("(NAME)");
	private static final Pattern PEAK_DATA_PATTERN = Pattern.compile("(NAME)(.*?)(^$)", Pattern.DOTALL | Pattern.MULTILINE);
	private static final Pattern NAME_PATTERN = Pattern.compile("(NAME)(.*)(SC)([0-9]+)(.*)(FR)([0-9]+)(-)([0-9]+)(.*)(RT)([0-9]+[.,]+[0-9]+)"); // "(NAME)(.*)"
	private static final Pattern PROFILE_DATA_PATTERN = Pattern.compile("(RE)(.*?)(NUM PEAKS)", Pattern.DOTALL | Pattern.MULTILINE);
	private static final Pattern PROFILE_PATTERN = Pattern.compile("([0-9]+)");
	private static final Pattern PEAK_PATTERN = Pattern.compile("(NUM PEAKS)(.*)", Pattern.DOTALL | Pattern.MULTILINE);
	private static final Pattern ION_PATTERN = Pattern.compile("\\((\\d+),(\\d+) ?(\\w[\\d.]*)?\\)");
	//
	private static final String NEW_LINE = "\n";
	private static final String CARRIAGE_RETURN = "\r";

	@Override
	public IProcessingInfo<IPeaks<?>> read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IProcessingInfo<IPeaks<?>> processingInfo = new ProcessingInfo<IPeaks<?>>();
		String content = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
		int numberOfHits = getNumberOfHits(content);
		if(numberOfHits <= 0) {
			processingInfo.addErrorMessage("AMDIS ELU Parser", "There seems to be no peak in the file.");
		} else {
			/*
			 * Calculate the scan interval and extract the peaks.
			 */
			int scanInterval;
			if(numberOfHits <= 2) {
				scanInterval = calculateScanIntervalSimple(content);
			} else {
				scanInterval = calculateScanIntervalExtended(content);
			}
			extractAmdisPeaks(content, scanInterval, processingInfo);
		}
		return processingInfo;
	}

	private int getNumberOfHits(String content) {

		Matcher matcher = HITS_PATTERN.matcher(content);
		int counter = 0;
		while(matcher.find()) {
			counter++;
		}
		return counter;
	}

	/**
	 * Calculates the mean scan interval without scan delay.
	 * 
	 * @param content
	 * @return int
	 */
	private int calculateScanIntervalSimple(String content) {

		List<Double> scanIntervals = new ArrayList<Double>();
		Matcher matcher = NAME_PATTERN.matcher(content);
		while(matcher.find()) {
			/*
			 * Try to calculate a mean value using the scan number and its retention time.
			 * Though, it's not perfect but that's the only information we get from the file.
			 */
			try {
				int scan = Integer.parseInt(matcher.group(4));
				double retentionTime = Double.parseDouble(matcher.group(12)) * IChromatogram.MINUTE_CORRELATION_FACTOR;
				if(scan > 0) {
					scanIntervals.add(retentionTime / scan);
				}
			} catch(NumberFormatException e) {
				//
			}
		}
		/*
		 * The scan interval must be scaled in milliseconds.
		 */
		int size = scanIntervals.size();
		double[] values = new double[size];
		for(int index = 0; index < size; index++) {
			values[index] = scanIntervals.get(index);
		}
		int scanInterval = (int)Calculations.getMean(values);
		return scanInterval;
	}

	/**
	 * Calculates the mean scan interval.
	 * 
	 * @param content
	 * @return int
	 */
	private int calculateScanIntervalExtended(String content) {

		/*
		 * Key: Scan, Value: Retention Time
		 */
		Map<Integer, Double> scanRetentionTimes = new HashMap<Integer, Double>();
		Matcher matcher = NAME_PATTERN.matcher(content);
		while(matcher.find()) {
			/*
			 * Try to calculate a mean value using the scan number and its retention time.
			 * Though, it's not perfect but that's the only information we get from the file.
			 */
			try {
				int scan = Integer.parseInt(matcher.group(4));
				double retentionTime = Double.parseDouble(matcher.group(12)) * IChromatogram.MINUTE_CORRELATION_FACTOR;
				if(scan > 0) {
					scanRetentionTimes.put(scan, retentionTime);
				}
			} catch(NumberFormatException e) {
				//
			}
		}
		/*
		 * The scan interval must be scaled in milliseconds.
		 */
		List<Integer> scans = new ArrayList<Integer>(scanRetentionTimes.keySet());
		Collections.sort(scans);
		int scanIndexFirst = 0;
		int end = scans.size(); // Size of the list
		int start = end / 2;
		/*
		 * Start a calculation of scan intervals.
		 * The distance between the measured scans should be as big as possible
		 * to minimize calculation errors.
		 */
		List<Double> scanIntervals = new ArrayList<Double>();
		for(int scanIndexNext = start; scanIndexNext < end; scanIndexNext++) {
			/*
			 * Correlate two scans.
			 */
			int firstScan = scans.get(scanIndexFirst++);
			int nextScan = scans.get(scanIndexNext);
			double firstRetentionTime = scanRetentionTimes.get(firstScan);
			double nextRetentionTime = scanRetentionTimes.get(nextScan);
			int deltaScan = nextScan - firstScan;
			double deltaRetentionTime = nextRetentionTime - firstRetentionTime;
			if(deltaScan > 0) {
				double scanInterval = deltaRetentionTime / deltaScan;
				scanIntervals.add(scanInterval);
			}
		}
		//
		int size = scanIntervals.size();
		double[] values = new double[size];
		for(int index = 0; index < size; index++) {
			values[index] = scanIntervals.get(index);
		}
		int scanInterval = (int)Calculations.getMean(values);
		return scanInterval;
	}

	/**
	 * Tries to extract as much peaks as possible.
	 * 
	 * @param content
	 * @param scanInterval
	 * @return {@link IProcessingInfo}
	 */
	@SuppressWarnings("unchecked")
	private void extractAmdisPeaks(String content, int scanInterval, IProcessingInfo processingInfo) {

		if(scanInterval <= 0) {
			processingInfo.addErrorMessage("AMDIS ELU Parser", "There seems to be no peak in the file. The scan interval is <= 0.");
		} else {
			IPeaks peaks = new Peaks();
			Matcher matcher = PEAK_DATA_PATTERN.matcher(content);
			while(matcher.find()) {
				/*
				 * Extract each peak.
				 */
				String peakData = matcher.group();
				IPeakMSD peak = extractAmdisPeak(peakData, scanInterval);
				if(peak != null) {
					peaks.addPeak(peak);
				}
			}
			processingInfo.setProcessingResult(peaks);
		}
	}

	/**
	 * Extracts the peak.
	 * 
	 * @param peakData
	 * @param scanInterval
	 * @return {@link IPeakMSD}
	 */
	private IPeakMSD extractAmdisPeak(String peakData, int scanInterval) {

		/*
		 * Try to parse the peak data.
		 */
		IPeakMSD peak = null;
		int peakStartRetentionTime = extractPeakStartRetentionTime(peakData, scanInterval);
		if(peakStartRetentionTime > 0) {
			/*
			 * Only process peaks with a valid start retention time.
			 * Get the highest signal from the intensity values list. It is used to scale the mass spectrum.
			 * Afterwards, the intensity values need to be normalized.
			 */
			IPeakIntensityValues peakIntensityValues = extractPeakProfile(peakData, peakStartRetentionTime, scanInterval);
			float totalSignal = extractTotalSignal(peakIntensityValues);
			peakIntensityValues.normalize();
			IPeakMassSpectrum peakMassSpectrum = extractPeakMassSpectrum(peakData);
			peakMassSpectrum.adjustTotalSignal(totalSignal);
			/*
			 * Try to create a peak model. It fails of certain conditions are not matched.
			 */
			try {
				IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, 0.0f, 0.0f);
				extractScanRange(peakModel, peakData);
				peak = new PeakMSD(peakModel, "AMDIS ELU");
			} catch(IllegalArgumentException e) {
				logger.warn(e);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
		//
		return peak;
	}

	private float extractTotalSignal(IPeakIntensityValues peakIntensityValues) {

		float totalSignal = Float.MIN_VALUE;
		for(int retentionTime : peakIntensityValues.getRetentionTimes()) {
			float signal = peakIntensityValues.getIntensityValue(retentionTime).getValue();
			if(signal > totalSignal) {
				totalSignal = signal;
			}
		}
		//
		return totalSignal;
	}

	/**
	 * Calculates the peak start retention time.
	 * 
	 * @param peakData
	 * @return int
	 */
	private int extractPeakStartRetentionTime(String peakData, int scanInterval) {

		int startRetentionTime = 0;
		Matcher matcher = NAME_PATTERN.matcher(peakData);
		if(matcher.find()) {
			try {
				int scan = Integer.parseInt(matcher.group(4));
				int startScan = Integer.parseInt(matcher.group(7));
				double retentionTime = (int)(Double.parseDouble(matcher.group(12)) * IChromatogram.MINUTE_CORRELATION_FACTOR);
				/*
				 * Calculate the start retention time of the peak.
				 */
				startRetentionTime = (int)(retentionTime - ((scan - startScan - 1) * scanInterval));
			} catch(NumberFormatException e) {
				//
			}
		}
		return startRetentionTime;
	}

	private void extractScanRange(IPeakModelMSD peakModel, String peakData) {

		Matcher matcher = NAME_PATTERN.matcher(peakData);
		if(matcher.find()) {
			int maxScan = Integer.parseInt(matcher.group(4));
			int startScan = Integer.parseInt(matcher.group(7));
			int stopScan = Integer.parseInt(matcher.group(9));
			//
			peakModel.setTemporarilyInfo(TEMP_INFO_START_SCAN, startScan);
			peakModel.setTemporarilyInfo(TEMP_INFO_STOP_SCAN, stopScan);
			peakModel.setTemporarilyInfo(TEMP_INFO_MAX_SCAN, maxScan);
		}
	}

	private IPeakIntensityValues extractPeakProfile(String peakData, int peakStartRetentionTime, int scanInterval) {

		IPeakIntensityValues peakIntensityValues = new PeakIntensityValues(Float.MAX_VALUE);
		Matcher matcher = PROFILE_DATA_PATTERN.matcher(peakData);
		if(matcher.find()) {
			String profileData = matcher.group().replaceAll(NEW_LINE, " ").replaceAll(CARRIAGE_RETURN, " ");
			extractProfile(peakIntensityValues, profileData, peakStartRetentionTime, scanInterval);
		}
		//
		return peakIntensityValues;
	}

	private void extractProfile(IPeakIntensityValues peakIntensityValues, String profileData, int peakStartRetentionTime, int scanInterval) {

		int retentionTime = peakStartRetentionTime;
		Matcher matcher = PROFILE_PATTERN.matcher(profileData);
		while(matcher.find()) {
			try {
				float relativeIntensity = Float.parseFloat(matcher.group(1));
				peakIntensityValues.addIntensityValue(retentionTime, relativeIntensity);
			} catch(NumberFormatException e) {
				//
			}
			retentionTime += scanInterval;
		}
	}

	private IPeakMassSpectrum extractPeakMassSpectrum(String peakData) {

		IPeakMassSpectrum peakMassSpectrum = new PeakMassSpectrum();
		Matcher matcher = PEAK_PATTERN.matcher(peakData);
		if(matcher.find()) {
			String massSpectrumData = matcher.group().replaceAll(NEW_LINE, " ").replaceAll(CARRIAGE_RETURN, " ");
			extractPeakIons(massSpectrumData, peakMassSpectrum);
		}
		//
		return peakMassSpectrum;
	}

	private void extractPeakIons(String massSpectrumData, IPeakMassSpectrum peakMassSpectrum) {

		Matcher ions = ION_PATTERN.matcher(massSpectrumData);
		while(ions.find()) {
			try {
				if(PreferenceSupplier.isExcludeUncertainIons()) {
					if(ions.group(3) == null) {
						peakMassSpectrum.addIon(getPeakIonFromRegex(ions));
					}
				} else {
					peakMassSpectrum.addIon(getPeakIonFromRegex(ions));
				}
				/*
				 * TODO also parse uncertainty factor.
				 */
			} catch(NumberFormatException e) {
				logger.warn(e);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
	}

	private IPeakIon getPeakIonFromRegex(Matcher ions) throws AbundanceLimitExceededException, IonLimitExceededException {

		double ion = Double.parseDouble(ions.group(1));
		float abundance = Float.parseFloat(ions.group(2));
		IPeakIon peakIon = new PeakIon(ion, abundance);
		return peakIon;
	}
}
