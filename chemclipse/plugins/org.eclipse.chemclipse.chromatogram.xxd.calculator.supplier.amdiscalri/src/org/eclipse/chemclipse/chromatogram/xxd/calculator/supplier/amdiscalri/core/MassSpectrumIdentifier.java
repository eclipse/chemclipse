/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.AlkaneIdentifier;
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

		IProcessingInfo<IMassSpectra> processingInfo;
		AlkaneIdentifier alkaneIdentifier = new AlkaneIdentifier();
		try {
			//
			processingInfo = alkaneIdentifier.runIdentification(massSpectraList, massSpectrumIdentifierSettings, monitor);
		} catch(Exception e) {
			logger.warn(e);
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage("Alkane Identifier", "Some has gone wrong.");
		}
		return processingInfo;
	}
}
