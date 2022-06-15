/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.settings;

import java.io.File;

import org.eclipse.chemclipse.msd.model.support.CalculationType;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@SystemSettings(SystemSettingsStrategy.NONE)
public class CombinedScanSettings {

	@JsonProperty(value = "File Export", defaultValue = "combinedScans.msl")
	@JsonPropertyDescription(value = "Select an export file.")
	@FileSettingProperty(dialogType = DialogType.SAVE_DIALOG, onlyDirectory = false, validExtensions = {"*.msl"}, extensionNames = {"AMDIS (*.msl)"})
	private File fileExport;
	@JsonProperty(value = "Use Normalized Scan", defaultValue = "true")
	@JsonPropertyDescription(value = "When merging scan, normalize the intensities.")
	private boolean useNormalizedScan = true;
	@JsonProperty(value = "Calculation Type", defaultValue = "SUM")
	@JsonPropertyDescription(value = "Defines how to create combined scans.")
	private CalculationType calculationType = CalculationType.SUM;
	@JsonProperty(value = "Use Peaks Instead Of Scans", defaultValue = "false")
	@JsonPropertyDescription(value = "Use peaks instead of scans to calculate the combined spectrum.")
	private boolean usePeaksInsteadOfScans = false;
	@JsonProperty(value = "Append", defaultValue = "true")
	@JsonPropertyDescription(value = "Append the exported data.")
	private boolean append = true;

	public File getFileExport() {

		return fileExport;
	}

	public void setFileExport(File fileExport) {

		this.fileExport = fileExport;
	}

	public boolean isUseNormalizedScan() {

		return useNormalizedScan;
	}

	public void setUseNormalizedScan(boolean useNormalizedScan) {

		this.useNormalizedScan = useNormalizedScan;
	}

	public CalculationType getCalculationType() {

		return calculationType;
	}

	public void setCalculationType(CalculationType calculationType) {

		this.calculationType = calculationType;
	}

	public boolean isUsePeaksInsteadOfScans() {

		return usePeaksInsteadOfScans;
	}

	public void setUsePeaksInsteadOfScans(boolean usePeaksInsteadOfScans) {

		this.usePeaksInsteadOfScans = usePeaksInsteadOfScans;
	}

	public boolean isAppend() {

		return append;
	}

	public void setAppend(boolean append) {

		this.append = append;
	}
}