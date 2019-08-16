/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.exceptions.NoExtractedWavelengthSignalStoredException;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanWSD;

public class ExtractedWavelengthSignals implements IExtractedWavelengthSignals {

	private static final Logger logger = Logger.getLogger(ExtractedWavelengthSignals.class);
	private List<IExtractedWavelengthSignal> signals;
	private int startWavelength = Integer.MAX_VALUE;
	private int stopWavelength = 0;
	private int startScan = 0;
	private int stopScan = 0;
	private IChromatogramWSD chromatogram = null;

	public ExtractedWavelengthSignals(int numberOfScans) {
		if(numberOfScans <= 0) {
			numberOfScans = 0;
			startScan = 0;
			stopScan = 0;
		} else {
			startScan = 1;
			stopScan = numberOfScans;
		}
		signals = new ArrayList<IExtractedWavelengthSignal>(numberOfScans);
	}

	public ExtractedWavelengthSignals(int numberOfScans, IChromatogramWSD chromatogram) {
		this(numberOfScans);
		this.chromatogram = chromatogram;
	}

	/**
	 * Creates a {@link IExtractedWavelengthSignals} instance.<br/>
	 * The start and stop scan needs also to be specified, if e.g. only a
	 * selection from scan 40 - 60 is chosen.
	 * 
	 * @param startScan
	 * @param stopScan
	 */
	public ExtractedWavelengthSignals(int startScan, int stopScan) {
		startScan = (startScan <= 0) ? 0 : startScan;
		stopScan = (stopScan <= 0) ? 0 : stopScan;
		int start = Math.min(startScan, stopScan);
		int stop = Math.max(startScan, stopScan);
		int numberOfScans;
		if(start == 0 || stop == 0) {
			numberOfScans = 0;
			start = 0;
			stop = 0;
		} else {
			numberOfScans = stop - start + 1;
		}
		signals = new ArrayList<IExtractedWavelengthSignal>(numberOfScans);
		this.startScan = start;
		this.stopScan = stop;
	}

	public ExtractedWavelengthSignals(int startScan, int stopScan, IChromatogramWSD chromatogram) {
		this(startScan, stopScan);
		this.chromatogram = chromatogram;
	}

	@Override
	public IChromatogramWSD getChromatogram() {

		return chromatogram;
	}

	@Override
	public void add(IExtractedWavelengthSignal extractedWavelengthSignal) {

		signals.add(extractedWavelengthSignal);
		setStartWavelength(extractedWavelengthSignal.getStartWavelength());
		setStopWavelength(extractedWavelengthSignal.getStopWavelength());
	}

	@Override
	public void add(int wavlength, float abundance, int retentionTime, boolean removePreviousAbundance) {

		IExtractedWavelengthSignal extractedWavelengthSignal;
		try {
			int scan = findClosestScan(retentionTime);
			if(scan > 0) {
				extractedWavelengthSignal = getExtractedWavelengthSignal(scan);
				extractedWavelengthSignal.setAbundance(wavlength, abundance, removePreviousAbundance);
			}
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		} catch(NoExtractedWavelengthSignalStoredException e) {
			logger.warn(e);
		}
	}

	@Override
	public IExtractedWavelengthSignal getExtractedWavelengthSignal(int scan) throws NoExtractedWavelengthSignalStoredException {

		if(scan <= 0) {
			throw new NoExtractedWavelengthSignalStoredException("The requested scan: " + scan + " is not available.");
		}
		if(scan < startScan || scan > stopScan) {
			throw new NoExtractedWavelengthSignalStoredException("The requested scan: " + scan + " is not available.");
		}
		int correction = startScan - 1;
		scan -= correction;
		return signals.get(--scan);
	}

	// TODO JUnit
	@Override
	public List<IExtractedWavelengthSignal> getExtractedWavelengthSignals() {

		return signals;
	}

	@Override
	public IScanWSD getScan(int scan) {

		IExtractedWavelengthSignal extractedWavelengthSignal;
		/*
		 * Retrieve the extracted ion signal.
		 */
		try {
			extractedWavelengthSignal = getExtractedWavelengthSignal(scan);
		} catch(NoExtractedWavelengthSignalStoredException e) {
			logger.warn(e);
			return null;
		}
		//
		IScanWSD scanWSD = new ScanWSD();
		scanWSD.setParentChromatogram(getChromatogram());
		IScanSignalWSD scanSignalWSD;
		float abundance;
		int startWavelength = extractedWavelengthSignal.getStartWavelength();
		int stopWavelength = extractedWavelengthSignal.getStopWavelength();
		//
		for(int wavelength = startWavelength; wavelength <= stopWavelength; wavelength++) {
			abundance = extractedWavelengthSignal.getAbundance(wavelength);
			if(abundance > 0.0f) {
				scanSignalWSD = new ScanSignalWSD(wavelength, abundance);
				scanWSD.addScanSignal(scanSignalWSD);
			}
		}
		return scanWSD;
	}

