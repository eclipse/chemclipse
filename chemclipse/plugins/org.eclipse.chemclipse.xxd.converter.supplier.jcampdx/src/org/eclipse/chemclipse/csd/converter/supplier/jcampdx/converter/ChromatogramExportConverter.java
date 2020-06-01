/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramExportConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramExportConverter;
import org.eclipse.chemclipse.converter.support.IConstants;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDWriter;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.io.ChromatogramWriter;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.jcampdx.internal.converter.SpecificationValidator;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramExportConverter extends AbstractChromatogramExportConverter implements IChromatogramExportConverter {

	private static final Logger logger = Logger.getLogger(ChromatogramExportConverter.class);
	private static final String DESCRIPTION = "JCAMP-DX Export Converter";

	@Override
	public IProcessingInfo<?> convert(File file, IChromatogram<? extends IPeak> chromatogram, IProgressMonitor monitor) {

		/*
		 * Validate the file.
		 */
		file = SpecificationValidator.validateSpecification(file, "JDX");
		IProcessingInfo<File> processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages() && chromatogram instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
			IChromatogramCSDWriter writer = new ChromatogramWriter();
			monitor.subTask(IConstants.EXPORT_CHROMATOGRAM);
			try {
				writer.writeChromatogram(file, chromatogramCSD, monitor);
				processingInfo.setProcessingResult(file);
			} catch(Exception e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "Something has definitely gone wrong with the file: " + file.getAbsolutePath());
			}
		}
		return processingInfo;
	}
}
