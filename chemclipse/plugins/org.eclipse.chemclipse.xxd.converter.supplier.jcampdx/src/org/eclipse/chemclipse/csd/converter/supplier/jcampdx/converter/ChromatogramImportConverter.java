/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.jcampdx.converter;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramImportConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramImportConverter;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.io.ChromatogramReader;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.jcampdx.internal.converter.SpecificationValidator;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramImportConverter extends AbstractChromatogramImportConverter implements IChromatogramImportConverter {

	private static final Logger logger = Logger.getLogger(ChromatogramImportConverter.class);
	private static final String DESCRIPTION = "JCAMP-DX Import Converter";

	@Override
	public IProcessingInfo convert(File file, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages()) {
			file = SpecificationValidator.validateSpecification(file, "JDX");
			IChromatogramCSDReader reader = new ChromatogramReader();
			try {
				IChromatogramCSD chromatogram = reader.read(file, monitor);
				processingInfo.setProcessingResult(chromatogram);
			} catch(Exception e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "Something has definitely gone wrong with the file: " + file.getAbsolutePath());
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo convertOverview(File file, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages()) {
			file = SpecificationValidator.validateSpecification(file, "JDX");
			IChromatogramCSDReader reader = new ChromatogramReader();
			try {
				IChromatogramOverview chromatogramOverview = reader.readOverview(file, monitor);
				processingInfo.setProcessingResult(chromatogramOverview);
			} catch(Exception e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "Something has definitely gone wrong with the file: " + file.getAbsolutePath());
			}
		}
		return processingInfo;
	}
}
