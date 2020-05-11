/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class DetectorRunnable implements IRunnableWithProgress {

	private IChromatogramSelectionMSD chromatogramSelection;
	private int detectedPeaks;
	private static final String PEAK_DETECTOR_ID = "org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis";

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
			monitor.beginTask("Deconvolution (AMDIS)", IProgressMonitor.UNKNOWN);
			PeakDetectorMSD.detect(chromatogramSelection, PEAK_DETECTOR_ID, monitor);
			detectedPeaks = chromatogramSelection.getChromatogram().getNumberOfPeaks();
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
