/*******************************************************************************
 * Copyright (c) 2014, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to simplified API, add generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.internal.identifier.FileIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumIdentifierFile extends AbstractMassSpectrumIdentifier {

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings;
		if(identifierSettings instanceof MassSpectrumIdentifierSettings) {
			massSpectrumIdentifierSettings = (MassSpectrumIdentifierSettings)identifierSettings;
		} else {
			massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		}
		try {
			FileIdentifier fileIdentifier = new FileIdentifier();
			IMassSpectra massSpectra = fileIdentifier.runIdentification(massSpectraList, massSpectrumIdentifierSettings, monitor);
			processingInfo.setProcessingResult(massSpectra);
			processingInfo.addInfoMessage(FileIdentifier.IDENTIFIER, "Mass spectra have been identified.");
		} catch(FileNotFoundException e) {
			processingInfo.addErrorMessage(FileIdentifier.IDENTIFIER, "An I/O error ocurred.");
		}
		return processingInfo;
	}
}
