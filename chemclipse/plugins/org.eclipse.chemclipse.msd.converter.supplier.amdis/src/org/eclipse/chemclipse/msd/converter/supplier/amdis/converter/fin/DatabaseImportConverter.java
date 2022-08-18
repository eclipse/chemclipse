/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.fin;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.database.AbstractDatabaseImportConverter;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.FINReader;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class DatabaseImportConverter extends AbstractDatabaseImportConverter {

	private static final Logger logger = Logger.getLogger(DatabaseImportConverter.class);
	private static final String DESCRIPTION = "AMDIS Import (FIN)";

	@Override
	public IProcessingInfo<IMassSpectra> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages()) {
			try {
				IMassSpectraReader massSpectraReader = new FINReader();
				IMassSpectra massSpectra = massSpectraReader.read(file, monitor);
				if(massSpectra != null && !massSpectra.isEmpty()) {
					processingInfo.setProcessingResult(massSpectra);
				} else {
					processingInfo.addErrorMessage(DESCRIPTION, "No mass spectra were extracted." + file.getAbsolutePath());
				}
			} catch(Exception e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "Exception parsing the file: " + file.getAbsolutePath());
			}
		}
		return processingInfo;
	}
}
