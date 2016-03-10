/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IMassSpectraIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.MassSpectraIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.internal.identifier.FileIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFileMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumIdentifier extends AbstractMassSpectrumIdentifier {

	@Override
	public IMassSpectraIdentifierProcessingInfo identify(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) {

		IMassSpectraIdentifierProcessingInfo processingInfo = new MassSpectraIdentifierProcessingInfo();
		/*
		 * Run the identification
		 */
		try {
			FileIdentifier fileIdentifier = new FileIdentifier();
			IFileMassSpectrumIdentifierSettings fileIdentifierSettings;
			if(massSpectrumIdentifierSettings instanceof IFileMassSpectrumIdentifierSettings) {
				fileIdentifierSettings = (IFileMassSpectrumIdentifierSettings)massSpectrumIdentifierSettings;
			} else {
				fileIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
			}
			//
			IMassSpectra massSpectra = fileIdentifier.runIdentification(massSpectraList, fileIdentifierSettings, monitor);
			processingInfo.setMassSpectra(massSpectra);
			processingInfo.addInfoMessage(FileIdentifier.IDENTIFIER, "Mass spectra have been identified.");
		} catch(Exception e) {
			processingInfo.addErrorMessage(FileIdentifier.IDENTIFIER, "Something has gone wrong.");
		}
		return processingInfo;
	}

	@Override
	public IMassSpectraIdentifierProcessingInfo identify(IScanMSD massSpectrum, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) {

		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		massSpectra.add(massSpectrum);
		return identify(massSpectra, massSpectrumIdentifierSettings, monitor);
	}

	@Override
	public IMassSpectraIdentifierProcessingInfo identify(IScanMSD massSpectrum, IProgressMonitor monitor) {

		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		massSpectra.add(massSpectrum);
		IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		return identify(massSpectra, massSpectrumIdentifierSettings, monitor);
	}

	@Override
	public IMassSpectraIdentifierProcessingInfo identify(List<IScanMSD> massSpectra, IProgressMonitor monitor) {

		IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		return identify(massSpectra, massSpectrumIdentifierSettings, monitor);
	}
}
