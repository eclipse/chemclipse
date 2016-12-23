/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.PeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.IFirstDerivativePeakDetectorCSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.IFirstDerivativePeakDetectorMSDSettings;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class DetectorRunnable implements IRunnableWithProgress {

	private static final String PEAK_DETECTOR_MSD_ID = "org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.firstderivative";
	private static final String PEAK_DETECTOR_CSD_ID = "org.eclipse.chemclipse.chromatogram.csd.peak.detector.supplier.firstderivative";
	//
	private IChromatogramSelection chromatogramSelection;
	private int detectedPeaks;

	public DetectorRunnable(IChromatogramSelection chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	/**
	 * Returns the number of detected peaks.
	 */
	public int getDetectedPeaks() {

		return detectedPeaks;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Peak Detector First Derivative", IProgressMonitor.UNKNOWN);
			/*
			 * Detect Peaks in actual chromatogram selection.
			 */
			if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
				/*
				 * MSD
				 */
				final ChromatogramSelectionMSD chromatogramSelectionMSD = (ChromatogramSelectionMSD)chromatogramSelection;
				IFirstDerivativePeakDetectorMSDSettings peakDetectorSettings = PreferenceSupplier.getPeakDetectorMSDSettings();
				PeakDetectorMSD.detect(chromatogramSelectionMSD, peakDetectorSettings, PEAK_DETECTOR_MSD_ID, monitor);
				detectedPeaks = chromatogramSelectionMSD.getChromatogramMSD().getNumberOfPeaks();
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						chromatogramSelectionMSD.update(true);
					}
				});
			} else if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
				/*
				 * FID
				 */
				final ChromatogramSelectionCSD chromatogramSelectionCSD = (ChromatogramSelectionCSD)chromatogramSelection;
				IFirstDerivativePeakDetectorCSDSettings peakDetectorSettings = PreferenceSupplier.getPeakDetectorCSDSettings();
				PeakDetectorCSD.detect(chromatogramSelectionCSD, peakDetectorSettings, PEAK_DETECTOR_CSD_ID, monitor);
				detectedPeaks = chromatogramSelectionCSD.getChromatogramCSD().getNumberOfPeaks();
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						chromatogramSelectionCSD.update(true);
					}
				});
			}
		} finally {
			monitor.done();
		}
	}
}
