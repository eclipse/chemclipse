/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.csv.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.database.AbstractDatabaseExportConverter;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.csv.internal.converter.SpecificationValidator;
import org.eclipse.chemclipse.xxd.converter.supplier.csv.io.core.MassSpectrumExtendedWriter;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumIdentificationExportConverter extends AbstractDatabaseExportConverter {

	private static final Logger logger = Logger.getLogger(MassSpectrumIdentificationExportConverter.class);
	private static final String DESCRIPTION = "CSV Mass Spectrum List Export";

	@Override
	public IProcessingInfo<File> convert(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) {

		MassSpectra massSpectra = new MassSpectra();
		massSpectra.addMassSpectrum(massSpectrum);
		return convert(file, massSpectra, append, monitor);
	}

	@Override
	public IProcessingInfo<File> convert(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		/*
		 * Checks that file and mass spectra are not null.
		 */
		file = SpecificationValidator.validateSpecification(file, "csv");
		IProcessingInfo<?> processingInfoValidate = validate(file, massSpectra);
		if(processingInfoValidate.hasErrorMessages()) {
			processingInfo.addMessages(processingInfoValidate);
		} else {
			try {
				/*
				 * Convert the mass spectra.
				 */
				IMassSpectraWriter massSpectraWriter = new MassSpectrumExtendedWriter();
				massSpectraWriter.write(file, massSpectra, append, monitor);
				processingInfo.setProcessingResult(file);
			} catch(FileNotFoundException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "The file couldn't be found: " + file.getAbsolutePath());
			} catch(FileIsNotWriteableException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "The file is not writeable: " + file.getAbsolutePath());
			} catch(IOException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "Something has gone completely wrong: " + file.getAbsolutePath());
			}
		}
		return processingInfo;
	}

	private IProcessingInfo<?> validate(File file, IMassSpectra massSpectra) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(super.validate(file));
		processingInfo.addMessages(super.validate(massSpectra));
		return processingInfo;
	}
}