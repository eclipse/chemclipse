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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io;

import java.io.File;

public class SpecificationValidator {

	/**
	 * Use only static methods.
	 */
	private SpecificationValidator() {
	}

	/**
	 * Validates the given chromatogram file.<br/>
	 * If the file is denoted only by a directory path, /CHROMATOGRAM.mzXML will
	 * be added. E.g.: /home/user/chrom will be validated to
	 * /home/user/chrom/CHROMATOGRAM.mzXML
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
		String path = file.getAbsolutePath();
		if(file.isDirectory()) {
			validFile = new File(file.getAbsolutePath() + File.separator + "CHROMATOGRAM.mzXML");
		} else {
			if(path.endsWith(".")) {
				validFile = new File(file.getAbsolutePath() + "mzXML");
			} else if(!path.endsWith(".mzXML") && !path.endsWith(".mzxml") && !path.endsWith(".MZXML")) {
				validFile = new File(file.getAbsolutePath() + ".mzXML");
			} else {
				validFile = file;
			}
		}
		return validFile;
	}
}
