/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - extract common methods to base class
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.peak.detector.supplier.firstderivative.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.IPeakDetectorWSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.IPeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseChromatogramSupport;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core.BasePeakDetector;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.model.DetectorType;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.INoiseSegment;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.support.traces.ITrace;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.PeakBuilderWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ITotalWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.TotalWavelengthSignalExtractor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.services.translation.TranslationService;

public class PeakDetectorWSD extends BasePeakDetector implements IPeakDetectorWSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorWSD.class);

	private static final float NORMALIZATION_BASE = 100000.0f;

	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionWSD chromatogramSelection, IPeakDetectorSettingsWSD detectorSettings, IProgressMonitor monitor) {

		/*
		 * Check whether the chromatogram selection is null or not.
		 */
		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, detectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(detectorSettings instanceof PeakDetectorSettingsWSD peakDetectorSettings) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				/*
				 * Extract the noise segments.
				 */
				List<INoiseSegment> noiseSegments = null;
				IChromatogramWSD chromatogram = chromatogramSelection.getChromatogram();
				if(peakDetectorSettings.isUseNoiseSegments()) {
					noiseSegments = NoiseChromatogramSupport.getNoiseSegments(chromatogram, chromatogramSelection, false, subMonitor.split(10));
				}
				List<IChromatogramPeakWSD> peaks = detectPeaks(chromatogramSelection, peakDetectorSettings, noiseSegments, subMonitor.split(90));
				for(IChromatogramPeakWSD peak : peaks) {
					chromatogram.getPeaks().add(peak);
				}
				chromatogram.setDirty(true);
				TranslationService translationService = TranslationSupport.getTranslationService();
				String peakDetectedMessage = MessageFormat.format(translationService.translate("%PeaksDetected", TranslationSupport.getContributorURI(PeakDetectorWSD.class)), String.valueOf(peaks.size()));
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, translationService.translate("%FirstDerivative", TranslationSupport.getContributorURI(PeakDetectorWSD.class)), peakDetectedMessage));
			} else {
				logger.warn("Settings is not of type: " + PeakDetectorSettingsWSD.class);
			}
		}
		return processingInfo;
	}

	/**
	 * Use this method if peaks shall be detected without adding them to the chromatogram.
	 * Detect the peaks in the selection.
	 * This method does not add the peaks to the chromatogram.
	 * It needs to be handled separately.
	 */
	public List<IChromatogramPeakWSD> detectPeaks(IChromatogramSelectionWSD chromatogramSelection, PeakDetectorSettingsWSD peakDetectorSettings, List<INoiseSegment> noiseSegments, IProgressMonitor monitor) {

		List<IChromatogramPeakWSD> extractPeaks = new ArrayList<>();
		Collection<IMarkedTraces<ITrace>> filterWavelengths = peakDetectorSettings.getFilterWavelengths();
		IChromatogramWSD chromatogram = chromatogramSelection.getChromatogram();
		for(IMarkedTraces<ITrace> wavelengths : filterWavelengths) {
			Threshold threshold = peakDetectorSettings.getThreshold();
			int windowSize = peakDetectorSettings.getMovingAverageWindowSize();
			List<IRawPeak> rawPeaks = new ArrayList<>();

			if(noiseSegments != null && !noiseSegments.isEmpty()) {
				/*
				 * Initial retention time range before running the detection using
				 * noise segments.
				 * | --- [S] --- [N] --- [E] --- |
				 */
				Iterator<INoiseSegment> iterator = noiseSegments.iterator();
				int startRetentionTime = chromatogramSelection.getStartRetentionTime();
				int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
				INoiseSegment noiseSegment = iterator.hasNext() ? iterator.next() : null;
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
	 */
	private List<IChromatogramPeakWSD> extractPeaks(List<IRawPeak> rawPeaks, IChromatogramWSD chromatogram, PeakDetectorSettingsWSD peakDetectorSettings, IMarkedTraces<ITrace> wavelengths) {

		List<IChromatogramPeakWSD> peaks = new ArrayList<>();
		Set<Integer> traces = wavelengths.stream().map(t -> (int)Math.round(t.getValue())).collect(Collectors.toSet());
		DetectorType detectorType = peakDetectorSettings.getDetectorType();
		boolean optimizeBaseline = peakDetectorSettings.isOptimizeBaseline();

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
				if(isValleyOption(detectorType) && optimizeBaseline) {
					scanRange = optimizeBaseline(chromatogram, scanRange.getStartScan(), rawPeak.getMaximumScan(), scanRange.getStopScan(), wavelengths);
				}
				/*
				 * Detector Type
				 */
				switch(detectorType) {
					case BB:
						peak = PeakBuilderWSD.createPeak(chromatogram, scanRange, false, traces, wavelengths.getMarkedTraceModus());
						break;
					case CB: // There is no real baseline, hence use VV.
					default:
						peak = PeakBuilderWSD.createPeak(chromatogram, scanRange, true, traces, wavelengths.getMarkedTraceModus());
						break;
				}
				/*
				 * Validate
				 * Add the detector description.
				 */
				if(isValidPeak(peak)) {
					TranslationService translationService = TranslationSupport.getTranslationService();
					peak.setDetectorDescription(translationService.translate("%FirstDerivative", TranslationSupport.getContributorURI(PeakDetectorWSD.class)));
					peaks.add(peak);
				}
			} catch(PeakException e) {
				logger.warn(e.getMessage());
			}
		}

		return peaks;
	}

	/**
	 * Initializes the slope values.
	 */
	public static IFirstDerivativeDetectorSlopes getFirstDerivativeSlopes(IChromatogramSelectionWSD chromatogramSelection, int windowSize, IMarkedTraces<ITrace> filterWavelengths) {

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

			return slopes;
		} catch(ChromatogramIsNullException e) {
			logger.warn(e.getLocalizedMessage(), e);
			return null;
		}
	}

	/**
	 * Checks that the peak is not null and that it matches
	 * the min S/N requirements.
	 */
	private boolean isValidPeak(IChromatogramPeakWSD peak) {

		/*
		 * Noise calculation needs to be adjusted for WSD chromatograms.
		 */
		return (peak != null); // && peak.getSignalToNoiseRatio() >= minimumSignalToNoiseRatio
	}

	protected ScanRange optimizeBaseline(IChromatogramWSD chromatogram, int startScan, int centerScan, int stopScan, IMarkedTraces<ITrace> wavelengths) {

		/*
		 * Right and left baseline optimization
		 */
		int stopScanOptimized = optimizeRightBaseline(chromatogram, startScan, centerScan, stopScan, wavelengths);
		int startScanOptimized = optimizeLeftBaseline(chromatogram, startScan, centerScan, stopScanOptimized, wavelengths);

		return new ScanRange(startScanOptimized, stopScanOptimized);
	}

	protected float getScanSignal(IChromatogramWSD chromatogram, int scanNumber, IMarkedTraces<ITrace> wavelengths) {

		IScan scan = chromatogram.getScan(scanNumber);
		if(scan instanceof IScanWSD scanWSD) {
			return scanWSD.getTotalSignal(wavelengths);
		}
		return scan.getTotalSignal();
	}

	private int optimizeRightBaseline(IChromatogramWSD chromatogram, int startScan, int centerScan, int stopScan, IMarkedTraces<ITrace> wavelengths) {

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

		return stopScanOptimized;
	}

	private int optimizeLeftBaseline(IChromatogramWSD chromatogram, int startScan, int centerScan, int stopScan, IMarkedTraces<ITrace> wavelengths) {

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

		return startScanOptimized;
	}

	private boolean isValleyOption(DetectorType detectorType) {

		return DetectorType.VV.equals(detectorType);
	}
}