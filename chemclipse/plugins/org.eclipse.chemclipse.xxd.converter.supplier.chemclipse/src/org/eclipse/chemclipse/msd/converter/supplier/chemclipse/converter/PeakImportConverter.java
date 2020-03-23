/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.io.IPeakReader;
import org.eclipse.chemclipse.msd.converter.peak.AbstractPeakImportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io.PeakReaderMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.SpecificationValidator;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakImportConverter extends AbstractPeakImportConverter {

	private static final Logger logger = Logger.getLogger(ChromatogramImportConverter.class);

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public IProcessingInfo convert(File file, IProgressMonitor monitor) {

		IProcessingMessage processingMessage;
		IProcessingInfo processingInfo = new ProcessingInfo();
		try {
			super.validate(file);
			file = SpecificationValidator.validateSpecification(file);
			IPeakReader peakReader = new PeakReaderMSD();
			processingInfo = peakReader.read(file, monitor);
		} catch(FileNotFoundException e) {
			logger.warn(e);
			processingMessage = new ProcessingMessage(MessageType.ERROR, IConstants.PROCESS_INFO_DESCRIPTION, "The given file was not found: " + file.getAbsolutePath());
			processingInfo.addMessage(processingMessage);
		} catch(FileIsNotReadableException e) {
			logger.warn(e);
			processingMessage = new ProcessingMessage(MessageType.ERROR, IConstants.PROCESS_INFO_DESCRIPTION, "The given file is not readable: " + file.getAbsolutePath());
			processingInfo.addMessage(processingMessage);
		} catch(FileIsEmptyException e) {
			logger.warn(e);
			processingMessage = new ProcessingMessage(MessageType.ERROR, IConstants.PROCESS_INFO_DESCRIPTION, "The given file is empty: " + file.getAbsolutePath());
			processingInfo.addMessage(processingMessage);
		} catch(IOException e) {
			logger.warn(e);
			processingMessage = new ProcessingMessage(MessageType.ERROR, IConstants.PROCESS_INFO_DESCRIPTION, "There has gone something wrong reading the file: " + file.getAbsolutePath());
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}
}
