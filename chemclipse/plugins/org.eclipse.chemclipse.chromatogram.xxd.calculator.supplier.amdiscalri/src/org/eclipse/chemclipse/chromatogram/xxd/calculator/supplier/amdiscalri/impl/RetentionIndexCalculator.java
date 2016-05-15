/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.processing.CalculatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.processing.ICalculatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.ISupplierCalculatorSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class RetentionIndexCalculator {

	private static final Logger logger = Logger.getLogger(RetentionIndexCalculator.class);
	private static final String DELIMITER = " ";

	public ICalculatorProcessingInfo apply(IChromatogramSelection chromatogramSelection, ISupplierCalculatorSettings supplierCalculatorSettings, IProgressMonitor monitor) {

		ICalculatorProcessingInfo processingInfo = new CalculatorProcessingInfo();
		String pathRetentionIndexFile = supplierCalculatorSettings.getPathRetentionIndexFile();
		File calibrationFile = new File(pathRetentionIndexFile);
		TreeMap<Integer, IRetentionIndexEntry> retentionIndices = getRetentionIndexEntries(calibrationFile);
		//
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
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
			float retentionIndex = calculateRetentionIndex(retentionTime, retentionIndices);
			supplierScan.setRetentionIndex(retentionIndex);
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
		if(chromatogram instanceof IChromatogramMSD) {
			IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
			for(IChromatogramPeakMSD peak : chromatogramMSD.getPeaks()) {
				IScan scan = peak.getPeakModel().getPeakMaximum();
				int retentionTime = scan.getRetentionTime();
				if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
					float retentionIndex = calculateRetentionIndex(retentionTime, retentionIndices);
					scan.setRetentionIndex(retentionIndex);
				}
			}
		} else if(chromatogram instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
			for(IChromatogramPeakCSD peak : chromatogramCSD.getPeaks()) {
				IScan scan = peak.getPeakModel().getPeakMaximum();
				int retentionTime = scan.getRetentionTime();
				if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
					float retentionIndex = calculateRetentionIndex(retentionTime, retentionIndices);
					scan.setRetentionIndex(retentionIndex);
				}
			}
		}
		//
		return processingInfo;
	}

	public TreeMap<Integer, IRetentionIndexEntry> getRetentionIndexEntries(File file) {

		TreeMap<Integer, IRetentionIndexEntry> retentionIndices = new TreeMap<Integer, IRetentionIndexEntry>();
		/*
		 * TODO: Cache the tree map so that it don't need to be reloaded.
		 */
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String line;
			while((line = bufferedReader.readLine()) != null) {
				/*
				 * 10.214 1600.0 100 981 Hexadecane
				 */
				try {
					String[] values = line.split(DELIMITER);
					if(values.length >= 5) {
						int retentionTime = (int)(Double.parseDouble(values[0]) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
						float retentionIndex = Float.parseFloat(values[1]);
						String peakName = values[4].trim();
						IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, peakName);
						retentionIndices.put(retentionTime, retentionIndexEntry);
					}
				} catch(NumberFormatException e) {
					logger.warn(e);
				}
			}
			bufferedReader.close();
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
		//
		return retentionIndices;
	}

	public float calculateRetentionIndex(int retentionTime, TreeMap<Integer, IRetentionIndexEntry> retentionIndices) {

		float retentionIndex = 0;
		Map.Entry<Integer, IRetentionIndexEntry> floorEntry = retentionIndices.floorEntry(retentionTime);
		Map.Entry<Integer, IRetentionIndexEntry> ceilingEntry = retentionIndices.ceilingEntry(retentionTime);
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
}
