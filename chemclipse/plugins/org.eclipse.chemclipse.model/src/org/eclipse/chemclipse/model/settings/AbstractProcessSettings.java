/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.settings;

import org.apache.commons.io.FileSystem;
import org.eclipse.chemclipse.model.core.IChromatogram;

public abstract class AbstractProcessSettings implements IProcessSettings {

	@Override
	public void setSystemSettings() {

	}

	/**
	 * Replaces the file name placeholder and validates the file name to be legal.
	 * 
	 * @param chromatogram
	 * @param fileNamePattern
	 * @param extension
	 * @return String
	 */
	protected String getFileName(IChromatogram<?> chromatogram, String fileNamePattern, String extension) {

		String fileName = replaceFileName(chromatogram, fileNamePattern);
		fileName = replaceFileExtension(fileName, extension);
		/*
		 * Remove OS specific file system control characters.
		 */
		return FileSystem.getCurrent().toLegalFileName(fileName, (char)'-');
	}

	private String replaceFileName(IChromatogram<?> chromatogram, String fileNamePattern) {

		String fileName = fileNamePattern;
		/*
		 * Replace the variable place holder by the according chromatogram header data.
		 */
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_NAME, chromatogram.getName(), "Name");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_DATANAME, chromatogram.getDataName(), "DataName");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_SAMPLEGROUP, chromatogram.getSampleGroup(), "SampleGroup");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_SHORTINFO, chromatogram.getShortInfo(), "ShortInfo");
		//
		return fileName;
	}

	private String replaceVariable(String fileNamePattern, String variable, String replacement, String defaultReplacement) {

		String result = fileNamePattern;
		//
		if(fileNamePattern.contains(variable)) {
			if(replacement == null || replacement.isEmpty()) {
				replacement = defaultReplacement;
			}
			result = fileNamePattern.replace(variable, replacement);
		}
		//
		return result;
	}

	private String replaceFileExtension(String fileName, String extension) {

		return fileName.replace(VARIABLE_EXTENSION, extension);
	}
}