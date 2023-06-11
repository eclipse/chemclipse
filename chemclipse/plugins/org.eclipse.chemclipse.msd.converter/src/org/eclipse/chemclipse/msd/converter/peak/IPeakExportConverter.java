/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - simplify API
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.peak;

import java.io.File;

import org.eclipse.chemclipse.converter.core.IExportConverter;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakExportConverter extends IExportConverter {

	/**
	 * Writes a the peak list to a file.
	 * 
	 * @param file
	 * @param peaks
	 * @param append
	 * @param monitor
	 * @return IProcessingInfo
	 */
	IProcessingInfo<File> convert(File file, IPeaks<? extends IPeakMSD> peaks, boolean append, IProgressMonitor monitor);
}
