/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.database.AbstractDatabaseImportConverter;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.io.MassSpectraReader;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.jcampdx.internal.converter.SpecificationValidator;
import org.eclipse.core.runtime.IProgressMonitor;

public class DatabaseImportConverterJDL extends AbstractDatabaseImportConverter {

	private static final Logger logger = Logger.getLogger(DatabaseImportConverterJDL.class);
	private static final String DESCRIPTION = "JDL Library";

	@Override
	public IProcessingInfo<IMassSpectra> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		/*
		 * Checks if the file is null or empty ...
		 */
		IProcessingInfo<File> processingInfoValidate = super.validate(file);
		if(processingInfoValidate.hasErrorMessages()) {
			processingInfo.addMessages(processingInfoValidate);
		} else {
			try {
				file = SpecificationValidator.validateSpecification(file, "JDL");
				IMassSpectraReader massSpectraReader = new MassSpectraReader();
				IMassSpectra massSpectra = massSpectraReader.read(file, monitor);
				if(massSpectra != null && massSpectra.size() > 0) {
					processingInfo.setProcessingResult(massSpectra);
				} else {
					processingInfo.addErrorMessage(DESCRIPTION, "No mass spectra are stored." + file.getAbsolutePath());
				}
			} catch(FileNotFoundException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "The file couldn't be found: " + file.getAbsolutePath());
			} catch(FileIsNotReadableException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "The file is not readable: " + file.getAbsolutePath());
			} catch(FileIsEmptyException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "The file is empty: " + file.getAbsolutePath());
			} catch(IOException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "Something has gone completely wrong: " + file.getAbsolutePath());
			}
		}
		return processingInfo;
	}
}
