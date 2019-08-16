/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileReader;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;

public class CalibrationFile {

	private File file;
	private ISeparationColumnIndices separationColumnIndices;

	public CalibrationFile(File file) {
		this.file = file;
		CalibrationFileReader calibrationFileReader = new CalibrationFileReader();
		this.separationColumnIndices = calibrationFileReader.parse(file);
	}

	public File getFile() {

		return file;
	}

	public ISeparationColumnIndices getSeparationColumnIndices() {

		return separationColumnIndices;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
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
		CalibrationFile other = (CalibrationFile)obj;
		if(file == null) {
			if(other.file != null)
				return false;
		} else if(!file.equals(other.file)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {

		return "CalibrationFile [file=" + file + ", separationColumnIndices=" + separationColumnIndices + "]";
	}
}
