/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.methods;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.methods.AbstractMethodImportConverter;
import org.eclipse.chemclipse.converter.methods.IMethodImportConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.IMethodReader;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodReader_1000;
import org.eclipse.core.runtime.IProgressMonitor;

public class MethodImportConverter extends AbstractMethodImportConverter implements IMethodImportConverter {

	private static final Logger logger = Logger.getLogger(MethodImportConverter.class);

	@Override
	public IProcessingInfo convert(File file, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		try {
			IMethodReader reader = new MethodReader_1000();
			ProcessMethod processMethod = reader.convert(file, monitor);
			processingInfo.setProcessingResult(processMethod);
		} catch(IOException e) {
			processingInfo.addErrorMessage("Method Converter (*.ocm)", "Something has gone wrong to read the file: " + file);
			logger.warn(e);
		}
		//
		return processingInfo;
	}
}
