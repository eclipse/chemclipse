/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.support.PeakProcessorSupport;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class DetectorByFileRunnable implements IRunnableWithProgress {

	private IChromatogramSelectionMSD chromatogramSelection;
	private File file;

	public DetectorByFileRunnable(IChromatogramSelectionMSD chromatogramSelection, File file) {
		this.chromatogramSelection = chromatogramSelection;
		this.file = file;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Deconvolution (AMDIS) - Read ELU", IProgressMonitor.UNKNOWN);
			PeakProcessorSupport peakProcessorSupport = new PeakProcessorSupport();
			PeakDetectorSettings peakDetectorSettings = PreferenceSupplier.getPeakDetectorSettings();
			peakProcessorSupport.extractEluFileAndSetPeaks(chromatogramSelection, file, peakDetectorSettings, monitor);
			updateSelection();
		} finally {
			monitor.done();
		}
	}

	// ---------------------------------------------------------private methods
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
	// ---------------------------------------------------------private methods
}
