/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model;

import java.io.File;

public class ChromatogramInputEntry implements IChromatogramInputEntry {

	private String name = "";
	private String inputFile = "";

	/**
	 * Set the chromatogram input file.
	 * 
	 * @param inputFile
	 */
	public ChromatogramInputEntry(String inputFile) {
		if(inputFile != null) {
			this.inputFile = inputFile;
			File file = new File(inputFile);
			if(file.exists()) {
				this.name = file.getName();
			} else {
				this.name = "File doesn't exist.";
			}
		} else {
			this.name = "";
			this.inputFile = "";
		}
	}

	public ChromatogramInputEntry(String inputFile, String name) {
		this.inputFile = inputFile;
		this.name = name;
	}

	@Override
	public String getInputFile() {

		return inputFile;
	}

	@Override
	public String getName() {

		return name;
	}
}
