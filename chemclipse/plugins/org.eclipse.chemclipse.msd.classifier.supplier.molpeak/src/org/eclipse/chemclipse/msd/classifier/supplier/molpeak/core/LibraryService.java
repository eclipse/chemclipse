/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API / generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.core;

import org.eclipse.chemclipse.chromatogram.msd.identifier.library.AbstractLibraryService;
import org.eclipse.chemclipse.chromatogram.msd.identifier.library.ILibraryService;
import org.eclipse.chemclipse.model.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier.BasePeakIdentifier;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class LibraryService extends AbstractLibraryService implements ILibraryService {

	private final BasePeakIdentifier basePeakIdentifier;

	public LibraryService() {

		basePeakIdentifier = new BasePeakIdentifier();
	}

	@Override
	public IProcessingInfo<IMassSpectra> identify(IIdentificationTarget identificationTarget, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		try {
			monitor.subTask("Base Peak Identifier - get reference mass spectrum");
			validateIdentificationTarget(identificationTarget);
			IMassSpectra massSpectra = basePeakIdentifier.getMassSpectra(identificationTarget);
			processingInfo.setProcessingResult(massSpectra);
		} catch(ValueMustNotBeNullException e) {
			processingInfo.addErrorMessage("Base Peak Identifier", "The identification target is not available.");
		}
		//
		return processingInfo;
	}

	@Override
	public boolean accepts(IIdentificationTarget identificationTarget) {

		return basePeakIdentifier.isValid(identificationTarget);
	}

	@Override
	public boolean requireProgressMonitor() {

		return false;
	}
}
