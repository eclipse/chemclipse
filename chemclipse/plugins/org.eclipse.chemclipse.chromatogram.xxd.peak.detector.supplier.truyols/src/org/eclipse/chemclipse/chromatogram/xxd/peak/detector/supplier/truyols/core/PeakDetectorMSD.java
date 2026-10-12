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

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.AbstractPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.model.DetectorType;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.settings.PeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.support.TruyolsPeakDetector;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.PeakBuilderMSD;
import org.eclipse.chemclipse.msd.model.util.MarkedTracesSupportMSD;
import org.eclipse.chemclipse.msd.model.xic.ITotalIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.TotalIonSignalExtractor;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.traces.ITrace;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetectorMSD extends AbstractPeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorMSD.class);

	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorSettingsMSD peakDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, peakDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(peakDetectorSettings instanceof PeakDetectorSettingsMSD settings) {
				IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
				List<IChromatogramPeakMSD> peaks = detectPeaks(chromatogramSelection, settings, monitor);
				if(!peaks.isEmpty()) {
					chromatogram.getPeaks().addAll(peaks);
					chromatogram.setDirty(true);
				}
				String message = MessageFormat.format("Peaks Detected", String.valueOf(peaks.size()));
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Truyols Detector", message));
			} else {
				logger.warn("Settings is not of type: " + PeakDetectorSettingsMSD.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> validate(IChromatogramSelection chromatogramSelection, IPeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		return super.validate(chromatogramSelection, peakDetectorSettings, monitor);
	}

	public List<IChromatogramPeakMSD> detectPeaks(IChromatogramSelectionMSD chromatogramSelection, PeakDetectorSettingsMSD settings, IProgressMonitor monitor) {

		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		List<IChromatogramPeakMSD> allPeaks = new ArrayList<>();
		Collection<IMarkedTraces<ITrace>> filterIonsList = settings.getFilterIons();
		for(IMarkedTraces<ITrace> filterIons : filterIonsList) {
			ITotalScanSignals signals;
			try {
				ITotalIonSignalExtractor extractor = new TotalIonSignalExtractor(chromatogram);
				signals = extractor.getTotalIonSignals(chromatogramSelection, filterIons);
			} catch(ChromatogramIsNullException e) {
				logger.warn(e.getLocalizedMessage(), e);
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
			List<IChromatogramPeakMSD> peaks = buildPeaks(rawPeaks, chromatogram, settings, filterIons);
			if(settings.isUseIndividualTraces() && !peaks.isEmpty()) {
				Set<Integer> traces = MarkedTracesSupportMSD.getTracesAsInteger(filterIons);
				if(!traces.isEmpty()) {
					String classifier = "Trace " + traces.iterator().next();
					for(IChromatogramPeakMSD peak : peaks) {
						peak.addClassifier(classifier);
					}
				}
			}
			allPeaks.addAll(peaks);
		}
		return allPeaks;
	}

	private List<IChromatogramPeakMSD> buildPeaks(List<IRawPeak> rawPeaks, IChromatogramMSD chromatogram, PeakDetectorSettingsMSD settings, IMarkedTraces<ITrace> filterIons) {

		List<IChromatogramPeakMSD> peaks = new ArrayList<>();
		PeakType peakType = toPeakType(settings.getDetectorType());
		Set<Integer> traces = MarkedTracesSupportMSD.getTracesAsInteger(filterIons);
		for(IRawPeak rawPeak : rawPeaks) {
			try {
				ScanRange scanRange = new ScanRange(rawPeak.getStartScan(), rawPeak.getStopScan());
				IChromatogramPeakMSD peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, peakType, traces, filterIons.getMarkedTraceModus());
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

	private static PeakType toPeakType(DetectorType detectorType) {

		if(detectorType == null) {
			return PeakType.VV;
		}
		switch(detectorType) {
			case BB:
				return PeakType.BB;
			case CB:
				return PeakType.CB;
			case VV:
			default:
				return PeakType.VV;
		}
	}
}