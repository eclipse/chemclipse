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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IMassSpectraIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.MassSpectraIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier.BasePeakIdentifier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.IBasePeakSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumIdentifier extends AbstractMassSpectrumIdentifier {

	@Override
	public IMassSpectraIdentifierProcessingInfo identify(List<IScanMSD> massSpectrumList, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) {

		IMassSpectraIdentifierProcessingInfo processingInfo = new MassSpectraIdentifierProcessingInfo();
		//
		BasePeakIdentifier basePeakIdentifier = new BasePeakIdentifier();
		IBasePeakSettings settings;
		if(massSpectrumIdentifierSettings instanceof IBasePeakSettings) {
			settings = (IBasePeakSettings)massSpectrumIdentifierSettings;
		} else {
			settings = PreferenceSupplier.getPeakIdentifierSettings();
		}
		basePeakIdentifier.identifyMassSpectra(massSpectrumList, settings, monitor);
		processingInfo.addInfoMessage("BasePeakIdentifier", "Everything is supi.");
		//
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

		IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		return identify(massSpectrum, massSpectrumIdentifierSettings, monitor);
	}

	@Override
	public IMassSpectraIdentifierProcessingInfo identify(List<IScanMSD> massSpectra, IProgressMonitor monitor) {

		IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		return identify(massSpectra, massSpectrumIdentifierSettings, monitor);
	}
}
