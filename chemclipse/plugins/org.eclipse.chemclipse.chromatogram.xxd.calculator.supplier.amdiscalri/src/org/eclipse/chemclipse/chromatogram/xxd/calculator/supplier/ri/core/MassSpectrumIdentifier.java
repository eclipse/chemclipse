/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.impl.AlkaneIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.settings.MassSpectrumIdentifierAlkaneSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumIdentifier extends AbstractMassSpectrumIdentifier {

	private static final Logger logger = Logger.getLogger(MassSpectrumIdentifier.class);

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		//
		try {
			AlkaneIdentifier alkaneIdentifier = new AlkaneIdentifier();
			MassSpectrumIdentifierAlkaneSettings alkaneSettings;
			if(massSpectrumIdentifierSettings instanceof MassSpectrumIdentifierAlkaneSettings) {
				alkaneSettings = (MassSpectrumIdentifierAlkaneSettings)massSpectrumIdentifierSettings;
			} else {
				alkaneSettings = PreferenceSupplier.getMassSpectrumIdentifierAlkaneSettings();
			}
			/*
			 * Run the identifier.
			 */
			processingInfo.addMessages(alkaneIdentifier.runIdentification(massSpectraList, alkaneSettings, monitor));
		} catch(Exception e) {
			logger.warn(e);
			processingInfo.addErrorMessage("Alkane(s) Identifier", "Some has gone wrong.");
		}
		//
		return processingInfo;
	}
}
