/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.peak;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramExportSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@SystemSettings(SystemSettingsStrategy.NONE)
public class PeakExportSettings {

	public static final String VARIABLE_CHROMATOGRAM_NAME = "{chromatogram_name}";
	public static final String VARIABLE_EXTENSION = "{extension}";
	@JsonProperty(value = "Export Folder", defaultValue = "")
	@FileSettingProperty(onlyDirectory = true)
	private File exportFolder;
	@JsonProperty(value = "Filename", defaultValue = VARIABLE_CHROMATOGRAM_NAME + VARIABLE_EXTENSION)
	private final String filenamePattern = VARIABLE_CHROMATOGRAM_NAME + VARIABLE_EXTENSION;

	@JsonIgnore
	public File getExportFileName(String extension, IChromatogram<?> chromatogram) {

		return new File(getExportFolder(), getFilenamePattern().replace(ChromatogramExportSettings.VARIABLE_CHROMATOGRAM_NAME, chromatogram.getName()).replace(ChromatogramExportSettings.VARIABLE_EXTENSION, extension));
	}

	public File getExportFolder() {

		return exportFolder;
	}

	public void setExportFolder(File exportFolder) {

		this.exportFolder = exportFolder;
	}

	public String getFilenamePattern() {

		return filenamePattern;
	}
}
