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

import org.eclipse.chemclipse.converter.quantitation.AbstractQuantDBImportConverter;
import org.eclipse.chemclipse.converter.quantitation.IQuantDBImportConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.quantitation.DatabaseReader_1000;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.quantitation.IDatabaseReader;
import org.eclipse.core.runtime.IProgressMonitor;

public class DatabaseImportConverter extends AbstractQuantDBImportConverter<IQuantitationDatabase> implements IQuantDBImportConverter<IQuantitationDatabase> {

	private static final Logger logger = Logger.getLogger(DatabaseImportConverter.class);

	@Override
	public IProcessingInfo<IQuantitationDatabase> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IQuantitationDatabase> processingInfo = new ProcessingInfo<>();
		try {
			IDatabaseReader reader = new DatabaseReader_1000();
			IQuantitationDatabase quantitationDatabase = reader.convert(file, monitor);
			processingInfo.setProcessingResult(quantitationDatabase);
		} catch(IOException e) {
			processingInfo.addErrorMessage("Quantitation Database Converter (*.ocq)", "Something has gone wrong to read the file: " + file);
			logger.warn(e);
		}
		//
		return processingInfo;
	}
}
