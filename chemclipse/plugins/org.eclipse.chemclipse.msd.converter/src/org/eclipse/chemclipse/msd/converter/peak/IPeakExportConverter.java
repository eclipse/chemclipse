/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.converter.core.IExportConverter;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public interface IPeakExportConverter extends IExportConverter {

	/**
	 * Writes the peak to a file.
	 * 
	 * @param file
	 * @param peak
	 * @param append
	 * @param monitor
	 * @return IProcessingInfo
	 */
	IProcessingInfo convert(File file, IPeakMSD peak, boolean append, IProgressMonitor monitor);

	/**
	 * Writes a the peak list to a file.
	 * 
	 * @param file
	 * @param peaks
	 * @param append
	 * @param monitor
	 * @return IProcessingInfo
	 */
	IProcessingInfo convert(File file, IPeaks peaks, boolean append, IProgressMonitor monitor);

	/**
	 * Checks the peak instance.
	 * 
	 * @param peak
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(IPeakMSD peak);

	/**
	 * Checks the peaks instance.
	 * 
	 * @param peaks
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(IPeaks peaks);
}
