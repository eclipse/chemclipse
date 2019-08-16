/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extract common methods to base class
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.IPeakDetectorWSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.IPeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.PeakBuilderWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetectorWSD extends BasePeakDetector implements IPeakDetectorWSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorWSD.class);
	//
	private static final String DETECTOR_DESCRIPTION = "Peak Detector First Derivative";
	//
	private static float NORMALIZATION_BASE = 100000.0f;
	private static int CONSECUTIVE_SCAN_STEPS = 3;

	@Override
	public IProcessingInfo detect(IChromatogramSelectionWSD chromatogramSelection, IPeakDetectorSettingsWSD detectorSettings, IProgressMonitor monitor) {

		/*
		 * Check whether the chromatogram selection is null or not.
		 */
		IProcessingInfo processingInfo = validate(chromatogramSelection, detectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(detectorSettings instanceof PeakDetectorSettingsWSD) {
				PeakDetectorSettingsWSD peakDetectorSettings = (PeakDetectorSettingsWSD)detectorSettings;
				detectPeaks(chromatogramSelection, peakDetectorSettings, monitor);
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Peak Detector First Derivative", "Peaks have been detected successfully."));
			} else {
				logger.warn("Settings is not of type: " + PeakDetectorSettingsWSD.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo detect(IChromatogramSelectionWSD chromatogramSelection, IProgressMonitor monitor) {

		PeakDetectorSettingsWSD peakDetectorSettings = PreferenceSupplier.getPeakDetectorSettingsWSD();
		return detect(chromatogramSelection, peakDetectorSettings, monitor);
	}

	/**
	 * Detect the peaks in the selection and add them to the chromatogram.
	 * 
	 * @param chromatogramSelection
	 * @throws ValueMustNotBeNullException
	 */
	private void detectPeaks(IChromatogramSelectionWSD chromatogramSelection, PeakDetectorSettingsWSD peakDetectorSettings, IProgressMonitor monitor) {

		IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, peakDetectorSettings.getMovingAverageWindowSize());
		List<IRawPeak> rawPeaks = getRawPeaks(slopes, peakDetectorSettings.getThreshold(), monitor);
		buildAndStorePeaks(rawPeaks, chromatogramSelection.getChromatogram(), peakDetectorSettings);
	}

	/**
	 * Builds from each raw peak a valid {@link IChromatogramPeakMSD} and adds it to the
	 * chromatogram.
	 * 
	 * @param rawPeaks
	 * @param chromatogram
	 */
	private void buildAndStorePeaks(List<IRawPeak> rawPeaks, IChromatogramWSD chromatogram, PeakDetectorSettingsWSD peakDetectorSettings) {

		IChromatogramPeakWSD peak = null;
		IScanRange scanRange = null;
		for(IRawPeak rawPeak : rawPeaks) {
			/*
			 * Build the peak and add it.
			 */
			try {
				scanRange = new ScanRange(rawPeak.getStartScan(), rawPeak.getStopScan());
				/*
				 * includeBackground
				 * false: BV or VB
				 * true: VV
				 */
				peak = PeakBuilderWSD.createPeak(chromatogram, scanRange, peakDetectorSettings.isIncludeBackground());
				if(isValidPeak(peak)) {
					/*
					 * Add the detector description. Add the peak to the
					 * chromatogram.
					 */
					peak.setDetectorDescription(DETECTOR_DESCRIPTION);
					chromatogram.addPeak(peak);
				}
			} catch(IllegalArgumentException e) {
				logger.warn(e);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * Initializes the slope values.
	 * 
	 * @param chromatogramSelection
	 * @param window
	 * @return {@link IFirstDerivativeDetectorSlopes}
	 */
	public static IFirstDerivativeDetectorSlopes getFirstDerivativeSlopes(IChromatogramSelectionWSD chromatogramSelection, WindowSize window) {

		ITotalScanSignals signals = new TotalScanSignals(chromatogramSelection);
		TotalScanSignalsModifier.normalize(signals, NORMALIZATION_BASE);
		/*
		 * Get the start and stop scan of the chromatogram selection.
		 */
		IFirstDerivativeDetectorSlopes slopes = new FirstDerivativeDetectorSlopes(signals);
		/*
		 * Fill the slope list.
		 */
		int startScan = signals.getStartScan();
		int stopScan = signals.getStopScan();
		//
		for(int scan = startScan; scan < stopScan; scan++) {
			ITotalScanSignal s1 = signals.getTotalScanSignal(scan);
			ITotalScanSignal s2 = signals.getNextTotalScanSignal(scan);
			if(s1 != null && s2 != null) {
				IPoint p1 = new Point(s1.getRetentionTime(), s1.getTotalSignal());
				IPoint p2 = new Point(s2.getRetentionTime(), s2.getTotalSignal());
				IFirstDerivativeDetectorSlope slope = new FirstDerivativeDetectorSlope(p1, p2, s1.getRetentionTime());
				slopes.add(slope);
			}
		}
		slopes.calculateMovingAverage(window);
		return slopes;
	}

	/**
	 * Checks that the peak is not null and that it matches
	 * the min S/N requirements.
	 * 
	 * @param peak
	 * @return boolean
	 */
	private boolean isValidPeak(IChromatogramPeakWSD peak) {

		/*
		 * Noise calculation needs to be adjusted for WSD chromatograms.
		 */
		if(peak != null) { // && peak.getSignalToNoiseRatio() >= minimumSignalToNoiseRatio
			return true;
		}
		return false;
	}
}
