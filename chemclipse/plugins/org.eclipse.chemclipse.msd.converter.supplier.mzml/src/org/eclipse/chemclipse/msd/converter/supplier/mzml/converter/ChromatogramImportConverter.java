/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.converter;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramImportConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.io.ChromatogramReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter.SpecificationValidator;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramImportConverter extends AbstractChromatogramImportConverter<IChromatogramMSD> {

	private static final Logger logger = Logger.getLogger(ChromatogramImportConverter.class);
	private static final String DESCRIPTION = "mzML Import Converter";
	private static final String IMPORT_CHROMATOGRAM = "Import mzML Chromatogram";
	private static final String IMPORT_CHROMATOGRAM_OVERVIEW = "Import mzML Chromatogram Overview";
	private static final String ERROR = "Error reading file: ";

	@Override
	public IProcessingInfo<IChromatogramMSD> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramMSD> processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages()) {
			/*
			 * Read the chromatogram.
			 */
			file = SpecificationValidator.validateSpecification(file);
			IChromatogramMSDReader reader = new ChromatogramReader();
			monitor.subTask(IMPORT_CHROMATOGRAM);
			try {
				IChromatogramMSD chromatogram = reader.read(file, monitor);
				processingInfo.setProcessingResult(chromatogram);
			} catch(IOException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, ERROR + file.getAbsolutePath());
			} catch(InterruptedException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, ERROR + file.getAbsolutePath());
				Thread.currentThread().interrupt();
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramOverview> processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages()) {
			file = SpecificationValidator.validateSpecification(file);
			IChromatogramMSDReader reader = new ChromatogramReader();
			monitor.subTask(IMPORT_CHROMATOGRAM_OVERVIEW);
			try {
				IChromatogramOverview chromatogramOverview = reader.readOverview(file, monitor);
				processingInfo.setProcessingResult(chromatogramOverview);
			} catch(Exception e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, ERROR + file.getAbsolutePath());
			}
		}
		return processingInfo;
	}
}
