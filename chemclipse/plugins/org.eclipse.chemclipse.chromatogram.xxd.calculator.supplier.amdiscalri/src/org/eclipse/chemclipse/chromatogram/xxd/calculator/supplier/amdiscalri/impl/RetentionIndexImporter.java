/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileReader;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexFileOption;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.RetentionIndexImporterSettings;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;

public class RetentionIndexImporter {

	public static final String PLACEHOLDER_CHROMATOGRAM_NAME = "{chromatogram}";

	public void apply(IChromatogram<? extends IPeak> chromatogram, RetentionIndexImporterSettings processSettings) {

		boolean processReferenceChromatograms = processSettings.isProcessReferenceChromatograms();
		/*
		 * Check Directory ...
		 * Check Name Option
		 */
		ISeparationColumnIndices separationColumnIndices = getSeparationColumnIndices(chromatogram.getFile(), chromatogram.getName(), processSettings);
		if(separationColumnIndices != null) {
			setSeparationColumnIndices(chromatogram, separationColumnIndices);
			if(processReferenceChromatograms) {
				for(IChromatogram<?> chromatogramReference : chromatogram.getReferencedChromatograms()) {
					setSeparationColumnIndices(chromatogramReference, separationColumnIndices);
				}
			}
		}
	}

	private void setSeparationColumnIndices(IChromatogram<? extends IPeak> chromatogram, ISeparationColumnIndices separationColumnIndices) {

		chromatogram.getSeparationColumnIndices().clear();
		chromatogram.getSeparationColumnIndices().putAll(separationColumnIndices);
	}

	private ISeparationColumnIndices getSeparationColumnIndices(File chromatogramFile, String chromatogramName, RetentionIndexImporterSettings processSettings) {

		ISeparationColumnIndices separationColumnIndices = null;
		//
		RetentionIndexFileOption retentionIndexFileOption = processSettings.getRetentionIndexFileOption();
		File file = getCalibrationFile(chromatogramFile, chromatogramName, processSettings);
		if(file != null) {
			switch(retentionIndexFileOption) {
				case CAL:
					CalibrationFileReader calibrationFileReader = new CalibrationFileReader();
					separationColumnIndices = calibrationFileReader.parse(file);
					break;
				default:
					break;
			}
		}
		//
		return separationColumnIndices;
	}

	private File getCalibrationFile(File chromatogramFile, String chromatogramName, RetentionIndexImporterSettings processSettings) {

		File file = null;
		//
		if(chromatogramFile != null) {
			/*
			 * Settings
			 */
			RetentionIndexFileOption retentionIndexFileOption = processSettings.getRetentionIndexFileOption();
			String fileNamePattern = processSettings.getFileNamePattern();
			boolean caseSensitive = processSettings.isCaseSensitive();
			boolean matchName = !fileNamePattern.isEmpty();
			boolean regularExpression = false;
			String extension = caseSensitive ? retentionIndexFileOption.extension() : retentionIndexFileOption.extension().toLowerCase();
			//
			if(matchName) {
				if(fileNamePattern.equals(PLACEHOLDER_CHROMATOGRAM_NAME)) {
					fileNamePattern = caseSensitive ? chromatogramName : chromatogramName.toLowerCase();
				} else {
					regularExpression = true;
				}
			}
			/*
			 * Parse the directory
			 */
			File directory;
			if(chromatogramFile.isFile()) {
				directory = chromatogramFile.getParentFile();
			} else {
				directory = chromatogramFile;
			}
			/*
			 * Locate calibration file.
			 */
			List<File> files = getCalibrationFiles(directory, caseSensitive, extension);
			exitloop:
			for(File filex : files) {
				if(matchName) {
					/*
					 * Strip extension
					 */
					String name = caseSensitive ? filex.getName() : filex.getName().toLowerCase();
					name = name.substring(0, name.length() - extension.length());
					if(regularExpression) {
						if(name.matches(fileNamePattern)) {
							file = filex;
							break exitloop;
						}
					} else {
						if(name.equals(fileNamePattern)) {
							file = filex;
							break exitloop;
						}
					}
				} else {
					file = filex;
					break exitloop;
				}
			}
		}
		//
		return file;
	}

	private List<File> getCalibrationFiles(File directory, boolean caseSensitive, String extension) {

		List<File> files = new ArrayList<File>();
		for(File file : directory.listFiles()) {
			String name = caseSensitive ? file.getName() : file.getName().toLowerCase();
			if(name.endsWith(extension)) {
				files.add(file);
			}
		}
		/*
		 * Sort alpha-numerically
		 */
		Collections.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
		//
		return files;
	}
}