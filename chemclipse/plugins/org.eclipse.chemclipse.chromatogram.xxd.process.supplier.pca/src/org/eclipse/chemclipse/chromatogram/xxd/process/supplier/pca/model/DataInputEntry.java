/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.io.File;

public class DataInputEntry implements IDataInputEntry {

	private String inputFile = "";

	/**
	 * Set the peak input file.
	 * 
	 * @param inputFile
	 */
	public DataInputEntry(String inputFile) {
		if(inputFile != null) {
			this.inputFile = inputFile;
		}
	}

	@Override
	public String getInputFile() {

		return inputFile;
	}

	@Override
	public String getName() {

		File file = new File(inputFile);
		return file.getName();
	}
}
