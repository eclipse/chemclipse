/*******************************************************************************
 * Copyright (c) 2008, 2018, 2019 Lablicate GmbH.
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
package org.eclipse.chemclipse.converter.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public interface IExportConverter {

	/**
	 * This method validates whether the file is writable.<br/>
	 *
	 * @param file
	 * @return {@link IProcessingInfo}
	 */
	<R> IProcessingInfo<R> validate(File file);

	default <R> IProcessingInfo<R> validate(String nameHint, InputStream stream) throws IOException {

		ProcessingInfo<R> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage("Can't export " + nameHint, "This ExportConverter does currentyl not supports the new stream API");
		return processingInfo;
	}
}
