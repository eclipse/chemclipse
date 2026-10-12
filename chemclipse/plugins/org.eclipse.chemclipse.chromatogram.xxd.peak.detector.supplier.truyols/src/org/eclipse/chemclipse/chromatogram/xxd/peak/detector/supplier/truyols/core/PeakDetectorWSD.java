/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.AbstractPeakDetectorWSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.IPeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.model.DetectorType;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.settings.PeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.support.TruyolsPeakDetector;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.traces.ITrace;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.PeakBuilderWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ITotalWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.TotalWavelengthSignalExtractor;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetectorWSD extends AbstractPeakDetectorWSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorWSD.class);

	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionWSD chromatogramSelection, IPeakDetectorSettingsWSD peakDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, peakDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(peakDetectorSettings instanceof PeakDetectorSettingsWSD settings) {
				IChromatogramWSD chromatogram = chromatogramSelection.getChromatogram();
				List<IChromatogramPeakWSD> peaks = detectPeaks(chromatogramSelection, settings, monitor);
				if(!peaks.isEmpty()) {
					chromatogram.getPeaks().addAll(peaks);
					chromatogram.setDirty(true);
				}
				String message = MessageFormat.format("Peaks Detected", String.valueOf(peaks.size()));
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Truyols Detector", message));
			} else {
				logger.warn("Settings is not of type: " + PeakDetectorSettingsWSD.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> validate(IChromatogramSelection chromatogramSelection, IPeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		return super.validate(chromatogramSelection, peakDetectorSettings, monitor);
	}

	public List<IChromatogramPeakWSD> detectPeaks(IChromatogramSelectionWSD chromatogramSelection, PeakDetectorSettingsWSD settings, IProgressMonitor monitor) {

		IChromatogramWSD chromatogram = chromatogramSelection.getChromatogram();
		List<IChromatogramPeakWSD> allPeaks = new ArrayList<>();
		Collection<IMarkedTraces<ITrace>> filterWavelengthsList = settings.getFilterWavelengths();
		ITotalWavelengthSignalExtractor extractor = new TotalWavelengthSignalExtractor(chromatogram);
		for(IMarkedTraces<ITrace> filterWavelengths : filterWavelengthsList) {
			ITotalScanSignals signals = extractor.getTotalWavelengthSignals(chromatogramSelection, filterWavelengths);
			if(signals == null) {
				continue;
			}
			int startScan = signals.getStartScan();
			int size = signals.size();
			double[] values = new double[size];
			int index = 0;
			for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
				values[index++] = signal.getTotalSignal();
			}
			List<IRawPeak> rawPeaks = TruyolsPeakDetector.detect(values, startScan, settings.getDerivativeOption(), settings.getWindowSize(), settings.getPolynomialOrder(), settings.getNoiseFactor());
			List<IChromatogramPeakWSD> peaks = buildPeaks(rawPeaks, chromatogram, settings, filterWavelengths);
			if(settings.isUseIndividualWavelengths() && !peaks.isEmpty()) {
				Set<Integer> traces = toTraceIntegers(filterWavelengths);
				if(!traces.isEmpty()) {
					String classifier = "Wavelength " + traces.iterator().next();
					for(IChromatogramPeakWSD peak : peaks) {
						peak.addClassifier(classifier);
					}
				}
			}
			allPeaks.addAll(peaks);
		}
		return allPeaks;
	}

	private List<IChromatogramPeakWSD> buildPeaks(List<IRawPeak> rawPeaks, IChromatogramWSD chromatogram, PeakDetectorSettingsWSD settings, IMarkedTraces<ITrace> filterWavelengths) {

		List<IChromatogramPeakWSD> peaks = new ArrayList<>();
		boolean includedBackground = includeBackground(settings.getDetectorType());
		Set<Integer> traces = toTraceIntegers(filterWavelengths);
		for(IRawPeak rawPeak : rawPeaks) {
			try {
				ScanRange scanRange = new ScanRange(rawPeak.getStartScan(), rawPeak.getStopScan());
				IChromatogramPeakWSD peak = PeakBuilderWSD.createPeak(chromatogram, scanRange, includedBackground, traces, filterWavelengths.getMarkedTraceModus());
				if(peak != null && peak.getSignalToNoiseRatio() >= settings.getMinimumSignalToNoiseRatio()) {
					peak.setDetectorDescription("Truyols Detector");
					peaks.add(peak);
				}
			} catch(Exception e) {
				logger.warn(e.getMessage());
			}
		}
		return peaks;
	}

	/**
	 * WSD peaks don't carry a stored chromatogram baseline, so only BB
	 * ("baseline-to-baseline", no background included) differs from the
	 * default valley/deconvolution behaviour.
	 */
	private static boolean includeBackground(DetectorType detectorType) {

		return detectorType != DetectorType.BB;
	}

	private static Set<Integer> toTraceIntegers(IMarkedTraces<ITrace> wavelengths) {

		return wavelengths.stream().map(t -> (int)Math.round(t.getValue())).collect(Collectors.toSet());
	}
}