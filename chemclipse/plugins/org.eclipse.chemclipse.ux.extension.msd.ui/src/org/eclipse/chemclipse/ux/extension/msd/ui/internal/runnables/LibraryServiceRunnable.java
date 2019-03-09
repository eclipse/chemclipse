/*******************************************************************************
 * Copyright (c) 2016, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.identifier.library.LibraryService;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.msd.ui.views.AbstractMassSpectrumLibraryView;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class LibraryServiceRunnable implements IRunnableWithProgress {

	private AbstractMassSpectrumLibraryView massSpectrumLibraryView;
	private IScanMSD unknownMassSpectrum;
	private IIdentificationTarget identificationTarget;

	public LibraryServiceRunnable(AbstractMassSpectrumLibraryView massSpectrumLibraryView, IScanMSD unknownMassSpectrum, IIdentificationTarget identificationTarget) {

		this.massSpectrumLibraryView = massSpectrumLibraryView;
		this.unknownMassSpectrum = unknownMassSpectrum;
		this.identificationTarget = identificationTarget;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Library Service", IProgressMonitor.UNKNOWN);
			try {
				IProcessingInfo<IMassSpectra> processingInfo = LibraryService.identify(identificationTarget, monitor);
				IMassSpectra massSpectra = processingInfo.getProcessingResult();
				if(massSpectra.size() > 0) {
					IScanMSD libraryMassSpectrum = massSpectra.getMassSpectrum(1);
					updateSelection(unknownMassSpectrum, libraryMassSpectrum, true);
				}
			} catch(Exception e) {
				updateSelection(unknownMassSpectrum, null, true);
			}
		} finally {
			monitor.done();
		}
	}

	// ---------------------------------------------------------private methods
	/*
	 * Updates the selection using the GUI thread.
	 */
	private void updateSelection(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum, boolean forceReload) {

		DisplayUtils.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				massSpectrumLibraryView.update(unknownMassSpectrum, libraryMassSpectrum, forceReload);
			}
		});
	}
	// ---------------------------------------------------------private methods
}
