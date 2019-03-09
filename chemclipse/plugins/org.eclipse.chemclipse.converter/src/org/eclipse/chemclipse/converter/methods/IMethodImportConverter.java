/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.chemclipse.converter.core.IImportConverter;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMethodImportConverter<R> extends IImportConverter {

	IProcessingInfo<R> convert(File file, IProgressMonitor monitor);

	/**
	 * Reads the given method from the input stream
	 *
	 * @param stream
	 * @param nameHint
	 * @param monitor
	 * @return a {@link IProcessingInfo} containing the result of the conversion process
	 * @throws IOException
	 *             in case of an IOError while reading streams
	 */
	default IProcessingInfo<R> convert(InputStream stream, String nameHint, IProgressMonitor monitor) throws IOException {

		ProcessingInfo<R> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage("Can't read " + nameHint, "This Converter does currentyl not supports the new stream API");
		return processingInfo;
	}
}
