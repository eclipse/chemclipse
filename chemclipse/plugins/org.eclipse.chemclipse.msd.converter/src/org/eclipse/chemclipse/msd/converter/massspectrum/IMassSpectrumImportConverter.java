/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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
package org.eclipse.chemclipse.msd.converter.massspectrum;

import java.io.File;

import org.eclipse.chemclipse.converter.core.IImportConverter;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMassSpectrumImportConverter<T> extends IImportConverter {

	/**
	 * Reads the mass spectra from the given file.
	 *
	 * @param file
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<T> convert(File file, IProgressMonitor monitor);
}
