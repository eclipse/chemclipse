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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.PeakQuantifier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.IPeakQuantifierProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;

public class QuantifySelectedPeakRunnable implements IRunnableWithProgress {

	private IChromatogramSelectionMSD chromatogramSelection;
	private static final String PEAK_QUANTIFIER_ID = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.peak";

	public QuantifySelectedPeakRunnable(IChromatogramSelectionMSD chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Peak Quantifier", IProgressMonitor.UNKNOWN);
			//
			IPeakMSD peak = chromatogramSelection.getSelectedPeak();
			IPeakQuantifierProcessingInfo processingInfo = PeakQuantifier.quantify(peak, PEAK_QUANTIFIER_ID, monitor);
			updateSelection(processingInfo);
		} finally {
			monitor.done();
		}
	}

	// ---------------------------------------------------------private methods
	/*
	 * Updates the selection using the GUI thread.
	 */
	private void updateSelection(final IPeakQuantifierProcessingInfo processingInfo) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				/*
				 * Show the processing view if error messages occurred.
				 */
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						ProcessingInfoViewSupport.showErrorInfoReminder(processingInfo);
					}
				});
				ProcessingInfoViewSupport.updateProcessingInfoView(processingInfo);
				//
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
