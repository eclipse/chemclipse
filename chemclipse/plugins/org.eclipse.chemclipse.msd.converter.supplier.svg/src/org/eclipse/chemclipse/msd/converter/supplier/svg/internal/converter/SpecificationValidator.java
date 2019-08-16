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
package org.eclipse.chemclipse.msd.converter.supplier.svg.internal.converter;

import java.io.File;

public class SpecificationValidator {

	/**
	 * Use only static methods.
	 */
	private SpecificationValidator() {
	}

	public static File validateSpecification(File file) {

		File validFile;
		String path = file.getAbsolutePath().toLowerCase();
		if(file.isDirectory()) {
			validFile = new File(file.getAbsolutePath() + File.separator + "chromatogram.svg");
		} else {
			if(path.endsWith(".")) {
				validFile = new File(file.getAbsolutePath() + "svg");
			} else if(!path.endsWith(".svg")) {
				validFile = new File(file.getAbsolutePath() + ".svg");
			} else {
				validFile = file;
			}
		}
		return validFile;
	}
}
