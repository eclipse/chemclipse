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
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.model;

import java.io.File;

import org.eclipse.chemclipse.model.columns.ISeparationColumn;

public class IdentifierFile {

	private File file;
	private ISeparationColumn separationColumn;

	public IdentifierFile(File file) {
		this.file = file;
		IdentifierFileReader calibrationFileReader = new IdentifierFileReader();
		this.separationColumn = calibrationFileReader.parse(file);
	}

	public File getFile() {

		return file;
	}

	public ISeparationColumn getSeparationColumn() {

		return separationColumn;
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
		IdentifierFile other = (IdentifierFile)obj;
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

		return "CalibrationFile [file=" + file + ", separationColumns=" + separationColumn + "]";
	}
}
