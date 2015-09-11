/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.processing.IBaselineDetectorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class DetectorRunnable implements IRunnableWithProgress {

	private static final String BASELINE_DETECTOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.snip";
	private IChromatogramSelection chromatogramSelection;

	public DetectorRunnable(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("SNIP Baseline Detector", IProgressMonitor.UNKNOWN);
			IBaselineDetectorSettings baselineDetectorSettings = PreferenceSupplier.getBaselineDetectorSettings();
			IBaselineDetectorProcessingInfo processingInfo = BaselineDetector.setBaseline(chromatogramSelection, baselineDetectorSettings, BASELINE_DETECTOR_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, true);
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
					((ChromatogramSelectionMSD)chromatogramSelection).update(false);
				} else if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
					((ChromatogramSelectionCSD)chromatogramSelection).update(false);
				}
			}
		});
	}
	// ---------------------------------------------------------private methods
}
