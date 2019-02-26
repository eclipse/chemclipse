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
package org.eclipse.chemclipse.xxd.process.supplier;

import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.xxd.process.preferences.PreferenceSupplier;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChromatogramExportSettings implements IProcessSettings {

	@JsonProperty(value = "Export Folder (leave empty for default)", defaultValue = "")
	@FileSettingProperty(onlyDirectory = true)
	private String exportFolder;

	public String getExportFolder() {

		if(exportFolder == null || exportFolder.isEmpty()) {
			return PreferenceSupplier.getChromatogramExportFolder();
		}
		return exportFolder;
	}

	public void setExportFolder(String exportFolder) {

		this.exportFolder = exportFolder;
	}
}
