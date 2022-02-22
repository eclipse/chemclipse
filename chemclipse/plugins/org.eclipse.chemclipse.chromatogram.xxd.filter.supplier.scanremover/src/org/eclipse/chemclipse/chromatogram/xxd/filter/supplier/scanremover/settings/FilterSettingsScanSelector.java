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
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.model.ScanSelectorOption;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsScanSelector extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Scan Selector Option", defaultValue = "RETENTION_TIME_MS")
	@JsonPropertyDescription(value = "Select the option to select the scan in the chromatogram selection.")
	private ScanSelectorOption scanSelectorOption = ScanSelectorOption.RETENTION_TIME_MS;
	@JsonProperty(value = "Scan Selector Value", defaultValue = "1000")
	@JsonPropertyDescription(value = "Select the scan at the given position.")
	@DoubleSettingsProperty(minValue = PreferenceSupplier.MIN_SCAN_SELECTOR_VALUE, maxValue = PreferenceSupplier.MAX_SCAN_SELECTOR_VALUE)
	private double scanSelectorValue = 1000.0d;

	public ScanSelectorOption getScanSelectorOption() {

		return scanSelectorOption;
	}

	public void setScanSelectorOption(ScanSelectorOption scanSelectorOption) {

		this.scanSelectorOption = scanSelectorOption;
	}

	public double getScanSelectorValue() {

		return scanSelectorValue;
	}

	public void setScanSelectorValue(double scanSelectorValue) {

		this.scanSelectorValue = scanSelectorValue;
	}
}