/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.jcampdx.internal.converter;

import java.io.File;

public class SpecificationValidator {

	/**
	 * Use only static methods.
	 */
	private SpecificationValidator() {
	}

	/**
	 * Validates the given chromatogram file.<br/>
	 * If the file is denoted only by a directory path, /CHROMATOGRAM.JDX will
	 * be added. E.g.: /home/user/chrom will be validated to
	 * /home/user/chrom/CHROMATOGRAM.JDX
	 * 
	 * @param file
	 */
	public static File validateSpecification(File file, String extension) {

		if(file == null) {
			return null;
		}
		/*
		 * Validate
		 */
		File validFile;
		extension = extension.toUpperCase();
		String path = file.getAbsolutePath().toUpperCase();
		if(file.isDirectory()) {
			validFile = new File(file.getAbsolutePath() + File.separator + "CHROMATOGRAM." + extension);
		} else {
			if(path.endsWith(".")) {
				validFile = new File(file.getAbsolutePath() + extension);
			} else if(!path.endsWith("." + extension)) {
				validFile = new File(file.getAbsolutePath() + "." + extension);
			} else {
				validFile = file;
			}
		}
		return validFile;
	}
}
