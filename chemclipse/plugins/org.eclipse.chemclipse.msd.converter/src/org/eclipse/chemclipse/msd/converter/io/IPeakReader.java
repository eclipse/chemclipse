/*******************************************************************************
 * Copyright (c) 2012, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakReader {

	String TEMP_INFO_START_SCAN = "START_SCAN";
	String TEMP_INFO_STOP_SCAN = "STOP_SCAN";
	String TEMP_INFO_MAX_SCAN = "MAX_SCAN";

	/**
	 * Imports peaks from a file.
	 * 
	 * @param file
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 * @throws FileNotFoundException
	 * @throws FileIsNotReadableException
	 * @throws FileIsEmptyException
	 * @throws IOException
	 */
	IProcessingInfo<IPeaks<?>> read(File file, IProgressMonitor monitor) throws IOException;
}
