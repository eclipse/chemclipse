/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.converter.io.IPeakReader;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IPeakIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.PeaksMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakIon;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ELUReader implements IPeakReader {

	/*
	 * We could try to add additional filter options in this class.
	 */
	private static final Logger logger = Logger.getLogger(ELUReader.class);
	//
	private static final Pattern HEADER_PATTERN = Pattern.compile("(NAME.*)");
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
	public IProcessingInfo<IPeaks<IPeakMSD>> read(File file, IProgressMonitor monitor) throws IOException {

		Charset charset = PreferenceSupplier.getCharsetImportELU();
		IProcessingInfo<IPeaks<IPeakMSD>> processingInfo = new ProcessingInfo<>();
		String content = FileUtils.readFileToString(file, charset);
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

		List<Double> scanIntervals = new ArrayList<>();
		Matcher matcher = NAME_PATTERN.matcher(content);
		while(matcher.find()) {
			/*
			 * Try to calculate a mean value using the scan number and its retention time.
			 * Though, it's not perfect but that's the only information we get from the file.
			 */
			try {
				int scan = Integer.parseInt(matcher.group(4));
				double retentionTime = Double.parseDouble(matcher.group(12)) * IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
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
		return (int)Calculations.getMean(values);
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
		Map<Integer, Double> scanRetentionTimes = new HashMap<>();
		Matcher matcher = NAME_PATTERN.matcher(content);
		while(matcher.find()) {
			/*
			 * Try to calculate a mean value using the scan number and its retention time.
			 * Though, it's not perfect but that's the only information we get from the file.
			 */
			try {
				int scan = Integer.parseInt(matcher.group(4));
				double retentionTime = Double.parseDouble(matcher.group(12)) * IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
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
		List<Integer> scans = new ArrayList<>(scanRetentionTimes.keySet());
		Collections.sort(scans);
		int scanIndexFirst = 0;
		int end = scans.size(); // Size of the list
		int start = end / 2;
		/*
		 * Start a calculation of scan intervals.
		 * The distance between the measured scans should be as big as possible
		 * to minimize calculation errors.
		 */
		List<Double> scanIntervals = new ArrayList<>();
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
		return (int)Calculations.getMean(values);
	}

	/**
	 * Tries to extract as much peaks as possible.
	 * 
	 * @param content
	 * @param scanInterval
	 * @return {@link IProcessingInfo}
	 */
	private void extractAmdisPeaks(String content, int scanInterval, IProcessingInfo<IPeaks<IPeakMSD>> processingInfo) {

		if(scanInterval <= 0) {
			processingInfo.addErrorMessage("AMDIS ELU Parser", "There seems to be no peak in the file. The scan interval is <= 0.");
		} else {
			IPeaks<IPeakMSD> peaks = new PeaksMSD();
			Matcher matcher = PEAK_DATA_PATTERN.matcher(content);
			while(matcher.find()) {
				/*
				 * NAME: |SC3|CN1|MP1-MODN:185(%57.6)|AM44322|PC25|SN154|WD>18|TA6.8|TR9.5|FR0-26|RT1.6150|MN0.17|RA0.142|IS482918|XN598097|MO6: 185 276 195 284 59 237|EW0-1|FG0.822|TN2.408|OR2|NT0
				 * RE
				 * 36347 55021 61507 45358 31167
				 * 26292 22722 20328 14626 13494
				 * ...
				 * NUM PEAKS: 103
				 * (59,18 )(60,51 )(61,999 )(62,25 )(70,949 )
				 * (74,17 )(87,28 )(88,302 )(185,4 )(276,3 )
				 * (321,2 )(349,1 )(355,5 )(487,2 )(71,35 B0.0)
				 * ...
				 */
				String peakData = matcher.group();
				IPeakMSD peak = extractPeak(peakData, scanInterval);
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
	private IPeakMSD extractPeak(String peakData, int scanInterval) {

		IPeakMSD peak = null;
		//
		String header = extractHeader(peakData, HEADER_PATTERN, "");
		int peakStartRetentionTime = extractPeakStartRetentionTime(peakData, scanInterval);
		//
		if(peakStartRetentionTime > 0) {
			try {
				/*
				 * Try to create a peak model. It fails of certain conditions are not matched.
				 * Only process peaks with a valid start retention time.
				 * Get the highest signal from the intensity values list. It is used to scale the mass spectrum.
				 * Afterwards, the intensity values need to be normalized.
				 */
				IPeakIntensityValues peakIntensityValues = extractPeakProfile(peakData, peakStartRetentionTime, scanInterval);
				float totalSignal = extractTotalSignal(peakIntensityValues);
				peakIntensityValues.normalize();
				IPeakMassSpectrum peakMassSpectrum = extractPeakMassSpectrum(peakData);
				peakMassSpectrum.adjustTotalSignal(totalSignal);
				IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, 0.0f, 0.0f);
				extractScanRange(peakModel, peakData);
				peak = new PeakMSD(peakModel, "AMDIS (ELU)");
				peak.setTemporaryData(header);
			} catch(Exception e) {
				logger.warn("PeakModel fails for AMDIS (ELU) component: " + header);
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
				/*
				 * Calculate the start retention time of the peak.
				 */
				int scan = Integer.parseInt(matcher.group(4));
				int startScan = Integer.parseInt(matcher.group(7));
				double retentionTime = (int)(Double.parseDouble(matcher.group(12)) * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				startRetentionTime = (int)(retentionTime - ((scan - startScan - 1) * scanInterval));
			} catch(NumberFormatException e) {
				// No warning, default will be returned.
			}
		}
		return startRetentionTime;
	}

	private String extractHeader(String peakData, Pattern pattern, String defaultValue) {

		String value = defaultValue;
		Matcher matcher = pattern.matcher(peakData);
		if(matcher.find()) {
			value = matcher.group();
		}
		//
		return value;
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
			String profileData = matcher.group().replace(NEW_LINE, " ").replace(CARRIAGE_RETURN, " ");
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
			String massSpectrumData = matcher.group().replace(NEW_LINE, " ").replace(CARRIAGE_RETURN, " ");
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
			}
		}
	}

	private IPeakIon getPeakIonFromRegex(Matcher ions) {

		double ion = Double.parseDouble(ions.group(1));
		float abundance = Float.parseFloat(ions.group(2));
		return new PeakIon(ion, abundance);
	}
}
