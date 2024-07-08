/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
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
import java.io.IOException;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramExportConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramExportConverter;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.l10n.ConverterMessages;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDWriter;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.io.ChromatogramWriter;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.jcampdx.internal.converter.SpecificationValidator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

public class ChromatogramExportConverter extends AbstractChromatogramExportConverter implements IChromatogramExportConverter {

	private static final Logger logger = Logger.getLogger(ChromatogramExportConverter.class);
	private static final String DESCRIPTION = "JCAMP-DX Export Converter";

	@Override
	public IProcessingInfo<File> convert(File file, IChromatogram<? extends IPeak> chromatogram, IProgressMonitor monitor) {

		/*
		 * Validate the file.
		 */
		file = SpecificationValidator.validateSpecification(file, "JDX");
		IProcessingInfo<File> processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages() && chromatogram instanceof IChromatogramCSD chromatogramCSD) {
			IChromatogramCSDWriter writer = new ChromatogramWriter();
			monitor.subTask(ConverterMessages.exportChromatogram);
			try {
				writer.writeChromatogram(file, chromatogramCSD, monitor);
				processingInfo.setProcessingResult(file);
			} catch(IOException e) {
				logger.error(e);
				processingInfo.addErrorMessage(DESCRIPTION, NLS.bind(ConverterMessages.failedToWriteFile, file.getAbsolutePath()));
			} catch(FileIsNotWriteableException e) {
				logger.error(e);
				processingInfo.addErrorMessage(DESCRIPTION, NLS.bind(ConverterMessages.fileNotWritable, file.getAbsolutePath()));
			}
		}
		return processingInfo;
	}
}
