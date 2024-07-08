/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.ocx.converter;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramImportConverter;
import org.eclipse.chemclipse.converter.l10n.ConverterMessages;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.supplier.ocx.io.ChromatogramReaderCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.SpecificationValidator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

public class ChromatogramImportConverter extends AbstractChromatogramImportConverter<IChromatogramCSD> {

	private static final Logger logger = Logger.getLogger(ChromatogramImportConverter.class);

	@Override
	public IProcessingInfo<IChromatogramCSD> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramCSD> processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages()) {
			file = SpecificationValidator.validateSpecification(file);
			IChromatogramCSDReader reader = new ChromatogramReaderCSD();
			try {
				IChromatogramCSD chromatogram = reader.read(file, monitor);
				processingInfo.setProcessingResult(chromatogram);
			} catch(IOException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(ConverterMessages.importChromatogram, NLS.bind(ConverterMessages.failedToReadFile, file.getAbsolutePath()));
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramOverview> processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages()) {
			file = SpecificationValidator.validateSpecification(file);
			IChromatogramCSDReader reader = new ChromatogramReaderCSD();
			try {
				IChromatogramOverview chromatogramOverview = reader.readOverview(file, monitor);
				processingInfo.setProcessingResult(chromatogramOverview);
			} catch(IOException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(ConverterMessages.importChromatogram, NLS.bind(ConverterMessages.failedToReadFile, file.getAbsolutePath()));
			}
		}
		return processingInfo;
	}
}
