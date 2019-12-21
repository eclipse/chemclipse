/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to simplified API, add generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier.BasePeakIdentifier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumIdentifier extends AbstractMassSpectrumIdentifier {

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectrumList, IMassSpectrumIdentifierSettings identifierSettings, IProgressMonitor monitor) {

		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings;
		if(identifierSettings instanceof MassSpectrumIdentifierSettings) {
			massSpectrumIdentifierSettings = (MassSpectrumIdentifierSettings)identifierSettings;
		} else {
			massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		}
		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		BasePeakIdentifier basePeakIdentifier = new BasePeakIdentifier();
		basePeakIdentifier.identifyMassSpectra(massSpectrumList, massSpectrumIdentifierSettings, monitor);
		return processingInfo;
	}
}
