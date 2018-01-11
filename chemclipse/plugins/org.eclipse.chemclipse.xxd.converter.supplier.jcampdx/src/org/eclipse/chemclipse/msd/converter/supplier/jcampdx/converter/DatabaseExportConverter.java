/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.jcampdx.converter;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.database.AbstractDatabaseExportConverter;
import org.eclipse.chemclipse.msd.converter.processing.database.DatabaseExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.database.IDatabaseExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class DatabaseExportConverter extends AbstractDatabaseExportConverter {

	@Override
	public IDatabaseExportConverterProcessingInfo convert(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) {

		return getProcessingInfo();
	}

	@Override
	public IDatabaseExportConverterProcessingInfo convert(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) {

		return getProcessingInfo();
	}

	private IDatabaseExportConverterProcessingInfo getProcessingInfo() {

		IDatabaseExportConverterProcessingInfo processingInfo = new DatabaseExportConverterProcessingInfo();
		processingInfo.addErrorMessage("JCAMP-DX Library", "The JCAMP-DX converter has no capabilities to export a library.");
		return processingInfo;
	}
}
