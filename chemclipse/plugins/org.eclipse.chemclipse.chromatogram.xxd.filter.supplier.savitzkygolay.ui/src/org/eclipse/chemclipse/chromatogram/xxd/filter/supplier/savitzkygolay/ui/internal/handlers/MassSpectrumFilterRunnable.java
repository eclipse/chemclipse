/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.MassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class MassSpectrumFilterRunnable implements IRunnableWithProgress {

	private static final String DESCRIPTION = "Savitzky Golay Filter Mass Spectrum";
	private static final String FILTER_ID_SAVITZKYGOLAY_MS = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.savitzkygolay.massspectrum";
	private IScanMSD massSpectrum;

	public MassSpectrumFilterRunnable(IScanMSD massSpectrum) {
		this.massSpectrum = massSpectrum;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			/*
			 * Apply the filter.
			 */
			IMassSpectrumFilterSettings massSpectrumFilterSettings = PreferenceSupplier.getMassSpectrumFilterSettings();
			final IProcessingInfo processingInfo;
			processingInfo = MassSpectrumFilter.applyFilter(massSpectrum, massSpectrumFilterSettings, FILTER_ID_SAVITZKYGOLAY_MS, monitor);
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

				MassSpectrumSelectionUpdateNotifier.fireUpdateChange(massSpectrum, true);
			}
		});
	}
}
