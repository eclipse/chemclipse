/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.massspectrum;

import java.io.File;

import org.eclipse.chemclipse.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.settings.AbstractProcessSettings;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IStandaloneMassSpectrum;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class MassSpectrumExportSettings extends AbstractProcessSettings {

	@JsonProperty(value = "Export Folder", defaultValue = "")
	@FileSettingProperty(onlyDirectory = true, dialogType = DialogType.SAVE_DIALOG)
	@JsonPropertyDescription("Set an absolute folder or use the variables.\n" + //
			"Variables:\n" + //
			VARIABLE_CURRENT_DIRECTORY //
	)
	private File exportFolder;

	@JsonProperty(value = "File Name", defaultValue = VARIABLE_MASSSPECTRUM_NAME + VARIABLE_EXTENSION)
	@JsonPropertyDescription("Set a specific name or use the variables or a combination.\n" + //
			"Variables:\n" + //
			VARIABLE_MASSSPECTRUM_NAME + "\n" + //
			VARIABLE_MASSSPECTRUM_SAMPLENAME + "\n" + //
			VARIABLE_MASSSPECTRUM_OPERATOR + "\n" + //
			VARIABLE_MASSSPECTRUM_INSTRUMENT + "\n" + //
			VARIABLE_MASSSPECTRUM_PLATE + "\n" + //
			VARIABLE_MASSSPECTRUM_POSITION + "\n" + //
			VARIABLE_EXTENSION //
	)
	private String filenamePattern = VARIABLE_MASSSPECTRUM_NAME + VARIABLE_EXTENSION;

	@JsonProperty(value = "Append", defaultValue = "false")
	@JsonPropertyDescription("Append the mass spectra to the file instead of replacing it. Use a static file name to collect all mass spectra of a batch job in one file.")
	private boolean append = false;

	public File getExportFolder() {

		if(exportFolder == null) {
			String folder = PreferenceSupplier.getMassSpectrumExportFolder();
			if(folder != null && !folder.isEmpty()) {
				return new File(folder);
			}
		}
		return exportFolder;
	}

	public void setExportFolder(File exportFolder) {

		this.exportFolder = exportFolder;
	}

	public String getFileNamePattern() {

		/*
		 * Check to enable backward compatibility
		 */
		if(filenamePattern == null) {
			return VARIABLE_MASSSPECTRUM_NAME + VARIABLE_EXTENSION;
		}
		return filenamePattern;
	}

	public void setFilenamePattern(String filenamePattern) {

		this.filenamePattern = filenamePattern;
	}

	public boolean isAppend() {

		return append;
	}

	public void setAppend(boolean append) {

		this.append = append;
	}

	/**
	 * Returns the file to export the given mass spectrum to.
	 * May return null if the current directory is used but can't be determined.
	 * 
	 * @param extension
	 * @param massSpectrum
	 * @return File
	 */
	@JsonIgnore
	public File getExportFile(String extension, IScanMSD massSpectrum) {

		String exportPath = resolvedFolders(massSpectrum);
		if(exportPath == null) {
			return null;
		}
		return new File(exportPath, getFileName(massSpectrum, getFileNamePattern(), extension));
	}

	private String resolvedFolders(IScanMSD massSpectrum) {

		String exportPath = getExportFolder().getAbsolutePath();
		if(exportPath.contains(IProcessSettings.VARIABLE_CURRENT_DIRECTORY)) {
			File currentDirectory = getCurrentDirectory(massSpectrum);
			if(currentDirectory == null) {
				return null;
			}
			exportPath = getCleanedFileValue(exportPath);
			exportPath = exportPath.replace(IProcessSettings.VARIABLE_CURRENT_DIRECTORY, currentDirectory.getAbsolutePath());
		}
		return exportPath;
	}

	private File getCurrentDirectory(IScanMSD massSpectrum) {

		if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum && standaloneMassSpectrum.getFile() != null) {
			return standaloneMassSpectrum.getFile().getParentFile();
		}
		IChromatogram chromatogram = massSpectrum.getParentChromatogram();
		if(chromatogram != null && chromatogram.getFile() != null) {
			return chromatogram.getFile().getParentFile();
		}
		return null;
	}

	private String getFileName(IScanMSD massSpectrum, String fileNamePattern, String extension) {

		String fileName = replaceFileName(massSpectrum, fileNamePattern);
		fileName = replaceFileExtension(fileName, extension);
		return toLegalFileName(fileName);
	}

	private String replaceFileName(IScanMSD massSpectrum, String fileNamePattern) {

		String name = null;
		String sampleName = null;
		String operator = null;
		String instrument = null;
		String plate = null;
		String position = null;
		if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum) {
			name = standaloneMassSpectrum.getName();
			sampleName = standaloneMassSpectrum.getSampleName();
			operator = standaloneMassSpectrum.getOperator();
			instrument = standaloneMassSpectrum.getInstrument();
			plate = standaloneMassSpectrum.getPlate();
			position = standaloneMassSpectrum.getPosition();
		}
		if(name == null || name.isEmpty()) {
			name = massSpectrum.getIdentifier();
		}
		/*
		 * Replace the variable place holder by the according mass spectrum meta data.
		 */
		String fileName = fileNamePattern;
		fileName = replaceVariable(fileName, VARIABLE_MASSSPECTRUM_NAME, name, "MassSpectrum");
		fileName = replaceVariable(fileName, VARIABLE_MASSSPECTRUM_SAMPLENAME, sampleName, "SampleName");
		fileName = replaceVariable(fileName, VARIABLE_MASSSPECTRUM_OPERATOR, operator, "Operator");
		fileName = replaceVariable(fileName, VARIABLE_MASSSPECTRUM_INSTRUMENT, instrument, "Instrument");
		fileName = replaceVariable(fileName, VARIABLE_MASSSPECTRUM_PLATE, plate, "Plate");
		fileName = replaceVariable(fileName, VARIABLE_MASSSPECTRUM_POSITION, position, "Position");

		return fileName;
	}
}
