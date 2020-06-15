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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.PeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.PeakDetectorWSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core.FirstDerivativePeakDetector;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class DetectorRunnable implements IRunnableWithProgress {

	private static final String PEAK_DETECTOR_MSD_ID = "org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.firstderivative";
	private static final String PEAK_DETECTOR_CSD_ID = "org.eclipse.chemclipse.chromatogram.csd.peak.detector.supplier.firstderivative";
	private static final String PEAK_DETECTOR_WSD_ID = "org.eclipse.chemclipse.chromatogram.wsd.peak.detector.supplier.firstderivative";
	//
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;
	private int detectedPeaks;

	@SuppressWarnings("rawtypes")
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
			monitor.beginTask(FirstDerivativePeakDetector.DETECTOR_DESCRIPTION, IProgressMonitor.UNKNOWN);
			/*
			 * Detect Peaks in actual chromatogram selection.
			 */
			if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
				/*
				 * MSD
				 */
				ChromatogramSelectionMSD chromatogramSelectionMSD = (ChromatogramSelectionMSD)chromatogramSelection;
				PeakDetectorMSD.detect(chromatogramSelectionMSD, PEAK_DETECTOR_MSD_ID, monitor);
				detectedPeaks = chromatogramSelectionMSD.getChromatogramMSD().getNumberOfPeaks();
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						chromatogramSelectionMSD.update(true);
					}
				});
			} else if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
				/*
				 * CSD
				 */
				ChromatogramSelectionCSD chromatogramSelectionCSD = (ChromatogramSelectionCSD)chromatogramSelection;
				PeakDetectorCSD.detect(chromatogramSelectionCSD, PEAK_DETECTOR_CSD_ID, monitor);
				detectedPeaks = chromatogramSelectionCSD.getChromatogramCSD().getNumberOfPeaks();
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						chromatogramSelectionCSD.update(true);
					}
				});
			} else if(chromatogramSelection instanceof ChromatogramSelectionWSD) {
				/*
				 * WSD
				 */
				ChromatogramSelectionWSD chromatogramSelectionWSD = (ChromatogramSelectionWSD)chromatogramSelection;
				PeakDetectorWSD.detect(chromatogramSelectionWSD, PEAK_DETECTOR_WSD_ID, monitor);
				detectedPeaks = chromatogramSelectionWSD.getChromatogramWSD().getNumberOfPeaks();
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						chromatogramSelectionWSD.update(true);
					}
				});
			}
		} finally {
			monitor.done();
		}
	}
}
