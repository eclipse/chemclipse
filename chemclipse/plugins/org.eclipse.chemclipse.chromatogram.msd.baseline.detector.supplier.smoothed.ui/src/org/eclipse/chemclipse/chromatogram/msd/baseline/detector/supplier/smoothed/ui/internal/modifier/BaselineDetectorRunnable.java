/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.baseline.detector.supplier.smoothed.ui.internal.modifier;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.baseline.detector.supplier.smoothed.settings.SmoothedBaselineDetectorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.processing.IBaselineDetectorProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class BaselineDetectorRunnable implements IRunnableWithProgress {

	private static final String BASELINE_DETECTOR_ID = "org.eclipse.chemclipse.chromatogram.msd.baseline.detector.supplier.smoothed";
	private IChromatogramSelectionMSD chromatogramSelection;

	public BaselineDetectorRunnable(IChromatogramSelectionMSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Baseline Detector", IProgressMonitor.UNKNOWN);
			SmoothedBaselineDetectorSettings baselineDetectorSettings = new SmoothedBaselineDetectorSettings();
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
				}
			}
		});
	}
}
