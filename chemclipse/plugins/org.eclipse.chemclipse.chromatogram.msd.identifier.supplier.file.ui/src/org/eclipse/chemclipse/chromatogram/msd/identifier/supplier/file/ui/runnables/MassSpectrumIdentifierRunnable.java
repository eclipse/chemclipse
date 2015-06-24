/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IMassSpectraIdentifierProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;

public class MassSpectrumIdentifierRunnable implements IRunnableWithProgress {

	private static final String description = "File Mass Spectrum Identifier";
	private static final String IDENTIFIER_ID = "org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.massSpectrum";
	private IChromatogramSelectionMSD chromatogramSelection;

	public MassSpectrumIdentifierRunnable(IChromatogramSelectionMSD chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(description, IProgressMonitor.UNKNOWN);
			IScanMSD massSpectrum = chromatogramSelection.getSelectedScan();
			IMassSpectraIdentifierProcessingInfo processingInfo = MassSpectrumIdentifier.identify(massSpectrum, IDENTIFIER_ID, monitor);
			update(processingInfo, massSpectrum);
		} finally {
			monitor.done();
		}
	}

	// ---------------------------------------------------------private methods
	/*
	 * Updates the selection using the GUI thread.
	 */
	private void update(final IMassSpectraIdentifierProcessingInfo processingInfo, final IScanMSD massSpectrum) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				/*
				 * Show the processing view if error messages occurred.
				 */
				ProcessingInfoViewSupport.showErrorInfoReminder(processingInfo);
				ProcessingInfoViewSupport.updateProcessingInfoView(processingInfo);
				/*
				 * Update the chromatogram selection.
				 */
				MassSpectrumSelectionUpdateNotifier.fireUpdateChange(massSpectrum, true);
			}
		});
	}
	// ---------------------------------------------------------private methods
}
