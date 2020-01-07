/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.FilterMode;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.NoiseSegment;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons.IonMarkMode;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
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
import org.eclipse.core.runtime.SubMonitor;

public class PeakDetectorMSD extends BasePeakDetector implements IPeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorMSD.class);
	//
	private static final String DETECTOR_DESCRIPTION = "Peak Detector First Derivative";

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public IProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorSettingsMSD detectorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, detectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(detectorSettings instanceof PeakDetectorSettingsMSD) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				PeakDetectorSettingsMSD peakDetectorSettings = (PeakDetectorSettingsMSD)detectorSettings;
				IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
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
				List<IChromatogramPeakMSD> peaks = detectPeaks(chromatogramSelection, peakDetectorSettings, noiseSegments, subMonitor.split(90));
				for(IChromatogramPeakMSD peak : peaks) {
					chromatogram.addPeak(peak);
				}
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Peak Detector First Derivative", peaks.size() + " peak(s) have been detected."));
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
	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		if(peakDetectorSettings == null) {
			peakDetectorSettings = PreferenceSupplier.getPeakDetectorSettingsMSD();
		}
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
	public List<IChromatogramPeakMSD> detectPeaks(IChromatogramSelectionMSD chromatogramSelection, PeakDetectorSettingsMSD peakDetectorSettings, IProgressMonitor monitor) {

		return detectPeaks(chromatogramSelection, peakDetectorSettings, null, monitor);
	}

	/**
	 * Additionally, noise segments are used if not null.
	 */
	public List<IChromatogramPeakMSD> detectPeaks(IChromatogramSelectionMSD chromatogramSelection, PeakDetectorSettingsMSD peakDetectorSettings, List<NoiseSegment> noiseSegments, IProgressMonitor monitor) {

		IMarkedIons ions = PeakDetectorSettingsMSD.getFilterIons(peakDetectorSettings);
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
				IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection, windowSize, ions);
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
		return extractPeaks(rawPeaks, chromatogramSelection.getChromatogram(), peakDetectorSettings, ions);
	}

	/**
	 * Builds from each raw peak a valid {@link IChromatogramPeakMSD} and adds it to the
	 * chromatogram.
	 * 
	 * @param rawPeaks
	 * @param chromatogram
	 * @return List<IChromatogramPeakCSD>
	 */
	private List<IChromatogramPeakMSD> extractPeaks(List<IRawPeak> rawPeaks, IChromatogramMSD chromatogram, PeakDetectorSettingsMSD peakDetectorSettings, IMarkedIons ions) {

		List<IChromatogramPeakMSD> peaks = new ArrayList<>();
		Set<Integer> traces = ions.getIonsNominal().stream().map(e -> e.intValue()).collect(Collectors.toSet());
		boolean includeBackground = peakDetectorSettings.isIncludeBackground();
		boolean optimizeBaseline = peakDetectorSettings.isOptimizeBaseline();
		//
		for(IRawPeak rawPeak : rawPeaks) {
			try {
				/*
				 * Optimize the scan range.
				 */
				ScanRange scanRange = new ScanRange(rawPeak.getStartScan(), rawPeak.getStopScan());
				if(includeBackground && optimizeBaseline) {
					scanRange = optimizeBaseline(chromatogram, scanRange.getStartScan(), rawPeak.getMaximumScan(), scanRange.getStopScan(), ions);
				}
				/*
				 * includeBackground
				 * false: BV or VB
				 * true: VV
				 */
				IChromatogramPeakMSD peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, includeBackground, traces, ions.getMode());
				if(isValidPeak(peak, peakDetectorSettings)) {
					/*
					 * Add the detector description.
					 */
					peak.setDetectorDescription(DETECTOR_DESCRIPTION);
					peaks.add(peak);
				}
			} catch(Exception e) {
				logger.debug(e.getLocalizedMessage());
			}
		}
		//
		return peaks;
	}

	/**
	 * Initializes the slope values.
	 * 
	 * @param chromatogramSelection
	 * @param windowSize
	 * @return {@link IFirstDerivativeDetectorSlopes}
	 */
	public static IFirstDerivativeDetectorSlopes getFirstDerivativeSlopes(IChromatogramSelectionMSD chromatogramSelection, WindowSize movingAverageWindowSize, IMarkedIons filterIons) {

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
			slopes.calculateMovingAverage(movingAverageWindowSize);
			return slopes;
		} catch(ChromatogramIsNullException e) {
			logger.warn(e.getLocalizedMessage(), e);
			return null;
		}
	}

	static IMarkedIons getIonFilter(Collection<Number> filterIons, FilterMode mode) {

		return new MarkedIons(buildIons(filterIons), buildFilterMode(mode));
	}

	private static int[] buildIons(Collection<Number> filterIons) {

		int[] result = new int[filterIons.size()];
		int cnt = 0;
		Iterator<Number> it = filterIons.iterator();
		while(it.hasNext()) {
			result[cnt++] = it.next().intValue();
		}
		return result;
	}

	private static IonMarkMode buildFilterMode(FilterMode mode) {

		switch(mode) {
			case EXCLUDE:
				return IonMarkMode.EXCLUDE;
			case INCLUDE:
				return IonMarkMode.INCLUDE;
		}
		throw new IllegalArgumentException("Unknown mode " + mode);
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
