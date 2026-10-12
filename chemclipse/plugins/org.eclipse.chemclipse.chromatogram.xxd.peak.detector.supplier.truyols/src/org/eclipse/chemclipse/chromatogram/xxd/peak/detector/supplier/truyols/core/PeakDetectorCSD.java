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
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.AbstractPeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.model.DetectorType;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.settings.PeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.support.TruyolsPeakDetector;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.support.PeakBuilderCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetectorCSD extends AbstractPeakDetectorCSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorCSD.class);

	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionCSD chromatogramSelection, IPeakDetectorSettingsCSD peakDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, peakDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(peakDetectorSettings instanceof PeakDetectorSettingsCSD settings) {
				IChromatogramCSD chromatogram = chromatogramSelection.getChromatogram();
				List<IChromatogramPeakCSD> peaks = detectPeaks(chromatogramSelection, settings, monitor);
				if(!peaks.isEmpty()) {
					chromatogram.getPeaks().addAll(peaks);
					chromatogram.setDirty(true);
				}
				String message = MessageFormat.format("Peaks Detected", String.valueOf(peaks.size()));
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Truyols Detector", message));
			} else {
				logger.warn("Settings is not of type: " + PeakDetectorSettingsCSD.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> validate(IChromatogramSelection chromatogramSelection, IPeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		return super.validate(chromatogramSelection, peakDetectorSettings, monitor);
	}

	public List<IChromatogramPeakCSD> detectPeaks(IChromatogramSelectionCSD chromatogramSelection, PeakDetectorSettingsCSD settings, IProgressMonitor monitor) {

		ITotalScanSignals signals = new TotalScanSignals(chromatogramSelection);
		int startScan = signals.getStartScan();
		int size = signals.size();
		double[] values = new double[size];
		int index = 0;
		for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
			values[index++] = signal.getTotalSignal();
		}

		List<IRawPeak> rawPeaks = TruyolsPeakDetector.detect(values, startScan, settings.getDerivativeOption(), settings.getWindowSize(), settings.getPolynomialOrder(), settings.getNoiseFactor());
		return buildPeaks(rawPeaks, chromatogramSelection.getChromatogram(), settings);
	}

	private List<IChromatogramPeakCSD> buildPeaks(List<IRawPeak> rawPeaks, IChromatogramCSD chromatogram, PeakDetectorSettingsCSD settings) {

		List<IChromatogramPeakCSD> peaks = new ArrayList<>();
		PeakType peakType = toPeakType(settings.getDetectorType());
		for(IRawPeak rawPeak : rawPeaks) {
			try {
				ScanRange scanRange = new ScanRange(rawPeak.getStartScan(), rawPeak.getStopScan());
				IChromatogramPeakCSD peak = PeakBuilderCSD.createPeak(chromatogram, scanRange, peakType);
				if(peak != null && peak.getSignalToNoiseRatio() >= settings.getMinimumSignalToNoiseRatio()) {
					peak.setDetectorDescription("Truyols Detector");
					peaks.add(peak);
				}
			} catch(PeakException e) {
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