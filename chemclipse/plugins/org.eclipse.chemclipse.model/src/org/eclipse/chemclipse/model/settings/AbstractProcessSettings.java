/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.settings;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileSystem;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.support.literature.LiteratureReference;

public abstract class AbstractProcessSettings implements IProcessSettings {

	private final List<LiteratureReference> literatureReference = new ArrayList<>();

	/**
	 * Replaces the pattern and extension.
	 * For example {chromatogram_name}{extension} is validated to TestChromatogram.csv
	 * 
	 * @param chromatogram
	 * @param fileNamePattern
	 * @param extension
	 * @return String
	 */
	public static String validateFileName(IChromatogram chromatogram, String fileNamePattern, String extension) {

		String fileName = replaceFileName(chromatogram, fileNamePattern);
		fileName = replaceFileExtension(fileName, extension);
		return toLegalFileName(fileName);
	}

	/**
	 * Removes the OS specific file system control characters.
	 * 
	 * @param fileName
	 * @return String
	 */
	protected static String toLegalFileName(String fileName) {

		return FileSystem.getCurrent().toLegalFileName(fileName, '-');
	}

	protected String getFileName(IChromatogram chromatogram, String fileNamePattern, String extension) {

		return validateFileName(chromatogram, fileNamePattern, extension);
	}

	@Override
	public List<LiteratureReference> getLiteratureReferences() {

		return literatureReference;
	}

	/*
	 * Jackson serializes File.getAbsolutePath()
	 * which mistakes the placeholder for a relative path and prepends the working directory
	 * so we remove it again.
	 */
	public static String getCleanedFileValue(String value) {

		int startIndex = value.indexOf(IProcessSettings.VARIABLE_CURRENT_DIRECTORY);
		if(startIndex != -1) {
			return value.substring(startIndex);
		}

		return value;
	}

	private static String replaceFileName(IChromatogram chromatogram, String fileNamePattern) {

		String fileName = fileNamePattern;
		/*
		 * Replace the variable place holder by the according chromatogram header data.
		 */
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_NAME, chromatogram.getName(), "Name");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_DATANAME, chromatogram.getDataName(), "DataName");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_SAMPLEGROUP, chromatogram.getSampleGroup(), "SampleGroup");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_SHORTINFO, chromatogram.getShortInfo(), "ShortInfo");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_SAMPLENAME, chromatogram.getSampleName(), "SampleName");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_OPERATOR, chromatogram.getOperator(), "Operator");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_INSTRUMENT, chromatogram.getInstrument(), "Instrument");
		fileName = replaceVariable(fileName, VARIABLE_CHROMATOGRAM_TAGS, chromatogram.getTags(), "Tags");

		return fileName;
	}

	protected static String replaceVariable(String fileNamePattern, String variable, String replacement, String defaultReplacement) {

		String result = fileNamePattern;
		if(fileNamePattern.contains(variable)) {
			if(replacement == null || replacement.isEmpty()) {
				replacement = defaultReplacement;
			}
			result = fileNamePattern.replace(variable, replacement);
		}

		return result;
	}

	protected static String replaceFileExtension(String fileName, String extension) {

		return fileName.replace(VARIABLE_EXTENSION, extension);
	}
}