/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - find the best matching service and use that.
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.library.ILibraryService;
import org.eclipse.chemclipse.chromatogram.msd.identifier.library.LibraryService;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class LibraryServiceRunnable implements IRunnableWithProgress {

	private final IIdentificationTarget identificationTarget;
	private IScanMSD libraryMassSpectrum;
	private final List<ILibraryService> libraryServices = new ArrayList<>();

	public LibraryServiceRunnable(IIdentificationTarget identificationTarget) {
		this.identificationTarget = identificationTarget;
		try {
			List<String> ids = LibraryService.getLibraryServiceSupport().getAvailableIdentifierIds();
			for(String id : ids) {
				ILibraryService libraryService = LibraryService.getLibraryService(id);
				if(libraryService != null && libraryService.accepts(identificationTarget)) {
					libraryServices.add(libraryService);
				}
			}
		} catch(NoIdentifierAvailableException e) {
			// never mind then
		}
	}

	public IScanMSD getLibraryMassSpectrum() {

		return libraryMassSpectrum;
	}

	public boolean mustRun() {

		return libraryServices.size() > 0;
	}

	public boolean requireProgressMonitor() {

		for(ILibraryService s : libraryServices) {
			if(s.requireProgressMonitor()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		if(libraryServices.isEmpty()) {
			return;
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Library Service", 100 * libraryServices.size());
		for(ILibraryService libraryService : libraryServices) {
			try {
				IProcessingInfo<IMassSpectra> processingInfo = libraryService.identify(identificationTarget, subMonitor.split(100));
				IMassSpectra massSpectra = processingInfo.getProcessingResult();
				if(massSpectra != null && massSpectra.size() > 0) {
					libraryMassSpectrum = massSpectra.getMassSpectrum(1);
					break;
				}
			} catch(Exception e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, getClass().getName(), "Fetching mass spectrum failed!", e));
			}
		}
	}
}
