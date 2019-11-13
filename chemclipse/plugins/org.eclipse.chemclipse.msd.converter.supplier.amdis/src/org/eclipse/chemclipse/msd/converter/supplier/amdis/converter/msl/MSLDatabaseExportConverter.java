/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.database.AbstractDatabaseExportConverter;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.internal.converter.SpecificationValidatorMSL;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.MSLWriter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * NAME FIELD:
 * If the mass spectrum is a type of IRegularLibraryMassSpectrum, than getLibraryInformation().getName() will be used,
 * otherwise massSpectrum.getIdentifier().
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MSLDatabaseExportConverter extends AbstractDatabaseExportConverter {

	private static final Logger logger = Logger.getLogger(MSLDatabaseExportConverter.class);
	private static final String DESCRIPTION = "AMDIS MSL MassSpectrum Export";

	@Override
	public IProcessingInfo convert(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) {

		file = SpecificationValidatorMSL.validateSpecification(file);
		IProcessingInfo processingInfo = validate(file, massSpectrum);
		if(!processingInfo.hasErrorMessages()) {
			try {
				/*
				 * Convert the mass spectrum.
				 */
				IMassSpectraWriter massSpectraWriter = new MSLWriter();
				massSpectraWriter.write(file, massSpectrum, append, monitor);
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

	@Override
	public IProcessingInfo convert(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) {

		file = SpecificationValidatorMSL.validateSpecification(file);
		IProcessingInfo processingInfo = validate(file, massSpectra);
		if(!processingInfo.hasErrorMessages()) {
			try {
				/*
				 * Convert the mass spectra.
				 */
				IMassSpectraWriter massSpectraWriter = new MSLWriter();
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

	private IProcessingInfo validate(File file, IScanMSD massSpectrum) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(super.validate(file));
		processingInfo.addMessages(super.validate(massSpectrum));
		return processingInfo;
	}

	private IProcessingInfo validate(File file, IMassSpectra massSpectra) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(super.validate(file));
		processingInfo.addMessages(super.validate(massSpectra));
		return processingInfo;
	}
}
