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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;

public interface IPeakIdentificationBatchJobWriter {

	/**
	 * Writes the peak identification batch job to the given file.
	 * 
	 * @param file
	 * @param peakIdentificationBatchJob
	 * @param monitor
	 * @throws FileNotFoundException
	 * @throws FileIsNotWriteableException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	void writeBatchProcessJob(File file, IPeakIdentificationBatchJob peakIdentificationBatchJob, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException, XMLStreamException;
}
