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

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ChromatogramInputEntry implements IChromatogramInputEntry {

	private String inputFile = "";

	/**
	 * Set the chromatogram input file.
	 * 
	 * @param inputFile
	 */
	public ChromatogramInputEntry(String inputFile) {
		if(inputFile != null) {
			this.inputFile = inputFile;
		}
	}

	@Override
	public String getInputFile() {

		return inputFile;
	}

	// TODO JUnit Windows/Linux/Mac
	@Override
	public String getName() {

		File file = new File(inputFile);
		return file.getName();
	}
}
