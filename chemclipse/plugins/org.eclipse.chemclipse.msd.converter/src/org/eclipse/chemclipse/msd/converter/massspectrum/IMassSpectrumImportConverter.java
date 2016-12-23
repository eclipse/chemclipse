/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.massspectrum;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.core.IImportConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;

/**
 * @author eselmeister
 */
public interface IMassSpectrumImportConverter extends IImportConverter {

	/**
	 * Reads the mass spectra from the given file.
	 * 
	 * @param file
	 * @param monitor
	 * @return {@link IMassSpectrumImportConverterProcessingInfo}
	 */
	IMassSpectrumImportConverterProcessingInfo convert(File file, IProgressMonitor monitor);
}
