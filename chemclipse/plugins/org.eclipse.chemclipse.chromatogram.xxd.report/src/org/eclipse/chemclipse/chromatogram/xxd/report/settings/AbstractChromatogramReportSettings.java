/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for settings defined in interface
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.settings;

import java.io.File;

import org.eclipse.chemclipse.model.settings.AbstractProcessSettings;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public abstract class AbstractChromatogramReportSettings extends AbstractProcessSettings implements IChromatogramReportSettings {

	@JsonProperty(value = "Export Folder", defaultValue = "", required = true)
	@FileSettingProperty(onlyDirectory = true, dialogType = DialogType.SAVE_DIALOG)
	private File exportFolder;
	@JsonProperty(value = "Append", defaultValue = "false")
	private boolean append = false;
	@JsonProperty(value = "File Name", defaultValue = VARIABLE_CHROMATOGRAM_NAME + VARIABLE_EXTENSION)
	@JsonPropertyDescription("Set a specific name or use the variables or a combination.\n" + //
			"Variables:\n" + //
			VARIABLE_CHROMATOGRAM_NAME + "\n" + //
			VARIABLE_CHROMATOGRAM_DATANAME + "\n" + //
			VARIABLE_CHROMATOGRAM_SAMPLEGROUP + "\n" + //
			VARIABLE_CHROMATOGRAM_SHORTINFO + "\n" + //
			VARIABLE_EXTENSION //
	)
	private String filenamePattern = VARIABLE_CHROMATOGRAM_NAME + VARIABLE_EXTENSION;

	@Override
	public File getExportFolder() {

		if(exportFolder == null) {
			String folder = getDefaultFolder();
			if(folder != null && !folder.isEmpty()) {
				return new File(folder);
			}
		}
		return exportFolder;
	}

	public void setExportFolder(File exportFolder) {

		this.exportFolder = exportFolder;
	}

	@Override
	public boolean isAppend() {

		return append;
	}

	@Override
	public String getFileNamePattern() {

		return filenamePattern;
	}

	protected abstract String getDefaultFolder();
}
