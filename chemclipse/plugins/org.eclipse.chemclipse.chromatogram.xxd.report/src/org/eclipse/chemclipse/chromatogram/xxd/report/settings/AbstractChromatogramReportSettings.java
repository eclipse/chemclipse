/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
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

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractChromatogramReportSettings extends AbstractProcessSettings implements IChromatogramReportSettings {

	@JsonProperty(value = "Export Folder", defaultValue = "")
	@FileSettingProperty(onlyDirectory = true)
	private File exportFolder;
	@JsonProperty(value = "Append", defaultValue = "false")
	private boolean append;
	@JsonProperty(value = "Filename", defaultValue = VARIABLE_CHROMATOGRAM_NAME + VARIABLE_EXTENSION)
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

	protected abstract String getDefaultFolder();

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
}
