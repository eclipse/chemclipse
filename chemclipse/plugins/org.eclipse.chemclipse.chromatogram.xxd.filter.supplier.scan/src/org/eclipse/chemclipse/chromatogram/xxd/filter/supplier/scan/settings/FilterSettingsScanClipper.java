/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsScanClipper extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Scan Numbers", defaultValue = PreferenceSupplier.DEF_CLIP_SCAN_NUMBER_PATTERN)
	@JsonPropertyDescription(value = "List the scans by number that shall be removed.")
	@StringSettingsProperty(allowEmpty = false)
	private String scanNumberPattern = PreferenceSupplier.DEF_CLIP_SCAN_NUMBER_PATTERN;

	@Override
	public void setSystemSettings() {

		scanNumberPattern = PreferenceSupplier.getScanNumberPattern();
	}

	public String getScanNumberPattern() {

		return scanNumberPattern;
	}

	public void setScanNumberPattern(String scanNumberPattern) {

		this.scanNumberPattern = scanNumberPattern;
	}
}