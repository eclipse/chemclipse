/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.processing.CalculatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.processing.ICalculatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.ISupplierCalculatorSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.SeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnIndices;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class RetentionIndexCalculator {

	private static final Logger logger = Logger.getLogger(RetentionIndexCalculator.class);
	//
	private static final String HEADER_VALUE_DELIMITER = "=";
	private static final String RI_VALUE_DELIMITER = " ";
	private static final String FILE_EXTENSION = ".cal";
	//
	private static final String COLUMN_MARKER = "#";
	//
	private static final String COLUMN_NAME = "#COLUMN_NAME";
	private static final String COLUMN_LENGTH = "#COLUMN_LENGTH";
	private static final String COLUMN_DIAMETER = "#COLUMN_DIAMETER";
	private static final String COLUMN_PHASE = "#COLUMN_PHASE";

	public ICalculatorProcessingInfo apply(IChromatogramSelection chromatogramSelection, ISupplierCalculatorSettings supplierCalculatorSettings, IProgressMonitor monitor) {

		ICalculatorProcessingInfo processingInfo = new CalculatorProcessingInfo();
		List<String> retentionIndexFiles = supplierCalculatorSettings.getRetentionIndexFiles();
		/*
		 * Create a calibration map for different column polarities.
		 */
		Map<String, String> calibrationMap = new HashMap<String, String>();
		for(String retentionIndexFile : retentionIndexFiles) {
			File file = new File(retentionIndexFile);
			if(file.exists() && retentionIndexFile.toLowerCase().endsWith(FILE_EXTENSION)) {
				String name = file.getName();
				String key = name.substring(0, name.length() - 4);
				calibrationMap.put(key, retentionIndexFile);
			}
		}
		/*
		 * Use the miscellaneous info to auto-detect the column.
		 */
		String miscInfo = chromatogramSelection.getChromatogram().getMiscInfo();
		String pathRetentionIndexFile;
		if(calibrationMap.containsKey(miscInfo)) {
			pathRetentionIndexFile = calibrationMap.get(miscInfo);
		} else {
			if(retentionIndexFiles.size() > 0) {
				pathRetentionIndexFile = retentionIndexFiles.get(0);
			} else {
				pathRetentionIndexFile = "";
			}
		}
		/*
		 * Run the calculation.
		 */
		calculateIndex(chromatogramSelection, pathRetentionIndexFile);
		//
		return processingInfo;
	}

	public ISeparationColumnIndices getSeparationColumnIndices(File file) {

		ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
		//
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			/*
			 * Column Secification
			 */
			String name = "";
			String length = "";
			String diameter = "";
			String phase = "";
			//
			String line;
			while((line = bufferedReader.readLine()) != null) {
				/*
				 * 10.214 1600.0 100 981 Hexadecane
				 * see: AMDIS - User Guide
				 * https://www.nist.gov/sites/default/files/documents/srd/AMDISMan.pdf
				 */
				if(line.startsWith(COLUMN_MARKER)) {
					/*
					 * Column data
					 * #COLUMN_NAME=DB5
					 * ...
					 */
					if(line.startsWith(COLUMN_NAME)) {
						name = getValue(line);
					} else if(line.startsWith(COLUMN_LENGTH)) {
						length = getValue(line);
					} else if(line.startsWith(COLUMN_DIAMETER)) {
						diameter = getValue(line);
					} else if(line.startsWith(COLUMN_PHASE)) {
						phase = getValue(line);
					}
				} else {
					/*
					 * RI data
					 */
					try {
						String[] values = line.split(RI_VALUE_DELIMITER);
						if(values.length >= 5) {
							int retentionTime = (int)(Double.parseDouble(values[0]) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR); // Retention Time: 1.908
							float retentionIndex = Float.parseFloat(values[1]); // Retention Index: 600.0
							/*
							 * The folowing values are used by AMDIS but are not needed here.
							 */
							// values[2] ... Net - the computed Net value
							// values[3] ... S/N - the computed "S/N (total)" value
							/*
							 * It's assumed, that all other values belong to the peak name.
							 */
							String peakName = values[4].trim(); // C6
							for(int i = 5; i < values.length; i++) {
								peakName += " " + values[i];
							}
							//
							IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, peakName);
							separationColumnIndices.put(retentionTime, retentionIndexEntry);
						} else {
							throw new IOException("Unexpected column count: " + Arrays.asList(values).toString());
						}
					} catch(NumberFormatException e) {
						logger.warn(e);
					}
				}
			}
			/*
			 * Create and set the column.
			 */
			ISeparationColumn separationColumn = new SeparationColumn(name, length, diameter, phase);
			separationColumnIndices.setSeparationColumn(separationColumn);
			//
			bufferedReader.close();
		} catch(IOException e) {
			logger.error(e);
		}
		//
		return separationColumnIndices;
	}

	private String getValue(String line) {

		String value = "";
		String[] values = line.split(HEADER_VALUE_DELIMITER);
		if(values.length == 2) {
			value = values[1].trim();
		}
		return value;
	}

	public float calculateRetentionIndex(int retentionTime, ISeparationColumnIndices separationColumnIndices) {

		float retentionIndex = 0;
		Map.Entry<Integer, IRetentionIndexEntry> floorEntry = separationColumnIndices.floorEntry(retentionTime);
		Map.Entry<Integer, IRetentionIndexEntry> ceilingEntry = separationColumnIndices.ceilingEntry(retentionTime);
		/*
		 * Calculate the value if both entries exists.
		 * See AMDIS manual:
		 * RIcomp = RIlo + ( (RIhi - RIlo) * (RTact - RTlo) / (RThi - RTlo) )
		 */
		if(floorEntry != null && ceilingEntry != null) {
			/*
			 * Get the values.
			 */
			IRetentionIndexEntry floorIndex = floorEntry.getValue();
			IRetentionIndexEntry ceilingIndex = ceilingEntry.getValue();
			float retentionIndexLow = floorIndex.getRetentionIndex();
			int retentionTimeLow = floorIndex.getRetentionTime();
			float retentionIndexHigh = ceilingIndex.getRetentionIndex();
			int retentionTimeHigh = ceilingIndex.getRetentionTime();
			if(retentionTimeLow == retentionTimeHigh) {
				/*
				 * We are at an exact value, return simply the RI, otherwise it will fail at if(denominatorRT != 0)
				 */
				return retentionIndexLow;
			}
			/*
			 * Execute the calculation.
			 */
			float factorRetentionIndex = retentionIndexHigh - retentionIndexLow;
			float nominatorRT = retentionTime - retentionTimeLow;
			float denominatorRT = retentionTimeHigh - retentionTimeLow;
			if(denominatorRT != 0) {
				/*
				 * Calculate the retention index.
				 */
				retentionIndex = retentionIndexLow + (factorRetentionIndex * nominatorRT / denominatorRT);
			}
		}
		//
		return retentionIndex;
	}

	@SuppressWarnings("unchecked")
	private void calculateIndex(IChromatogramSelection chromatogramSelection, String pathRetentionIndexFile) {

		File calibrationFile = new File(pathRetentionIndexFile);
		if(calibrationFile.exists()) {
			ISeparationColumnIndices separationColumnIndices = getSeparationColumnIndices(calibrationFile);
			IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			int startScan = chromatogram.getScanNumber(startRetentionTime);
			int stopScan = chromatogram.getScanNumber(stopRetentionTime);
			/*
			 * Scans
			 */
			for(int scan = startScan; scan <= stopScan; scan++) {
				IScan supplierScan = chromatogram.getScan(scan);
				int retentionTime = supplierScan.getRetentionTime();
				float retentionIndex = calculateRetentionIndex(retentionTime, separationColumnIndices);
				supplierScan.setRetentionIndex(retentionIndex);
				/*
				 * Calculate RI also for the optimized MS.
				 */
				if(supplierScan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)supplierScan;
					IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
					if(optimizedMassSpectrum != null) {
						optimizedMassSpectrum.setRetentionIndex(retentionIndex);
					}
				}
			}
			/*
			 * Peaks
			 */
			List<? extends IPeak> peaks = chromatogram.getPeaks();
			for(IPeak peak : peaks) {
				IScan scan = peak.getPeakModel().getPeakMaximum();
				int retentionTime = scan.getRetentionTime();
				if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
					float retentionIndex = calculateRetentionIndex(retentionTime, separationColumnIndices);
					scan.setRetentionIndex(retentionIndex);
				}
			}
		}
	}
}
