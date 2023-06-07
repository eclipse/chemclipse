/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.massspectrum;

import org.eclipse.chemclipse.converter.core.AbstractExportConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

@SuppressWarnings("rawtypes") // TODO this can't both be IScanMSD and IMassSpectra so the generic approach is likely false
public abstract class AbstractMassSpectrumExportConverter extends AbstractExportConverter implements IMassSpectrumExportConverter {

	@Override
	public IProcessingInfo<?> validate(IScanMSD massSpectrum) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		if(massSpectrum == null) {
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Mass Spectra Export", "The is no mass spectrum to export.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> validate(IMassSpectra massSpectra) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		if(massSpectra == null) {
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Mass Spectra Export", "The are no mass spectra to export.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}
}
