/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.internal.identifier.FileIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.internal.identifier.UnknownIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.MassSpectrumUnknownSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumIdentifierUnknown extends AbstractMassSpectrumIdentifier {

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		if(identifierSettings instanceof MassSpectrumUnknownSettings) {
			MassSpectrumUnknownSettings massSpectrumIdentifierSettings = (MassSpectrumUnknownSettings)identifierSettings;
			UnknownIdentifier unknownIdentifier = new UnknownIdentifier();
			unknownIdentifier.runIdentification(massSpectraList, massSpectrumIdentifierSettings);
			processingInfo.addInfoMessage(FileIdentifier.IDENTIFIER, "Mass spectra have been identified.");
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
		MassSpectrumUnknownSettings settings = PreferenceSupplier.getMassSpectrumUnknownSettings();
		return identify(massSpectra, settings, monitor);
	}

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectra, IProgressMonitor monitor) {

		MassSpectrumUnknownSettings settings = PreferenceSupplier.getMassSpectrumUnknownSettings();
		return identify(massSpectra, settings, monitor);
	}
}
