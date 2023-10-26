/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.quantitation;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.quantitation.AbstractQuantDBExportConverter;
import org.eclipse.chemclipse.converter.quantitation.IQuantDBExportConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.quantitation.DatabaseWriter_1000;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.quantitation.IDatabaseWriter;
import org.eclipse.core.runtime.IProgressMonitor;

public class DatabaseExportConverter extends AbstractQuantDBExportConverter implements IQuantDBExportConverter {

	private static final Logger logger = Logger.getLogger(DatabaseExportConverter.class);

	@Override
	public IProcessingInfo<File> convert(File file, IQuantitationDatabase quantitationDatabase, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		try {
			IDatabaseWriter writer = new DatabaseWriter_1000();
			writer.convert(file, quantitationDatabase, monitor);
			processingInfo.setProcessingResult(file);
		} catch(IOException e) {
			processingInfo.addErrorMessage("Quantitation Database Converter (*.ocq)", "Something has gone wrong to write the file: " + file);
			logger.warn(e);
		}
		//
		return processingInfo;
	}
}
