/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.custom;

import java.util.Set;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.support.PeakBuilderCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.PeakBuilderMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;

/**
 * TODO
 * Move template methods to ChemClipse and merge with the template peak detector.
 *
 */
public class PeakDetectorSupport {

	private static final Logger logger = Logger.getLogger(PeakDetectorSupport.class);
	private static final String DETECTOR_DESCRIPTION = "Peak Detector (UX)";

	public static IPeak extractPeakByRetentionTime(IChromatogram<? extends IPeak> chromatogram, int startRetentionTime, int stopRetentionTime, boolean includeBackground, boolean optimizeRange, Set<Integer> traces) {

		int startScan = chromatogram.getScanNumber(startRetentionTime);
		int stopScan = chromatogram.getScanNumber(stopRetentionTime);
		return extractPeakByScanRange(chromatogram, startScan, stopScan, includeBackground, optimizeRange, traces);
	}

	public static IPeak extractPeakByScanRange(IChromatogram<? extends IPeak> chromatogram, int startScan, int stopScan, boolean includeBackground, boolean optimizeRange, Set<Integer> traces) {

		IPeak peak = null;
		//
		try {
			if(startScan > 0 && startScan < stopScan) {
				/*
				 * Get the scan range.
				 */
				IScanRange scanRange;
				if(optimizeRange) {
					scanRange = optimizeRange(chromatogram, startScan, stopScan, traces);
				} else {
					scanRange = new ScanRange(startScan, stopScan);
				}
				/*
				 * Try to create a peak.
				 */
				if(chromatogram instanceof IChromatogramMSD) {
					IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
					if(traces.size() > 0) {
						/**
						 * Must be called with 'exclude' mode, so given ions will be 'excluded' from AbstractScan#removeIons.
						 */
						peak = PeakBuilderMSD.createPeak(chromatogramMSD, scanRange, includeBackground, traces, IMarkedIons.IonMarkMode.EXCLUDE);
					} else {
						peak = PeakBuilderMSD.createPeak(chromatogramMSD, scanRange, includeBackground);
					}
					peak.setDetectorDescription(DETECTOR_DESCRIPTION);
				} else if(chromatogram instanceof IChromatogramCSD) {
					IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
					peak = PeakBuilderCSD.createPeak(chromatogramCSD, scanRange, includeBackground);
					peak.setDetectorDescription(DETECTOR_DESCRIPTION);
				} else if(chromatogram instanceof IChromatogramWSD) {
					logger.info("Handling WSD data is not supported yet");
				}
			}
		} catch(PeakException e) {
			logger.warn(e);
		}
		//
		return peak;
	}

	private static IScanRange optimizeRange(IChromatogram<? extends IPeak> chromatogram, int startScan, int stopScan, Set<Integer> traces) {

		int scanWidth = stopScan - startScan + 1;
		int partLength = scanWidth / 4;
		/*
		 * Assume max value in ~ in the middle.
		 */
		float maxSignalCenter = Float.MIN_VALUE;
		int centerScan = startScan;
		for(int i = startScan + partLength; i <= stopScan - partLength; i++) {
			float signal = getScanSignal(chromatogram, i, traces);
			if(signal > maxSignalCenter) {
				maxSignalCenter = signal;
				centerScan = i;
			}
		}
		/*
		 * Left border optimization
		 */
		float minSignalLeft = Float.MAX_VALUE;
		int startScanOptimized = startScan;
		for(int i = startScan; i < centerScan; i++) {
			float signal = getScanSignal(chromatogram, i, traces);
			if(signal < minSignalLeft) {
				minSignalLeft = signal;
				startScanOptimized = i;
			}
		}
		/*
		 * Right border optimization
		 */
		float minSignalRight = Float.MAX_VALUE;
		int stopScanOptimized = stopScan;
		for(int i = stopScan; i > centerScan; i--) {
			float signal = getScanSignal(chromatogram, i, traces);
			if(signal < minSignalRight) {
				minSignalRight = signal;
				stopScanOptimized = i;
			}
		}
		//
		return new ScanRange(startScanOptimized, stopScanOptimized);
	}

	private static float getScanSignal(IChromatogram<? extends IPeak> chromatogram, int scanNumber, Set<Integer> traces) {

		float scanSignal = 0.0f;
		IScan scan = chromatogram.getScan(scanNumber);
		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
			for(int trace : traces) {
				scanSignal += extractedIonSignal.getAbundance(trace);
			}
		} else {
			scanSignal = scan.getTotalSignal();
		}
		return scanSignal;
	}
}
