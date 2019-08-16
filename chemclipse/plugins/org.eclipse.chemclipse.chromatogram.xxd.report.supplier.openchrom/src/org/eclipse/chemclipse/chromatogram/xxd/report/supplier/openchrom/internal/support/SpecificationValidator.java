/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.internal.support;

import java.io.File;

public class SpecificationValidator {

	/**
	 * Use only static methods.
	 */
	private SpecificationValidator() {
	}

	/**
	 * Validates the given file.<br/>
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
			validFile = new File(file.getAbsolutePath() + File.separator + "ChemClipseReport.txt");
		} else {
			if(path.endsWith(".")) {
				validFile = new File(file.getAbsolutePath() + "txt");
			} else if(!path.endsWith(".txt")) {
				validFile = new File(file.getAbsolutePath() + ".txt");
			} else {
				validFile = file;
			}
		}
		return validFile;
	}
}
