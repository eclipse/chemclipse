/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.support.PeakBuilderCSD;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.support.BackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IBackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.support.PeakBuilderMSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.support.PeakBuilderWSD;

public class ManualPeakDetector {

	private static final String DETECTOR_DESCRIPTION = "Manual";
	private static final String CHROMATOGRAM_MUST_NOT_BE_NULL = "The chromatogram instance must be not null.";

	/**
	 * Returns a peak calculated by the given values.
	 * 
	 * @return IPeak
	 */
	public IChromatogramPeakMSD calculatePeak(IChromatogramMSD chromatogram, int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) throws PeakException {

		if(chromatogram == null) {
			throw new PeakException(CHROMATOGRAM_MUST_NOT_BE_NULL);
		}
		/*
		 * Create the peak.
		 */
		int startScan = chromatogram.getScanNumber(startRetentionTime);
		int stopScan = chromatogram.getScanNumber(stopRetentionTime);
		IScanRange scanRange = new ScanRange(startScan, stopScan);
		IBackgroundAbundanceRange backgroundAbundanceRange = new BackgroundAbundanceRange(startAbundance, stopAbundance);
		/*
		 * Return the peak or throw an exception.
		 */
		IChromatogramPeakMSD peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, backgroundAbundanceRange, true);
		if(peak != null) {
			peak.setDetectorDescription(DETECTOR_DESCRIPTION);
		}
		return peak;
	}

	public IChromatogramPeakCSD calculatePeak(IChromatogramCSD chromatogram, int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) throws PeakException {

		if(chromatogram == null) {
			throw new PeakException(CHROMATOGRAM_MUST_NOT_BE_NULL);
		}
		/*
		 * Create the peak.
		 */
		int startScan = chromatogram.getScanNumber(startRetentionTime);
		int stopScan = chromatogram.getScanNumber(stopRetentionTime);
		IScanRange scanRange = new ScanRange(startScan, stopScan);
		IBackgroundAbundanceRange backgroundAbundanceRange = new BackgroundAbundanceRange(startAbundance, stopAbundance);
		/*
		 * Return the peak or throw an exception.
		 */
		IChromatogramPeakCSD peak = PeakBuilderCSD.createPeak(chromatogram, scanRange, backgroundAbundanceRange, true);
		if(peak != null) {
			peak.setDetectorDescription(DETECTOR_DESCRIPTION);
		}
		return peak;
	}

	public IChromatogramPeakWSD calculatePeak(IChromatogramWSD chromatogram, int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) throws PeakException {

		if(chromatogram == null) {
			throw new PeakException(CHROMATOGRAM_MUST_NOT_BE_NULL);
		}
		/*
		 * Create the peak.
		 */
		int startScan = chromatogram.getScanNumber(startRetentionTime);
		int stopScan = chromatogram.getScanNumber(stopRetentionTime);
		IScanRange scanRange = new ScanRange(startScan, stopScan);
		IBackgroundAbundanceRange backgroundAbundanceRange = new BackgroundAbundanceRange(startAbundance, stopAbundance);
		/*
		 * Return the peak or throw an exception.
		 */
		IChromatogramPeakWSD peak = PeakBuilderWSD.createPeak(chromatogram, scanRange, backgroundAbundanceRange, true);
		if(peak != null) {
			peak.setDetectorDescription(DETECTOR_DESCRIPTION);
		}
		return peak;
	}
}
