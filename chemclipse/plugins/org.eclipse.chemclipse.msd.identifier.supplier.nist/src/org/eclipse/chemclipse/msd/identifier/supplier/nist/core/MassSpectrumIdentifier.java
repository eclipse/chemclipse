/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support.Identifier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumIdentifier extends AbstractMassSpectrumIdentifier {

	private static final String DESCRIPTION = "NIST-DB Identifier";

	@Override
	public IProcessingInfo identify(List<IScanMSD> massSpectrumList, IMassSpectrumIdentifierSettings identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(identifierSettings instanceof MassSpectrumIdentifierSettings) {
			try {
				MassSpectrumIdentifierSettings massSpectrumIdentifierSettings = (MassSpectrumIdentifierSettings)identifierSettings;
				Identifier identifier = new Identifier();
				IMassSpectra massSpectra = identifier.runMassSpectrumIdentification(massSpectrumList, massSpectrumIdentifierSettings, monitor);
				processingInfo.setProcessingResult(massSpectra);
			} catch(FileNotFoundException e) {
				processingInfo.addErrorMessage(DESCRIPTION, "An I/O error ocurred.");
			}
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The settings are not of type: " + PeakIdentifierSettings.class);
		}
		/*
		 * Run the identifier.
		 */
		return processingInfo;
	}

	@Override
	public IProcessingInfo identify(IScanMSD massSpectrum, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) {

		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		massSpectra.add(massSpectrum);
		return identify(massSpectra, massSpectrumIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(IScanMSD massSpectrum, IProgressMonitor monitor) {

		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		return identify(massSpectrum, massSpectrumIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(List<IScanMSD> massSpectra, IProgressMonitor monitor) {

		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		return identify(massSpectra, massSpectrumIdentifierSettings, monitor);
	}
}
