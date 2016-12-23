/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
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

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.MassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

/**
 * THIS CLASS IS NOT SUITED FOR PRODUCTIVE USE!<br/>
 * IT IS AN TESTCLASS!
 * 
 * @author eselmeister
 */
public class TestMassSpectrumImportConverter extends AbstractMassSpectrumImportConverter {

	@Override
	public IMassSpectrumImportConverterProcessingInfo convert(File file, IProgressMonitor monitor) {

		IMassSpectrumImportConverterProcessingInfo processingInfo = new MassSpectrumImportConverterProcessingInfo();
		try {
			processingInfo.addMessages(super.validate(file));
		} catch(Exception e) {
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "MassSpectrum Import", "The file couldn't be validated.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}
}
