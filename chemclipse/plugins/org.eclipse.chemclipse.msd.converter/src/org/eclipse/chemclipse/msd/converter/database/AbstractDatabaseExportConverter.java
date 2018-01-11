/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.database;

import org.eclipse.chemclipse.converter.core.AbstractExportConverter;
import org.eclipse.chemclipse.msd.converter.processing.database.DatabaseExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.database.IDatabaseExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public abstract class AbstractDatabaseExportConverter extends AbstractExportConverter implements IDatabaseExportConverter {

	@Override
	public IDatabaseExportConverterProcessingInfo validate(IScanMSD massSpectrum) {

		IDatabaseExportConverterProcessingInfo processingInfo = new DatabaseExportConverterProcessingInfo();
		if(massSpectrum == null) {
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Database Export", "The is no mass spectrum to export.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}

	@Override
	public IDatabaseExportConverterProcessingInfo validate(IMassSpectra massSpectra) {

		IDatabaseExportConverterProcessingInfo processingInfo = new DatabaseExportConverterProcessingInfo();
		if(massSpectra == null) {
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Database Export", "The are no mass spectra to export.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}
}
