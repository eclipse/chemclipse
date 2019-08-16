/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.methods;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.methods.AbstractMethodExportConverter;
import org.eclipse.chemclipse.converter.methods.IMethodExportConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.IMethodWriter;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodWriter_1000;
import org.eclipse.core.runtime.IProgressMonitor;

public class MethodExportConverter extends AbstractMethodExportConverter<File> implements IMethodExportConverter<File> {

	private static final Logger logger = Logger.getLogger(MethodExportConverter.class);

	@Override
	public IProcessingInfo<File> convert(File file, IProcessMethod processMethod, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		try {
			IMethodWriter writer = new MethodWriter_1000();
			writer.convert(file, processMethod, monitor);
			processingInfo.setProcessingResult(file);
		} catch(IOException e) {
			processingInfo.addErrorMessage("Method Converter (*.ocm)", "Something has gone wrong to write the file: " + file);
			logger.warn(e);
		}
		//
		return processingInfo;
	}
}
