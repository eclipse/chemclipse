/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
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

import java.io.File;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * THIS CLASS IS NOT SUITED FOR PRODUCTIVE USE!<br/>
 * IT IS A TESTCLASS!
 */
public class TestMassSpectrumExportConverter extends AbstractMassSpectrumExportConverter {

	@Override
	public IProcessingInfo<?> convert(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		try {
			processingInfo.addMessages(super.validate(file));
			processingInfo.addMessages(super.validate(massSpectrum));
		} catch(Exception e) {
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "MassSpectrum Export", "The file couldn't be validated.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> convert(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		try {
			processingInfo.addMessages(super.validate(file));
			processingInfo.addMessages(super.validate(massSpectra));
		} catch(Exception e) {
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "MassSpectrum Export", "The file couldn't be validated.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}
}