	@Override
	public int getStartWavelength() {

		return startWavelength;
	}

	@Override
	public int getStopWavelength() {

		return stopWavelength;
	}

	@Override
	public int size() {

		return signals.size();
	}

	@Override
	public int getStartScan() {

		return startScan;
	}

	@Override
	public int getStopScan() {

		return stopScan;
	}

	@Override
	public ITotalScanSignals getTotalWavelengthSignals(int wavelength) {

		IScanRange scanRange = new ScanRange(getStartScan(), getStopScan());
		return getTotalWavelengthSignals(wavelength, scanRange);
	}

	@Override
	public ITotalScanSignals getTotalWavelengthSignals() {

		IScanRange scanRange = new ScanRange(getStartScan(), getStopScan());
		return getTotalWavelengthSignals((int)ISignal.TOTAL_INTENSITY, scanRange);
	}

	@Override
	public ITotalScanSignals getTotalWavelengthSignals(IScanRange scanRange) {

		return getTotalWavelengthSignals((int)ISignal.TOTAL_INTENSITY, scanRange);
	}

	@Override
	public ITotalScanSignals getTotalWavelengthSignals(int wavelength, IScanRange scanRange) {

		int startScan;
		int stopScan;
		if(scanRange != null) {
			startScan = scanRange.getStartScan();
			stopScan = scanRange.getStopScan();
		} else {
			startScan = 0;
			stopScan = 0;
		}
		IExtractedWavelengthSignal extractedWavelengthSignal;
		ITotalScanSignal totalWavelengthSignal;
		ITotalScanSignals totalIonSignals = new TotalScanSignals(startScan, stopScan, getChromatogram());
		int retentionTime;
		float retentionIndex;
		float signal;
		for(int scan = startScan; scan <= stopScan; scan++) {
			try {
				extractedWavelengthSignal = getExtractedWavelengthSignal(scan);
				retentionTime = extractedWavelengthSignal.getRetentionTime();
				retentionIndex = extractedWavelengthSignal.getRetentionIndex();
				// TIC
				if(wavelength == (int)ISignal.TOTAL_INTENSITY) {
					signal = extractedWavelengthSignal.getTotalSignal();
				} else { // XIC
					signal = extractedWavelengthSignal.getAbundance(wavelength);
				}
				/*
				 * Add the total ion signal to the total ion signal list.
				 */
				totalWavelengthSignal = new TotalScanSignal(retentionTime, retentionIndex, signal);
				totalIonSignals.add(totalWavelengthSignal);
			} catch(NoExtractedWavelengthSignalStoredException e) {
				logger.warn(e);
			}
		}
		return totalIonSignals;
	}

	@Override
	public IExtractedWavelengthSignals makeDeepCopyWithoutSignals() {

		IExtractedWavelengthSignals extractedWavelengthSignals = new ExtractedWavelengthSignals(startScan, stopScan, chromatogram);
		IExtractedWavelengthSignal extractedWavelengthSignal;
		//
		for(IExtractedWavelengthSignal signal : signals) {
			extractedWavelengthSignal = new ExtractedWavelengthSignal(signal.getStartWavelength(), signal.getStopWavelength());
			extractedWavelengthSignal.setRetentionTime(signal.getRetentionTime());
			extractedWavelengthSignal.setRetentionIndex(signal.getRetentionIndex());
			extractedWavelengthSignals.add(extractedWavelengthSignal);
		}
		return extractedWavelengthSignals;
	}

	private void setStartWavelength(int wavelength) {

		if(wavelength < startWavelength) {
			startWavelength = wavelength;
		}
	}

	private void setStopWavelength(int wavelength) {

		if(wavelength > stopWavelength) {
			stopWavelength = wavelength;
		}
	}

	@Override
	public Set<Integer> getUsedWavelenghts() {

		Set<Integer> usedWavelenghts = new TreeSet<Integer>();
		for(IExtractedWavelengthSignal signal : signals) {
			for(int wavelength = signal.getStartWavelength(); wavelength <= signal.getStopWavelength(); wavelength++) {
				if(signal.getAbundance(wavelength) != 0) {
					usedWavelenghts.add(wavelength);
				}
			}
		}
		return usedWavelenghts;
	}

	/**
	 * Returns the closest scan to the given retention time.
	 * 
	 * @param retentionTime
	 * @return int
	 */
	private int findClosestScan(int retentionTime) throws ChromatogramIsNullException {

		if(chromatogram == null) {
			throw new ChromatogramIsNullException();
		}
		return chromatogram.getScanNumber(retentionTime);
	}
}
