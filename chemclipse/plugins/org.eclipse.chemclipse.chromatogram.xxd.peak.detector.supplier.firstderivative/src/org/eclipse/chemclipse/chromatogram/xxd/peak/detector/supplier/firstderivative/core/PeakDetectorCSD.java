/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.IPeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.support.PeakBuilderCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.NoiseSegment;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class PeakDetectorCSD extends BasePeakDetector implements IPeakDetectorCSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorCSD.class);

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionCSD chromatogramSelection, IPeakDetectorSettingsCSD detectorSettings, IProgressMonitor monitor) {

		/*
		 * Check whether the chromatogram selection is null or not.
		 */
		IProcessingInfo processingInfo = validate(chromatogramSelection, detectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(detectorSettings instanceof PeakDetectorSettingsCSD) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				PeakDetectorSettingsCSD peakDetectorSettings = (PeakDetectorSettingsCSD)detectorSettings;
				IChromatogramCSD chromatogram = chromatogramSelection.getChromatogram();
				/*
				 * Extract the noise segments.
				 */
				List<NoiseSegment> noiseSegments = null;
				if(peakDetectorSettings.isUseNoiseSegments()) {
					noiseSegments = NoiseChromatogramClassifier.getNoiseSegments(chromatogram, chromatogramSelection, false, subMonitor.split(10));
				}
				/*
				 * Detect and add the peaks.
				 */
				List<IChromatogramPeakCSD> peaks = detectPeaks(chromatogramSelection, peakDetectorSettings, noiseSegments, subMonitor.split(90));
				for(IChromatogramPeakCSD peak : peaks) {
					chromatogram.addPeak(peak);
				}
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, FirstDerivativePeakDetector.DETECTOR_DESCRIPTION, "Peaks have been detected successfully."));
			} else {
				logger.warn("Settings is not of type: " + PeakDetectorSettingsCSD.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionCSD chromatogramSelection, IProgressMonitor monitor) {

		PeakDetectorSettingsCSD peakDetectorSettings = PreferenceSupplier.getPeakDetectorSettingsCSD();
		return detect(chromatogramSelection, peakDetectorSettings, monitor);
	}

	/**
	 * Use this method if peaks shall be detected without adding them to the chromatogram.
	 * Detect the peaks in the selection.
	 * This method does not add the peaks to the chromatogram.
	 * It needs to be handled separately.
	 * 
	 * @param chromatogramSelection
	 * @throws ValueMustNotBeNullException
	 */
	public List<IChromatogramPeakCSD> detectPeaks(IChromatogramSelectionCSD chromatogramSelection, PeakDetectorSettingsCSD peakDetectorSettings, IProgressMonitor monitor) {

		return detectPeaks(chromatogramSelection, peakDetectorSettings, null, monitor);
	}

	/**
	 * Additionally, noise segments are used if not null.
	 */
	public List<IChromatogramPeakCSD> detectPeaks(IChromatogramSelectionCSD chromatogramSelection, PeakDetectorSettingsCSD peakDetectorSettings, List<NoiseSegment> noiseSegments, IProgressMonitor monitor) {

		Threshold threshold = peakDetectorSettings.getThreshold();
		WindowSize windowSize = peakDetectorSettings.getMovingAverageWindowSize();
		List<IRawPeak> rawPeaks = new ArrayList<>();
		//
		if(noiseSegments != null && noiseSegments.size() > 0) {
			/*
			 * Initial retention time range before running the detection using
			 * noise segments.
			 * | --- [S] --- [N] --- [E] --- |
			 */
			Iterator<NoiseSegment> iterator = noiseSegments.iterator();
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			NoiseSegment noiseSegment = iterator.hasNext() ? iterator.next() : null;
			/*
			 * Range from the start of the chromatogram selection to the first noise segment
			 * | --- [S]
			 */
			if(noiseSegment != null) {
				chromatogramSelection.setRangeRetentionTime(startRetentionTime, noiseSegment.getStartRetentionTime());
				IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize);
				rawPeaks.addAll(getRawPeaks(slopes, threshold, monitor));
			}
			/*
			 * Ranges between the noise segments
			 * [S] --- [N] --- [E]
			 */
			while(iterator.hasNext()) {
				int startRetentionTimeSegment = noiseSegment.getStopRetentionTime();
				noiseSegment = iterator.next();
				int stopRetentionTimeSegment = noiseSegment.getStartRetentionTime();
				chromatogramSelection.setRangeRetentionTime(startRetentionTimeSegment, stopRetentionTimeSegment);
				IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize);
				rawPeaks.addAll(getRawPeaks(slopes, threshold, monitor));
			}
			/*
			 * Range from the last noise segment to the end of the chromatogram selection
			 * [E] --- |
			 */
			if(noiseSegment != null) {
				chromatogramSelection.setRangeRetentionTime(noiseSegment.getStopRetentionTime(), stopRetentionTime);
				IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize);
				rawPeaks.addAll(getRawPeaks(slopes, threshold, monitor));
			}
			/*
			 * Reset the retention time range to its initial values.
			 */
			chromatogramSelection.setRangeRetentionTime(startRetentionTime, stopRetentionTime);
		} else {
			/*
			 * Default: no noise segments
			 */
			IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize);
			rawPeaks.addAll(getRawPeaks(slopes, threshold, monitor));
		}
		return extractPeaks(rawPeaks, chromatogramSelection.getChromatogram(), peakDetectorSettings);
	}

	/**
	 * Builds from each raw peak a valid {@link IChromatogramPeakMSD} and adds it to the
	 * chromatogram.
	 * 
	 * @param rawPeaks
	 * @param chromatogram
	 * @return List<IChromatogramPeakCSD>
	 */
	private List<IChromatogramPeakCSD> extractPeaks(List<IRawPeak> rawPeaks, IChromatogramCSD chromatogram, PeakDetectorSettingsCSD peakDetectorSettings) {

		List<IChromatogramPeakCSD> peaks = new ArrayList<>();
		boolean includeBackground = peakDetectorSettings.isIncludeBackground();
		boolean optimizeBaseline = peakDetectorSettings.isOptimizeBaseline();
		//
		for(IRawPeak rawPeak : rawPeaks) {
			/*
			 * Build the peak and add it.
			 */
			try {
				/*
				 * Optimize the scan range.
				 */
				ScanRange scanRange = new ScanRange(rawPeak.getStartScan(), rawPeak.getStopScan());
				if(includeBackground && optimizeBaseline) {
					scanRange = optimizeBaseline(chromatogram, scanRange.getStartScan(), rawPeak.getMaximumScan(), scanRange.getStopScan(), null);
				}
				/*
				 * includeBackground
				 * false: BV or VB
				 * true: VV
				 */
				IChromatogramPeakCSD peak = PeakBuilderCSD.createPeak(chromatogram, scanRange, peakDetectorSettings.isIncludeBackground());
				if(isValidPeak(peak, peakDetectorSettings)) {
					/*
					 * Add the detector description.
					 */
					peak.setDetectorDescription(FirstDerivativePeakDetector.DETECTOR_DESCRIPTION);
					peaks.add(peak);
				}
			} catch(IllegalArgumentException e) {
				logger.warn(e);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
		//
		return peaks;
	}

	/**
	 * Initializes the slope values.
	 * 
	 * @param chromatogramSelection
	 * @param window
	 * @return {@link IFirstDerivativeDetectorSlopes}
	 */
	public static IFirstDerivativeDetectorSlopes getFirstDerivativeSlopes(IChromatogramSelectionCSD chromatogramSelection, WindowSize windowSize) {

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
		/*
		 * Moving average on the slopes
		 */
		if(!WindowSize.NONE.equals(windowSize)) {
			slopes.calculateMovingAverage(windowSize);
		}
		//
		return slopes;
	}

	/**
	 * Checks that the peak is not null and that it matches
	 * the min S/N requirements.
	 * 
	 * @param peak
	 * @return boolean
	 */
	private boolean isValidPeak(IChromatogramPeakCSD peak, PeakDetectorSettingsCSD peakDetectorSettings) {

		if(peak != null && peak.getSignalToNoiseRatio() >= peakDetectorSettings.getMinimumSignalToNoiseRatio()) {
			return true;
		}
		return false;
	}
}
