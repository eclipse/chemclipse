/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.converter.peak.AbstractPeakExportConverter;
import org.eclipse.chemclipse.msd.converter.processing.peak.IPeakExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.peak.PeakExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.internal.converter.SpecificationValidatorMSL;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.AmdisMSLWriter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

/**
 * NAME FIELD:
 * If the mass spectrum is a type of IRegularLibraryMassSpectrum, than getLibraryInformation().getName() will be used,
 * otherwise massSpectrum.getIdentifier().
 * 
 * @author chemclipse
 * 
 */
public class MSLPeakExportConverter extends AbstractPeakExportConverter {

	private static final Logger logger = Logger.getLogger(MSLPeakExportConverter.class);
	private static final String DESCRIPTION = "AMDIS MSL MassSpectrum Export";

	@Override
	public IPeakExportConverterProcessingInfo convert(File file, IPeakMSD peak, boolean append, IProgressMonitor monitor) {

		IPeakExportConverterProcessingInfo processingInfo = new PeakExportConverterProcessingInfo();
		/*
		 * Checks that file and mass spectrum are not null.
		 */
		file = SpecificationValidatorMSL.validateSpecification(file);
		IProcessingInfo processingInfoValidate = validate(file, peak);
		if(processingInfoValidate.hasErrorMessages()) {
			processingInfo.addMessages(processingInfoValidate);
		} else {
			try {
				/*
				 * Convert the mass spectrum.
				 */
				IMassSpectraWriter massSpectraWriter = new AmdisMSLWriter();
				massSpectraWriter.write(file, peak.getExtractedMassSpectrum(), append);
				processingInfo.setFile(file);
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
	public IPeakExportConverterProcessingInfo convert(File file, IPeaks peaks, boolean append, IProgressMonitor monitor) {

		IPeakExportConverterProcessingInfo processingInfo = new PeakExportConverterProcessingInfo();
		/*
		 * Checks that file and mass spectra are not null.
		 */
		file = SpecificationValidatorMSL.validateSpecification(file);
		IProcessingInfo processingInfoValidate = validate(file, peaks);
		if(processingInfoValidate.hasErrorMessages()) {
			processingInfo.addMessages(processingInfoValidate);
		} else {
			try {
				/*
				 * Convert the mass spectra.
				 */
				IMassSpectraWriter massSpectraWriter = new AmdisMSLWriter();
				IMassSpectra massSpectra = extractMassSpectra(peaks);
				massSpectraWriter.write(file, massSpectra, append);
				processingInfo.setFile(file);
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

	private IMassSpectra extractMassSpectra(IPeaks peaks) {

		/*
		 * Get the mass spectra.
		 */
		IMassSpectra massSpectra = new MassSpectra();
		for(IPeak peak : peaks.getPeaks()) {
			if(peak instanceof IPeakMSD) {
				IPeakMSD peakMSD = (IPeakMSD)peak;
				massSpectra.addMassSpectrum(peakMSD.getExtractedMassSpectrum());
			}
		}
		return massSpectra;
	}

	private IProcessingInfo validate(File file, IPeakMSD peak) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(super.validate(file));
		processingInfo.addMessages(super.validate(peak));
		return processingInfo;
	}

	private IProcessingInfo validate(File file, IPeaks peaks) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(super.validate(file));
		processingInfo.addMessages(super.validate(peaks));
		return processingInfo;
	}
}
