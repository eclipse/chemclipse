/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Florian Ernst - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class DetectorRunnable implements IRunnableWithProgress {

	private IChromatogramSelectionMSD chromatogramSelection;
	private int detectedPeaks;
	private static final String PEAK_DETECTOR_ID = "org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution";

	public DetectorRunnable(IChromatogramSelectionMSD chromatogramSelection) {
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
			monitor.beginTask("Peak Deconvolution Detector", IProgressMonitor.UNKNOWN);
			/*
			 * Detect Peaks in current chromatogram selection.
			 */
			PeakDetectorSettings peakDetectorSettings = new PeakDetectorSettings();
			peakDetectorSettings.setSensitivity(PreferenceSupplier.getSensitivity());
			peakDetectorSettings.setMinimumSignalToNoiseRatio(PreferenceSupplier.getMinimumSignalToNoiseRatio());
			peakDetectorSettings.setMinimumPeakWidth(PreferenceSupplier.getMinimumPeakWidth());
			peakDetectorSettings.setMinimumPeakRising(PreferenceSupplier.getMinimumPeakRising());
			peakDetectorSettings.setBaselineIterations(PreferenceSupplier.getBaselineIterations());
			peakDetectorSettings.setQuantityNoiseSegments(PreferenceSupplier.getQuantityNoiseSegments());
			peakDetectorSettings.setSensitivityOfDeconvolution(PreferenceSupplier.getSensitivityOfDeconvolution());
			PeakDetectorMSD.detect(chromatogramSelection, peakDetectorSettings, PEAK_DETECTOR_ID, monitor);
			detectedPeaks = chromatogramSelection.getChromatogramMSD().getNumberOfPeaks();
			updateSelection();
		} finally {
			monitor.done();
		}
	}

	/*
	 * Updates the selection using the GUI thread.
	 */
	private void updateSelection() {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
					/*
					 * The chromatogram editor shall be reloaded to show the peaks.
					 */
					((ChromatogramSelectionMSD)chromatogramSelection).update(true);
				}
			}
		});
	}
}
