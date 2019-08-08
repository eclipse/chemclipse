/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Christoph Läubrich - extract common methods to base class
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.PeakBuilderMSD;
import org.eclipse.chemclipse.msd.model.xic.ITotalIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.TotalIonSignalExtractor;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetectorMSD extends BasePeakDetector implements IPeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorMSD.class);
	//
	private static final String DETECTOR_DESCRIPTION = "Peak Detector First Derivative";

	@Override
	public IProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorSettingsMSD detectorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, detectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(detectorSettings instanceof PeakDetectorSettingsMSD) {
				PeakDetectorSettingsMSD peakDetectorSettings = (PeakDetectorSettingsMSD)detectorSettings;
				detectPeaks(chromatogramSelection, peakDetectorSettings, monitor);
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Peak Detector First Derivative", "Peaks have been detected successfully."));
			} else {
				logger.warn("Settings is not of type: " + PeakDetectorSettingsMSD.class);
			}
		}
		return processingInfo;
	}

	private IPeakDetectorSettingsMSD peakDetectorSettings;

	public IPeakDetectorSettingsMSD getPeakDetectorSettings() {

		return peakDetectorSettings;
	}

	public PeakDetectorMSD setPeakDetectorSettings(IPeakDetectorSettingsMSD peakDetectorSettings) {

		this.peakDetectorSettings = peakDetectorSettings;
		return this;
	}

	// TODO JUnit
	@Override
	public IProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		if(peakDetectorSettings == null)
			peakDetectorSettings = PreferenceSupplier.getPeakDetectorSettingsMSD();
		return detect(chromatogramSelection, peakDetectorSettings, monitor);
	}

	/**
	 * Detect the peaks in the selection and add them to the chromatogram.
	 * 
	 * @param chromatogramSelection
	 * @throws ValueMustNotBeNullException
	 */
	private void detectPeaks(IChromatogramSelectionMSD chromatogramSelection, PeakDetectorSettingsMSD peakDetectorSettings, IProgressMonitor monitor) {

		IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, peakDetectorSettings.getMovingAverageWindowSize());
		List<IRawPeak> rawPeaks = getRawPeaks(slopes, peakDetectorSettings.getThreshold(), monitor);
		buildAndStorePeaks(rawPeaks, chromatogramSelection.getChromatogramMSD(), peakDetectorSettings);
	}

	/**
	 * Builds from each raw peak a valid {@link IChromatogramPeakMSD} and adds it to the
	 * chromatogram.
	 * 
	 * @param rawPeaks
	 * @param chromatogram
	 */
	private void buildAndStorePeaks(List<IRawPeak> rawPeaks, IChromatogramMSD chromatogram, PeakDetectorSettingsMSD peakDetectorSettings) {

		IChromatogramPeakMSD peak = null;
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
				peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, peakDetectorSettings.isIncludeBackground());
				/*
				 * TODO Resolve, why this peak does throw an exception. When
				 * detecting peaks in the chromatogram OP17760.D/DATA.MS a
				 * PeakException occurs:<br/> Threshold.OFF : peak number 191
				 * ScanRange[startScan=4636,stopScan=4646]<br/> Threshold.LOW :
				 * peak number 173 ScanRange[startScan=4636,stopScan=4646]<br/>
				 * The first scan is the peak maximum, as it seems, so no
				 * inflection point equation could be calculated.
				 */
				if(isValidPeak(peak, peakDetectorSettings)) {
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
	 * @param windowSize
	 * @return {@link IFirstDerivativeDetectorSlopes}
	 */
	public static IFirstDerivativeDetectorSlopes getFirstDerivativeSlopes(IChromatogramSelectionMSD chromatogramSelection, WindowSize windowSize) {

		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		try {
			ITotalIonSignalExtractor totalIonSignalExtractor = new TotalIonSignalExtractor(chromatogram);
			ITotalScanSignals signals = totalIonSignalExtractor.getTotalIonSignals(chromatogramSelection);
			TotalScanSignalsModifier.normalize(signals, NORMALIZATION_BASE);
			/*
			 * Get the start and stop scan of the chromatogram selection.
			 */
			int startScan = signals.getStartScan();
			int stopScan = signals.getStopScan();
			IFirstDerivativeDetectorSlopes slopes = new FirstDerivativeDetectorSlopes(signals);
			/*
			 * Fill the slope list.
			 */
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
			slopes.calculateMovingAverage(windowSize);
			return slopes;
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
			return null;
		}
	}

	/**
	 * Checks that the peak is not null and that it matches
	 * the min S/N requirements.
	 * 
	 * @param peak
	 * @return boolean
	 */
	private boolean isValidPeak(IChromatogramPeakMSD peak, PeakDetectorSettingsMSD peakDetectorSettings) {

		if(peak != null && peak.getSignalToNoiseRatio() >= peakDetectorSettings.getMinimumSignalToNoiseRatio()) {
			return true;
		}
		return false;
	}
}
