/*******************************************************************************
 * Copyright (c) 2011, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.peak;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.core.IImportConverter;
import org.eclipse.chemclipse.msd.converter.processing.peak.IPeakImportConverterProcessingInfo;

public interface IPeakImportConverter extends IImportConverter {

	/**
	 * Converts the peaks stored in the file to an instance of IPeaks.
	 * 
	 * @param file
	 * @param monitor
	 * @return IPeakImportConverterProcessingInfo
	 */
	IPeakImportConverterProcessingInfo convert(File file, IProgressMonitor monitor);
}
