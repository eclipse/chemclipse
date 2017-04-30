/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.core;

import org.eclipse.chemclipse.chromatogram.msd.identifier.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.msd.identifier.library.AbstractLibraryService;
import org.eclipse.chemclipse.chromatogram.msd.identifier.library.ILibraryService;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.ILibraryServiceProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.LibraryServiceProcessingInfo;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier.BasePeakIdentifier;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;

public class LibraryService extends AbstractLibraryService implements ILibraryService {

	private BasePeakIdentifier basePeakIdentifier;

	public LibraryService() {
		basePeakIdentifier = new BasePeakIdentifier();
	}

	@Override
	public ILibraryServiceProcessingInfo identify(IIdentificationTarget identificationTarget, IProgressMonitor monitor) {

		ILibraryServiceProcessingInfo processingInfo = new LibraryServiceProcessingInfo();
		try {
			monitor.subTask("Base Peak Identifier - get reference mass spectrum");
			validateIdentificationTarget(identificationTarget);
			IMassSpectra massSpectra = basePeakIdentifier.getMassSpectra(identificationTarget, monitor);
			processingInfo.setMassSpectra(massSpectra);
		} catch(ValueMustNotBeNullException e) {
			processingInfo.addErrorMessage("Base Peak Identifier", "The identification target is not available.");
		}
		//
		return processingInfo;
	}
}
