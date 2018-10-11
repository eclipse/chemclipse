/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.smoothed.core;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.AbstractBaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.smoothed.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.smoothed.settings.DetectorSettings;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class BaselineDetector extends AbstractBaselineDetector {

	private static WindowSize WINDOW_SIZE = WindowSize.WIDTH_7;

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(chromatogramSelection, baselineDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(baselineDetectorSettings instanceof DetectorSettings) {
				calculateBaseline(chromatogramSelection);
			}
		}
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		DetectorSettings detectorSettings = PreferenceSupplier.getDetectorSettings();
		return setBaseline(chromatogramSelection, detectorSettings, monitor);
	}

	/**
	 * Calculates the baseline.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void calculateBaseline(IChromatogramSelection chromatogramSelection) {

		ITotalScanSignal totalIonSignal;
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		IScanRange scanRange = new ScanRange(startScan, stopScan);
		/*
		 * If the scan range is lower than the given window size, do nothing.
		 */
		if(scanRange.getWidth() <= WINDOW_SIZE.getSize()) {
			return;
		}
		/*
		 * Determine how many scans should be involved in the consecutive window
		 * range.<br/> The used scan range should be equivalent to 150 scans
		 * with a scan interval of 500ms.
		 */
		// TODO als Parameter? (Settings aufbauen?)
		int windowScanRange = calculateWindowScanRange(chromatogram.getScanInterval(), 150, 500);
		ITotalScanSignalExtractor totalScanSignalExtractor;
		try {
			totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogram);
		} catch(ChromatogramIsNullException e) {
			return;
		}
		ITotalScanSignals totalIonSignals = totalScanSignalExtractor.getTotalScanSignals(startScan, stopScan);
		/*
		 * Iterates through all scans and sets calculates the lowest scan value
		 * for each window scan range.
		 */
		float baselineTotalSignal;
		for(int scan = startScan; scan <= stopScan; scan++) {
			totalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			baselineTotalSignal = getMinTotalSignal(scan, scan + windowScanRange, totalIonSignals);
			totalIonSignal.setTotalSignal(baselineTotalSignal);
		}
		TotalScanSignalsModifier.calculateMovingAverage(totalIonSignals, WINDOW_SIZE);
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		/*
		 * Applies the baseline.
		 */
		applyBaseline(baselineModel, totalIonSignals, startScan, stopScan);
	}

	/**
	 * Sets the baseline.
	 * 
	 * @param baselineModel
	 */
	private void applyBaseline(IBaselineModel baselineModel, ITotalScanSignals totalIonSignals, int startScan, int stopScan) {

		ITotalScanSignal actualTotalIonSignal;
		ITotalScanSignal nextTotalIonSignal;
		// int numberOfScans = totalIonSignals.size();
		int startRetentionTime;
		int stopRetentionTime;
		float startBackgroundAbundance;
		float stopBackgroundAbundance;
		/*
		 * Why scan < numberOfScans instead of scan <= numberOfScans? Because of
		 * .getNextTotalIonSignal();
		 */
		for(int scan = startScan; scan < stopScan; scan++) {
			actualTotalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			nextTotalIonSignal = totalIonSignals.getNextTotalScanSignal(scan);
			/*
			 * Retention times and background abundances.
			 */
			startRetentionTime = actualTotalIonSignal.getRetentionTime();
			startBackgroundAbundance = actualTotalIonSignal.getTotalSignal();
			stopRetentionTime = nextTotalIonSignal.getRetentionTime();
			stopBackgroundAbundance = nextTotalIonSignal.getTotalSignal();
			/*
			 * Set the baseline.
			 * It is validate == false, cause we know that the segments are calculated without overlap.
			 */
			baselineModel.addBaseline(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance, false);
		}
	}

	/**
	 * If a scan is not existant in the total ion signals list, the total signal
	 * value will be presumed to be 0.
	 * 
	 * @param startScan
	 * @param stopScan
	 * @param totalIonSignals
	 * @return float
	 */
	private float getMinTotalSignal(int startScan, int stopScan, ITotalScanSignals totalIonSignals) {

		ITotalScanSignal totalIonSignal;
		int count = stopScan - startScan + 1;
		float[] values = new float[count];
		/*
		 * Iterates through the scan range and retrieve the total signals.<br/>
		 * If a scan is not present, the value will be by default 0.
		 */
		for(int scan = startScan, i = 0; scan <= stopScan; scan++, i++) {
			totalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			if(totalIonSignal != null) {
				values[i] = totalIonSignal.getTotalSignal();
			}
		}
		return Calculations.getMin(values);
	}

	/**
	 * Calculates a window range that is equivalent to 150 scan à 500ms based on
	 * the given respectively actual scan interval if given (..., 150, 500).<br/>
	 * E.g., if a chromatogram was measured with 2 scans / minute the window
	 * range is less than as if the chromatogram was measured wth 4 scans /
	 * minute.
	 * 
	 * @param actualScanInterval
	 * @param scans
	 * @param scanInterval
	 * @return int
	 */
	private int calculateWindowScanRange(int actualScanInterval, int scans, int scanInterval) {

		if(actualScanInterval <= 0) {
			return 0;
		}
		/*
		 * E.g. 150 scans à 500ms (scan interval).
		 */
		int window = scans * scanInterval;
		return window / actualScanInterval;
	}
}
