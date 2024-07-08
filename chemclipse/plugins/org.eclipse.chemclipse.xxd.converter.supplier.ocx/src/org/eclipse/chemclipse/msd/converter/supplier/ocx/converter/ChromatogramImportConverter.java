/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.ocx.converter;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramImportConverter;
import org.eclipse.chemclipse.converter.l10n.ConverterMessages;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.io.ChromatogramReaderMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.SpecificationValidator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;

public class ChromatogramImportConverter extends AbstractChromatogramImportConverter<IChromatogramMSD> {

	private static final Logger logger = Logger.getLogger(ChromatogramImportConverter.class);

	@Override
	public IProcessingInfo<IChromatogramMSD> convert(File file, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, "Convert file " + file.getName(), 100);
		try {
			subMonitor.subTask("Validate");
			IProcessingInfo<IChromatogramMSD> processingInfo = super.validate(file);
			if(!processingInfo.hasErrorMessages()) {
				/*
				 * Read the chromatogram.
				 */
				file = SpecificationValidator.validateSpecification(file);
				subMonitor.worked(1);
				IChromatogramMSDReader reader = new ChromatogramReaderMSD();
				monitor.subTask(ConverterMessages.importChromatogram);
				try {
					IChromatogramMSD chromatogram = reader.read(file, subMonitor.split(99));
					processingInfo.setProcessingResult(chromatogram);
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.warn(e);
					processingInfo.addErrorMessage(ConverterMessages.importChromatogram, NLS.bind(ConverterMessages.failedToWriteFile, file.getAbsolutePath()));
				} catch(OperationCanceledException e) {
					logger.warn(e);
					processingInfo.addErrorMessage(ConverterMessages.importChromatogram, NLS.bind(ConverterMessages.cancelledConversion, file.getAbsolutePath()));
				} catch(IOException e) {
					logger.warn(e);
					processingInfo.addErrorMessage(ConverterMessages.importChromatogram, NLS.bind(ConverterMessages.failedToWriteFile, file.getAbsolutePath()));
				}
			}
			return processingInfo;
		} finally {
			SubMonitor.done(subMonitor);
		}
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramOverview> processingInfo = super.validate(file);
		if(!processingInfo.hasErrorMessages()) {
			file = SpecificationValidator.validateSpecification(file);
			IChromatogramMSDReader reader = new ChromatogramReaderMSD();
			try {
				IChromatogramOverview chromatogramOverview = reader.readOverview(file, monitor);
				processingInfo.setProcessingResult(chromatogramOverview);
			} catch(IOException e) {
				logger.error(e);
				processingInfo.addErrorMessage(ConverterMessages.importChromatogram, NLS.bind(ConverterMessages.failedToWriteFile, file.getAbsolutePath()));
			}
		}
		return processingInfo;
	}
}
