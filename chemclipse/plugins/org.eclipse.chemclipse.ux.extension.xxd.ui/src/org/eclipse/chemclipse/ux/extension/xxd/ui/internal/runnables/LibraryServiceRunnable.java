/*******************************************************************************
 * Copyright (c) 2017, 2018, 2019 Lablicate GmbH.
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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.identifier.library.LibraryService;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class LibraryServiceRunnable implements IRunnableWithProgress {

	private IIdentificationTarget identificationTarget;
	private IScanMSD libraryMassSpectrum;

	public LibraryServiceRunnable(IIdentificationTarget identificationTarget) {

		this.identificationTarget = identificationTarget;
	}

	public IScanMSD getLibraryMassSpectrum() {

		return libraryMassSpectrum;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Library Service", IProgressMonitor.UNKNOWN);
			try {
				IProcessingInfo processingInfo = LibraryService.identify(identificationTarget, monitor);
				IMassSpectra massSpectra = (IMassSpectra)processingInfo.getProcessingResult();
				if(massSpectra.size() > 0) {
					libraryMassSpectrum = massSpectra.getMassSpectrum(1);
				}
			} catch(Exception e) {
				e.printStackTrace();
				libraryMassSpectrum = null;
			}
		} finally {
			monitor.done();
		}
	}
}
