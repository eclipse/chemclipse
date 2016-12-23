/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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
	public static File validateSpecification(File file) {

		if(file == null) {
			return null;
		}
		/*
		 * Validate
		 */
		File validFile;
		String path = file.getAbsolutePath().toUpperCase();
		if(file.isDirectory()) {
			validFile = new File(file.getAbsolutePath() + File.separator + "CHROMATOGRAM.JDX");
		} else {
			if(path.endsWith(".")) {
				validFile = new File(file.getAbsolutePath() + "JDX");
			} else if(!path.endsWith(".JDX")) {
				validFile = new File(file.getAbsolutePath() + ".JDX");
			} else {
				validFile = file;
			}
		}
		return validFile;
	}
}
