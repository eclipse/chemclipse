/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.converter;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramExportConverter;
import org.eclipse.chemclipse.converter.processing.chromatogram.ChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.chromatogram.AbstractChromatogramWSDExportConverter;
import org.eclipse.chemclipse.wsd.converter.io.IChromatogramWSDWriter;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.io.ChromatogramWriterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.SpecificationValidator;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramExportConverter extends AbstractChromatogramWSDExportConverter implements IChromatogramExportConverter {

	private static final Logger logger = Logger.getLogger(ChromatogramExportConverter.class);

	@Override
	public IChromatogramExportConverterProcessingInfo convert(File file, IChromatogramWSD chromatogram, IProgressMonitor monitor) {

		IChromatogramExportConverterProcessingInfo processingInfo = new ChromatogramExportConverterProcessingInfo();
		/*
		 * Validate the file.
		 */
		file = SpecificationValidator.validateSpecification(file);
		IProcessingInfo processingInfoValidate = super.validate(file);
		/*
		 * Don't process if errors have occurred.
		 */
		if(processingInfoValidate.hasErrorMessages()) {
			processingInfo.addMessages(processingInfoValidate);
		} else {
			monitor.subTask(IConstants.EXPORT_CHROMATOGRAM);
			IChromatogramWSDWriter writer = new ChromatogramWriterWSD();
			try {
				writer.writeChromatogram(file, chromatogram, monitor);
			} catch(Exception e) {
				logger.warn(e);
				processingInfo.addErrorMessage(IConstants.DESCRIPTION_EXPORT, "Something has definitely gone wrong with the file: " + file.getAbsolutePath());
			}
			processingInfo.setFile(file);
		}
		return processingInfo;
	}
}
