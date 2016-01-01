/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public interface IBatchProcessJobReader {

	/**
	 * Reads the batch process job file and returns a job instance.
	 * 
	 * @param file
	 * @param monitor
	 * @return
	 * @throws FileNotFoundException
	 * @throws FileIsNotReadableException
	 * @throws FileIsEmptyException
	 * @throws IOException
	 */
	IBatchProcessJob read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException;
}
