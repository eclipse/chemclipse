/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * chemclipse - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.files;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSpecificationValidator {

	private static final Logger logger = LoggerFactory.getLogger(FileSpecificationValidator.class);

	public File validate(File file, String directoryExtension) {

		/*
		 * Check
		 */
		if(file == null) {
			return null;
		}
		//
		if(file.isDirectory()) {
			validateAndPatchDirectory(file, directoryExtension);
		} else {
		}
		//
		return file;
	}

	public File validateAndPatchDirectory(File file, String directoryExtension) {

		String fileName = file.getName().toLowerCase();
		directoryExtension = directoryExtension.toLowerCase();
		//
		if(!fileName.endsWith(directoryExtension)) {
			file = new File(file.getAbsolutePath() + directoryExtension);
			if(!file.mkdir()) {
				logger.info("The directory already exists: " + file.getAbsolutePath());
			}
		}
		//
		return file;
	}
}
