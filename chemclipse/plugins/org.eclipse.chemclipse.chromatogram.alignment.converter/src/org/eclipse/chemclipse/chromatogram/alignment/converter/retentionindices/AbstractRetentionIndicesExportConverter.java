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

public abstract class AbstractRetentionIndicesExportConverter implements IRetentionIndicesExportConverter {

	/**
	 * This method validates whether the retention indices file is writable.<br/>
	 * If the file is not writable a FileIsNotWritableException will be thrown.
	 * 
	 * @param chromatogram
	 * @throws FileNotFoundException
	 * @throws FileIsNotWriteableException
	 * @throws IOException
	 */
	public void validate(final File file) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		/*
		 * When no file is given.
		 */
		if(file == null) {
			throw new FileNotFoundException();
		}
		createDirectoriesAndFiles(file);
		/*
		 * Tests if the file can be written.
		 */
		if(!file.canWrite()) {
			throw new FileIsNotWriteableException("Can't write the retention indices file: " + file.getAbsoluteFile());
		}
	}

	/**
	 * Creates missing directories and files.<br/>
	 * If a failure occurs an exception will be thrown.
	 * 
	 * @param file
	 * @throws FileIsNotWriteableException
	 * @throws IOException
	 */
	private void createDirectoriesAndFiles(final File file) throws FileIsNotWriteableException, IOException {

		/*
		 * Take a look if the given file is a directory or a file.<br/> Check
		 * that all directories exists. If not create them and the in case of a
		 * file also the file.
		 */
		if(file.isDirectory()) {
			if(!file.exists() && !file.mkdirs()) {
				throw new FileIsNotWriteableException("The given retention indices directory " + file + " can not be created.");
			}
		} else {
			File newFile = new File(file.getParentFile().getAbsolutePath());
			if(!newFile.exists() && !newFile.mkdirs()) {
				throw new FileIsNotWriteableException("The given retention indices directory " + file + " can not be created.");
			}
			if(!file.exists() && !file.createNewFile()) {
				throw new FileIsNotWriteableException("The given retention indices file " + file + " can not be created.");
			}
		}
	}
}
