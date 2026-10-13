/*******************************************************************************
 * Copyright (c) 2008, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Christoph Läubrich - extract common methods to base class
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.firstderivative.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
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
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.INoiseSegment;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.PeakBuilderMSD;
import org.eclipse.chemclipse.msd.model.util.MarkedTracesSupportMSD;
import org.eclipse.chemclipse.msd.model.xic.ITotalIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.TotalIonSignalExtractor;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.support.traces.ITrace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.services.translation.TranslationService;

public class PeakDetectorMSD extends BasePeakDetector implements IPeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorMSD.class);

	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorSettingsMSD detectorSettings, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, detectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(detectorSettings instanceof PeakDetectorSettingsMSD peakDetectorSettings) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
				/*
				 * Extract the noise segments.
				 */
				List<INoiseSegment> noiseSegments = null;
				if(peakDetectorSettings.isUseNoiseSegments()) {
					noiseSegments = NoiseChromatogramSupport.getNoiseSegments(chromatogram, chromatogramSelection, false, subMonitor.split(10));
				}
				/*
				 * Detect and add the peaks.
				 */
				List<IChromatogramPeakMSD> peaks = detectPeaks(chromatogramSelection, peakDetectorSettings, noiseSegments, subMonitor.split(90));
				for(IChromatogramPeakMSD peak : peaks) {
					chromatogram.getPeaks().add(peak);
				}
				chromatogram.setDirty(true);
				TranslationService translationService = TranslationSupport.getTranslationService();
				String peakDetectedMessage = MessageFormat.format(translationService.translate("%PeaksDetected", TranslationSupport.getContributorURI(PeakDetectorMSD.class)), String.valueOf(peaks.size()));
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, translationService.translate("%FirstDerivative", TranslationSupport.getContributorURI(PeakDetectorMSD.class)), peakDetectedMessage));
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

	/**
	 * Use this method if peaks shall be detected without adding them to the chromatogram.
	 * Detect the peaks in the selection.
	 * This method does not add the peaks to the chromatogram.
	 * It needs to be handled separately.
	 */
	public List<IChromatogramPeakMSD> detectPeaks(IChromatogramSelectionMSD chromatogramSelection, PeakDetectorSettingsMSD peakDetectorSettings, IProgressMonitor monitor) {

		return detectPeaks(chromatogramSelection, peakDetectorSettings, null, monitor);
	}

	/**
	 * Additionally, noise segments are used if not null.
	 */
	public List<IChromatogramPeakMSD> detectPeaks(IChromatogramSelectionMSD chromatogramSelection, PeakDetectorSettingsMSD peakDetectorSettings, List<INoiseSegment> noiseSegments, IProgressMonitor monitor) {

		List<IChromatogramPeakMSD> extractPeaks = new ArrayList<>();
		Collection<IMarkedTraces<ITrace>> filterIons = peakDetectorSettings.getFilterIons();
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		for(IMarkedTraces<ITrace> ions : filterIons) {
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
					IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize, ions);
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
					IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize, ions);
					rawPeaks.addAll(getRawPeaks(slopes, threshold, monitor));
				}
				/*
				 * Range from the last noise segment to the end of the chromatogram selection
				 * [E] --- |
				 */
				if(noiseSegment != null) {
					chromatogramSelection.setRangeRetentionTime(noiseSegment.getStopRetentionTime(), stopRetentionTime);
					IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize, ions);
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
				IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize, ions);
				rawPeaks.addAll(getRawPeaks(slopes, threshold, monitor));
			}
			List<IChromatogramPeakMSD> peaks = extractPeaks(rawPeaks, chromatogram, peakDetectorSettings, ions);
			if(peakDetectorSettings.isUseIndividualTraces()) {
				String classifier = "Trace " + MarkedTracesSupportMSD.getTracesAsInteger(ions).iterator().next();
				for(IChromatogramPeakMSD msd : peaks) {
					msd.addClassifier(classifier);
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
	private List<IChromatogramPeakMSD> extractPeaks(List<IRawPeak> rawPeaks, IChromatogramMSD chromatogram, PeakDetectorSettingsMSD peakDetectorSettings, IMarkedTraces<ITrace> ions) {

		List<IChromatogramPeakMSD> peaks = new ArrayList<>();
		Set<Integer> traces = MarkedTracesSupportMSD.getTracesAsInteger(ions);
		DetectorType detectorType = peakDetectorSettings.getDetectorType();
		boolean optimizeBaseline = peakDetectorSettings.isOptimizeBaseline();

		for(IRawPeak rawPeak : rawPeaks) {
			try {
				/*
				 * Optimize the scan range.
				 */
				ScanRange scanRange = new ScanRange(rawPeak.getStartScan(), rawPeak.getStopScan());
				if(isValleyOption(detectorType) && optimizeBaseline) {
					scanRange = optimizeBaseline(chromatogram, scanRange.getStartScan(), rawPeak.getMaximumScan(), scanRange.getStopScan(), ions);
				}
				/*
				 * Detector Type
				 */
				IChromatogramPeakMSD peak = null;
				switch(detectorType) {
					case BB:
						peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, PeakType.BB, traces, ions.getMarkedTraceModus());
						break;
					case CB:
						peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, PeakType.CB, traces, ions.getMarkedTraceModus());
						break;
					default:
						peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, PeakType.VV, traces, ions.getMarkedTraceModus());
						break;
				}
				/*
				 * Validate
				 * Add the detector description.
				 */
				if(isValidPeak(peak, peakDetectorSettings)) {
					TranslationService translationService = TranslationSupport.getTranslationService();
					peak.setDetectorDescription(translationService.translate("%FirstDerivative", TranslationSupport.getContributorURI(PeakDetectorMSD.class)));
					peaks.add(peak);
				}
			} catch(Exception e) {
				logger.error(e);
			}
		}

		chromatogram.setDirty(true);
		return peaks;
	}

	/**
	 * Initializes the slope values.
	 *
	 * @param chromatogramSelection
	 * @param windowSize
	 * @return {@link IFirstDerivativeDetectorSlopes}
	 */
	public static IFirstDerivativeDetectorSlopes getFirstDerivativeSlopes(IChromatogramSelectionMSD chromatogramSelection, int windowSize, IMarkedTraces<ITrace> filterIons) {

		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		try {
			ITotalIonSignalExtractor totalIonSignalExtractor = new TotalIonSignalExtractor(chromatogram);
			ITotalScanSignals signals = totalIonSignalExtractor.getTotalIonSignals(chromatogramSelection, filterIons);
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
	 *
	 * @param peak
	 * @return boolean
	 */
	private boolean isValidPeak(IChromatogramPeakMSD peak, PeakDetectorSettingsMSD peakDetectorSettings) {

		return (peak != null && peak.getSignalToNoiseRatio() >= peakDetectorSettings.getMinimumSignalToNoiseRatio());
	}

	protected ScanRange optimizeBaseline(IChromatogramMSD chromatogram, int startScan, int centerScan, int stopScan, IMarkedTraces<ITrace> ions) {

		/*
		 * Right and left baseline optimization
		 */
		int stopScanOptimized = optimizeRightBaseline(chromatogram, startScan, centerScan, stopScan, ions);
		int startScanOptimized = optimizeLeftBaseline(chromatogram, startScan, centerScan, stopScanOptimized, ions);

		return new ScanRange(startScanOptimized, stopScanOptimized);
	}

	protected float getScanSignal(IChromatogramMSD chromatogram, int scanNumber, IMarkedTraces<ITrace> ions) {

		IScan scan = chromatogram.getScan(scanNumber);
		if(scan instanceof IScanMSD scanMSD) {
			return scanMSD.getTotalSignal(ions);
		}
		return scan.getTotalSignal();
	}

	private int optimizeRightBaseline(IChromatogramMSD chromatogram, int startScan, int centerScan, int stopScan, IMarkedTraces<ITrace> ions) {

		IPoint p1 = new Point(getRetentionTime(chromatogram, startScan), getScanSignal(chromatogram, startScan, ions));
		IPoint p2 = new Point(getRetentionTime(chromatogram, stopScan), getScanSignal(chromatogram, stopScan, ions));
		LinearEquation backgroundEquation = Equations.createLinearEquation(p1, p2);
		/*
		 * Right border optimization
		 */
		int stopScanOptimized = stopScan;
		for(int i = stopScan; i > centerScan; i--) {
			float signal = getScanSignal(chromatogram, i, ions);
			int retentionTime = chromatogram.getScan(i).getRetentionTime();
			if(signal < backgroundEquation.calculateY(retentionTime)) {
				stopScanOptimized = i;
			}
		}

		return stopScanOptimized;
	}

	private int optimizeLeftBaseline(IChromatogramMSD chromatogram, int startScan, int centerScan, int stopScan, IMarkedTraces<ITrace> ions) {

		IPoint p1 = new Point(getRetentionTime(chromatogram, startScan), getScanSignal(chromatogram, startScan, ions));
		IPoint p2 = new Point(getRetentionTime(chromatogram, stopScan), getScanSignal(chromatogram, stopScan, ions));
		LinearEquation backgroundEquation = Equations.createLinearEquation(p1, p2);
		/*
		 * Right border optimization
		 */
		int startScanOptimized = startScan;
		for(int i = startScan; i < centerScan; i++) {
			float signal = getScanSignal(chromatogram, i, ions);
			int retentionTime = chromatogram.getScan(i).getRetentionTime();
			if(signal < backgroundEquation.calculateY(retentionTime)) {
				/*
				 * Create a new equation
				 */
				startScanOptimized = i;
				p1 = new Point(getRetentionTime(chromatogram, startScanOptimized), getScanSignal(chromatogram, startScanOptimized, ions));
				p2 = new Point(getRetentionTime(chromatogram, stopScan), getScanSignal(chromatogram, stopScan, ions));
				backgroundEquation = Equations.createLinearEquation(p1, p2);
			}
		}

		return startScanOptimized;
	}

	private boolean isValleyOption(DetectorType detectorType) {

		return DetectorType.VV.equals(detectorType);
	}
}