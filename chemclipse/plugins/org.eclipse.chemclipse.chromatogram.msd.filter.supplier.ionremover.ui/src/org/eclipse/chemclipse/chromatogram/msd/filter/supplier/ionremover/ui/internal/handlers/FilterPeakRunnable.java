/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.PeakFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class FilterPeakRunnable implements IRunnableWithProgress {

	private static final String DESCRIPTION = "Ion Remover Filter Peak";
	private static final String FILTER_ID_PEAK = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.peak";
	private IChromatogramSelectionMSD chromatogramSelectionMSD;
	private boolean useSelectedPeak = true; // default

	public FilterPeakRunnable(IChromatogramSelectionMSD chromatogramSelectionMSD, boolean useSelectedPeak) {

		this.chromatogramSelectionMSD = chromatogramSelectionMSD;
		this.useSelectedPeak = useSelectedPeak;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * The doOperation calls the execute of the chromatogram modifier
		 * method.<br/> Why not doing it directly? Model and GUI should be
		 * handled separately.
		 */
		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			/*
			 * Apply the filter.
			 */
			IPeakFilterSettings peakFilterSettings = PreferenceSupplier.getPeakFilterSettings();
			final IProcessingInfo<?> processingInfo;
			if(useSelectedPeak) {
				processingInfo = PeakFilter.applyFilter(chromatogramSelectionMSD.getSelectedPeak(), peakFilterSettings, FILTER_ID_PEAK, monitor);
			} else {
				processingInfo = PeakFilter.applyFilter(chromatogramSelectionMSD, peakFilterSettings, FILTER_ID_PEAK, monitor);
			}
			//
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
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

				chromatogramSelectionMSD.update(true);
			}
		});
	}
}
