/*******************************************************************************
 * Copyright (c) 2012, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.chromatogram;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramImportConverter;
import org.eclipse.chemclipse.csd.converter.processing.chromatogram.IChromatogramCSDImportConverterProcessingInfo;

public interface IChromatogramCSDImportConverter extends IChromatogramImportConverter {

	/**
	 * All implementing classes must return an IChromatogram instance.<br/>
	 * If no suitable converter is available, null will be returned.<br/>
	 * <br/>
	 * AbstractChromatogramImportConverter implements
	 * IChromatogramImportConverter. When extending from
	 * AbstractChromatogramImportConverter => super.validate(chromatogram) can
	 * be used.
	 * 
	 * @param chromatogram
	 * @param monitor
	 * @return {@link IChromatogramCSDImportConverterProcessingInfo}
	 */
	IChromatogramCSDImportConverterProcessingInfo convert(File file, IProgressMonitor monitor);
}
