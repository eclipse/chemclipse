/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.xy.internal.support;

import java.io.File;

public class SpecificationValidator {

	/**
	 * Use only static methods.
	 */
	private SpecificationValidator() {
	}

	/**
	 * Validates the given chromatogram file.<br/>
	 * If the file is denoted only by a directory path, /CHROMATOGRAM.SMS will
	 * be added. E.g.: /home/user/chrom will be validated to
	 * /home/user/chrom/CHROMATOGRAM.xy
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
		String path = file.getAbsolutePath().toLowerCase();
		if(file.isDirectory()) {
			validFile = new File(file.getAbsolutePath() + File.separator + "CHROMATOGRAM.xy");
		} else {
			if(path.endsWith(".")) {
				validFile = new File(file.getAbsolutePath() + "xy");
			} else if(!path.endsWith(".xy")) {
				validFile = new File(file.getAbsolutePath() + ".xy");
			} else {
				validFile = file;
			}
		}
		return validFile;
	}
}
