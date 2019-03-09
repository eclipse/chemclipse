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

public interface IImportConverter {

	/**
	 * This method validates the file which contains the information to be
	 * imported.
	 *
	 * @param chromatogram
	 * @return {@link IProcessingInfo}
	 */
	<T> IProcessingInfo<T> validate(File file);

	default <T> IProcessingInfo<T> validate(String nameHint, InputStream stream) throws IOException {

		ProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage("Can't import " + nameHint, "This ImportConverter does currentyl not supports the new stream API");
		return processingInfo;
	}
}
