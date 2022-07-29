/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakIdentificationBatchJobReader {

	/**
	 * Reads the file and returns an IPeakIdentificationBatchJob instance.
	 * 
	 * @param file
	 * @param monitor
	 * @return {@link IPeakIdentificationBatchJob}
	 * @throws IOException
	 */
	IPeakIdentificationBatchJob read(File file, IProgressMonitor monitor) throws IOException;
}
