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
 * Christoph Läubrich - fix generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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

public class MassSpectrumIdentifier extends AbstractMassSpectrumIdentifier {

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		if(identifierSettings instanceof MassSpectrumIdentifierSettings) {
			try {
				MassSpectrumIdentifierSettings massSpectrumIdentifierSettings = (MassSpectrumIdentifierSettings)identifierSettings;
				FileIdentifier fileIdentifier = new FileIdentifier();
				IMassSpectra massSpectra = fileIdentifier.runIdentification(massSpectraList, massSpectrumIdentifierSettings, monitor);
				processingInfo.setProcessingResult(massSpectra);
				processingInfo.addInfoMessage(FileIdentifier.IDENTIFIER, "Mass spectra have been identified.");
			} catch(FileNotFoundException e) {
				processingInfo.addErrorMessage(FileIdentifier.IDENTIFIER, "An I/O error ocurred.");
			}
		} else {
			processingInfo.addErrorMessage(FileIdentifier.IDENTIFIER, "The settings are not of type: " + MassSpectrumIdentifierSettings.class);
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IMassSpectra> identify(IScanMSD massSpectrum, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) {

		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		massSpectra.add(massSpectrum);
		return identify(massSpectra, massSpectrumIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo<IMassSpectra> identify(IScanMSD massSpectrum, IProgressMonitor monitor) {

		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		massSpectra.add(massSpectrum);
		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		return identify(massSpectra, massSpectrumIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectra, IProgressMonitor monitor) {

		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		return identify(massSpectra, massSpectrumIdentifierSettings, monitor);
	}
}
