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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.alignment.converter.retentionindices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.alignment.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.chromatogram.alignment.model.core.IRetentionIndices;

public interface IRetentionIndicesExportConverter {

	/**
	 * This method returns the file of the written retention indices.
	 * 
	 * @param file
	 * @param retentionIndices
	 * @return File
	 * @throws FileNotFoundException
	 * @throws FileIsNotWriteableException
	 * @throws IOException
	 */
	File convert(File file, IRetentionIndices retentionIndices) throws FileNotFoundException, FileIsNotWriteableException, IOException;

	/**
	 * This class checks the file attributes and throws an exception if
	 * something is wrong.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws FileIsNotWriteableException
	 * @throws IOException
	 */
	void validate(File file) throws FileNotFoundException, FileIsNotWriteableException, IOException;
}
