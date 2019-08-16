/*******************************************************************************
 * Copyright (c) 2010, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add equals method
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

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputFile == null) ? 0 : inputFile.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ChromatogramInputEntry other = (ChromatogramInputEntry)obj;
		if(inputFile == null) {
			if(other.inputFile != null)
				return false;
		} else if(!inputFile.equals(other.inputFile))
			return false;
		if(name == null) {
			if(other.name != null)
				return false;
		} else if(!name.equals(other.name))
			return false;
		return true;
	}
}
