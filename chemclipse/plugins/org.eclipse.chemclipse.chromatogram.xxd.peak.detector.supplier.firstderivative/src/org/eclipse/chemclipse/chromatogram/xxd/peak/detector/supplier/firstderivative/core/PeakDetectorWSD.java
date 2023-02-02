/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.IPeakDetectorWSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.IPeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.messages.FirstDerivativeMessages;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.messages.IFirstDerivativeMessages;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.NoiseSegment;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.PeakBuilderWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ITotalWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.TotalWavelengthSignalExtractor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class PeakDetectorWSD<P extends IPeak, C extends IChromatogram<P>, R> extends BasePeakDetector<P, C, R> implements IPeakDetectorWSD<P, C, R> {

	private static final Logger logger = Logger.getLogger(PeakDetectorWSD.class);
	//
	private static final float NORMALIZATION_BASE = 100000.0f;

	@Override
	public IProcessingInfo<R> detect(IChromatogramSelectionWSD chromatogramSelection, IPeakDetectorSettingsWSD detectorSettings, IProgressMonitor monitor) {

		/*
		 * Check whether the chromatogram selection is null or not.
		 */
		IProcessingInfo<R> processingInfo = validate(chromatogramSelection, detectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(detectorSettings instanceof PeakDetectorSettingsWSD peakDetectorSettings) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				/*
				 * Extract the noise segments.
				 */
				List<NoiseSegment> noiseSegments = null;
				IChromatogramWSD chromatogram = chromatogramSelection.getChromatogram();
				if(peakDetectorSettings.isUseNoiseSegments()) {
					noiseSegments = NoiseChromatogramClassifier.getNoiseSegments(chromatogram, chromatogramSelection, false, subMonitor.split(10));
				}
				List<IChromatogramPeakWSD> peaks = detectPeaks(chromatogramSelection, peakDetectorSettings, noiseSegments, subMonitor.split(90));
				for(IChromatogramPeakWSD peak : peaks) {
					chromatogram.addPeak(peak);
				}
				chromatogram.setDirty(true);
				String peakDetectedMessage = FirstDerivativeMessages.INSTANCE().getMessage(IFirstDerivativeMessages.PEAKS_DETECTED, String.valueOf(peaks.size()));
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, FirstDerivativePeakDetector.DETECTOR_DESCRIPTION, peakDetectedMessage));
			} else {
				logger.warn("Settings is not of type: " + PeakDetectorSettingsWSD.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<R> detect(IChromatogramSelectionWSD chromatogramSelection, IProgressMonitor monitor) {

		PeakDetectorSettingsWSD peakDetectorSettings = PreferenceSupplier.getPeakDetectorSettingsWSD();
		chromatogramSelection.getChromatogram().setDirty(true);
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
	public List<IChromatogramPeakWSD> detectPeaks(IChromatogramSelectionWSD chromatogramSelection, PeakDetectorSettingsWSD peakDetectorSettings, List<NoiseSegment> noiseSegments, IProgressMonitor monitor) {

		List<IChromatogramPeakWSD> extractPeaks = new ArrayList<>();
		Collection<IMarkedWavelengths> filterWavelengths = peakDetectorSettings.getFilterWavelengths();
		IChromatogramWSD chromatogram = chromatogramSelection.getChromatogram();
		for(IMarkedWavelengths wavelengths : filterWavelengths) {
			Threshold threshold = peakDetectorSettings.getThreshold();
			int windowSize = peakDetectorSettings.getMovingAverageWindowSize();
			List<IRawPeak> rawPeaks = new ArrayList<>();
			//
			if(noiseSegments != null && !noiseSegments.isEmpty()) {
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
					IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize, wavelengths);
					rawPeaks.addAll(getRawPeaks(slopes, threshold, monitor));
				}
				/*
				 * Ranges between the noise segments
				 * [S] --- [N] --- [E]
				 */
				while(iterator.hasNext() && noiseSegment != null) {
					int previousStopRetentionTimeSegment = noiseSegment.getStopRetentionTime();
					noiseSegment = iterator.next();
					int nextStartRetentionTimeSegment = noiseSegment.getStartRetentionTime();
					chromatogramSelection.setRangeRetentionTime(previousStopRetentionTimeSegment, nextStartRetentionTimeSegment);
					IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize, wavelengths);
					rawPeaks.addAll(getRawPeaks(slopes, threshold, monitor));
				}
				/*
				 * Range from the last noise segment to the end of the chromatogram selection
				 * [E] --- |
				 */
				if(noiseSegment != null) {
					chromatogramSelection.setRangeRetentionTime(noiseSegment.getStopRetentionTime(), stopRetentionTime);
					IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize, wavelengths);
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
				IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize, wavelengths);
				rawPeaks = getRawPeaks(slopes, peakDetectorSettings.getThreshold(), monitor);
			}
			List<IChromatogramPeakWSD> peaks = extractPeaks(rawPeaks, chromatogram, peakDetectorSettings, wavelengths);
			if(peakDetectorSettings.isIndividualWavelengths()) {
				String classifier = "Wavelength " + peaks.iterator().next();
				for(IChromatogramPeakWSD wsd : peaks) {
					wsd.addClassifier(classifier);
				}
			}
			extractPeaks.addAll(peaks);
		}
		chromatogram.setDirty(true);
		return extractPeaks;
	}

	/**
	 * Builds from each raw peak a valid {@link IChromatogramPeakMSD} and adds it to the
	 * chromatogram.
	 * 
	 * @param rawPeaks
	 * @param chromatogram
	 * @return List<IChromatogramPeakCSD>
	 */
	private List<IChromatogramPeakWSD> extractPeaks(List<IRawPeak> rawPeaks, IChromatogramWSD chromatogram, PeakDetectorSettingsWSD peakDetectorSettings, IMarkedWavelengths wavelengths) {

		List<IChromatogramPeakWSD> peaks = new ArrayList<>();
		Set<Integer> traces = wavelengths.getWavelengths().stream().map(Double::intValue).collect(Collectors.toSet());
		boolean includeBackground = peakDetectorSettings.isIncludeBackground();
		boolean optimizeBaseline = peakDetectorSettings.isOptimizeBaseline();
		//
		IChromatogramPeakWSD peak = null;
		IScanRange scanRange = null;
		for(IRawPeak rawPeak : rawPeaks) {
			/*
			 * Build the peak and add it.
			 */
			try {
				/*
				 * Optimize the scan range.
				 */
				scanRange = new ScanRange(rawPeak.getStartScan(), rawPeak.getStopScan());
				if(includeBackground && optimizeBaseline) {
					scanRange = optimizeBaseline(chromatogram, scanRange.getStartScan(), rawPeak.getMaximumScan(), scanRange.getStopScan(), wavelengths);
				}
				/*
				 * includeBackground
				 * false: BV or VB
				 * true: VV
				 */
				peak = PeakBuilderWSD.createPeak(chromatogram, scanRange, includeBackground, traces, wavelengths.getMarkedTraceModus());
				if(isValidPeak(peak)) {
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
	public static IFirstDerivativeDetectorSlopes getFirstDerivativeSlopes(IChromatogramSelectionWSD chromatogramSelection, int windowSize, IMarkedWavelengths filterWavelengths) {

		IChromatogramWSD chromatogram = chromatogramSelection.getChromatogram();
		try {
			ITotalWavelengthSignalExtractor totalWavelengthSignalExtractor = new TotalWavelengthSignalExtractor(chromatogram);
			ITotalScanSignals signals = totalWavelengthSignalExtractor.getTotalWavelengthSignals(chromatogramSelection, filterWavelengths);
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
			if(windowSize != 0) {
				slopes.calculateMovingAverage(windowSize);
			}
			//
			return slopes;
		} catch(ChromatogramIsNullException e) {
			logger.warn(e.getLocalizedMessage(), e);
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
	private boolean isValidPeak(IChromatogramPeakWSD peak) {

		/*
		 * Noise calculation needs to be adjusted for WSD chromatograms.
		 */
		return (peak != null); // && peak.getSignalToNoiseRatio() >= minimumSignalToNoiseRatio
	}

	protected ScanRange optimizeBaseline(IChromatogramWSD chromatogram, int startScan, int centerScan, int stopScan, IMarkedWavelengths wavelengths) {

		/*
		 * Right and left baseline optimization
		 */
		int stopScanOptimized = optimizeRightBaseline(chromatogram, startScan, centerScan, stopScan, wavelengths);
		int startScanOptimized = optimizeLeftBaseline(chromatogram, startScan, centerScan, stopScanOptimized, wavelengths);
		//
		return new ScanRange(startScanOptimized, stopScanOptimized);
	}

	protected float getScanSignal(IChromatogramWSD chromatogram, int scanNumber, IMarkedWavelengths wavelengths) {

		IScan scan = chromatogram.getScan(scanNumber);
		if(scan instanceof IScanWSD scanWSD) {
			return scanWSD.getTotalSignal(wavelengths);
		}
		return scan.getTotalSignal();
	}

	private int optimizeRightBaseline(IChromatogramWSD chromatogram, int startScan, int centerScan, int stopScan, IMarkedWavelengths wavelengths) {

		IPoint p1 = new Point(getRetentionTime(chromatogram, startScan), getScanSignal(chromatogram, startScan, wavelengths));
		IPoint p2 = new Point(getRetentionTime(chromatogram, stopScan), getScanSignal(chromatogram, stopScan, wavelengths));
		LinearEquation backgroundEquation = Equations.createLinearEquation(p1, p2);
		/*
		 * Right border optimization
		 */
		int stopScanOptimized = stopScan;
		for(int i = stopScan; i > centerScan; i--) {
			float signal = getScanSignal(chromatogram, i, wavelengths);
			int retentionTime = chromatogram.getScan(i).getRetentionTime();
			if(signal < backgroundEquation.calculateY(retentionTime)) {
				stopScanOptimized = i;
			}
		}
		//
		return stopScanOptimized;
	}

	private int optimizeLeftBaseline(IChromatogramWSD chromatogram, int startScan, int centerScan, int stopScan, IMarkedWavelengths wavelengths) {

		IPoint p1 = new Point(getRetentionTime(chromatogram, startScan), getScanSignal(chromatogram, startScan, wavelengths));
		IPoint p2 = new Point(getRetentionTime(chromatogram, stopScan), getScanSignal(chromatogram, stopScan, wavelengths));
		LinearEquation backgroundEquation = Equations.createLinearEquation(p1, p2);
		/*
		 * Right border optimization
		 */
		int startScanOptimized = startScan;
		for(int i = startScan; i < centerScan; i++) {
			float signal = getScanSignal(chromatogram, i, wavelengths);
			int retentionTime = chromatogram.getScan(i).getRetentionTime();
			if(signal < backgroundEquation.calculateY(retentionTime)) {
				/*
				 * Create a new equation
				 */
				startScanOptimized = i;
				p1 = new Point(getRetentionTime(chromatogram, startScanOptimized), getScanSignal(chromatogram, startScanOptimized, wavelengths));
				p2 = new Point(getRetentionTime(chromatogram, stopScan), getScanSignal(chromatogram, stopScan, wavelengths));
				backgroundEquation = Equations.createLinearEquation(p1, p2);
			}
		}
		//
		return startScanOptimized;
	}
}
