/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.chemclipse.converter;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramExportConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramExportConverter;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDWriter;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.io.ChromatogramWriterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.SpecificationValidator;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramExportConverter extends AbstractChromatogramExportConverter implements IChromatogramExportConverter {

	private static final Logger logger = Logger.getLogger(ChromatogramExportConverter.class);

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public IProcessingInfo convert(File file, IChromatogram<? extends IPeak> chromatogram, IProgressMonitor monitor) {

		file = SpecificationValidator.validateSpecification(file);
		IProcessingInfo processingInfo = super.validate(file);
		/*
		 * Don't process if errors have occurred.
		 */
		if(!processingInfo.hasErrorMessages() && chromatogram instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
			IChromatogramCSDWriter writer = new ChromatogramWriterCSD();
			try {
				writer.writeChromatogram(file, chromatogramCSD, monitor);
			} catch(Exception e) {
				logger.warn(e);
				processingInfo.addErrorMessage(IConstants.DESCRIPTION_EXPORT, "Something has definitely gone wrong with the file: " + file.getAbsolutePath());
			}
			processingInfo.setProcessingResult(file);
		}
		return processingInfo;
	}
}
