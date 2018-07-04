/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.RetentionTimeMinutesProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class SupplierFilterStretchSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterStretchSettings {

	@JsonProperty(value = "Scan Delay (Minutes)", defaultValue = "0")
	@JsonPropertyDescription(value = "Set the scan delay.")
	@RetentionTimeMinutesProperty(minValue = PreferenceSupplier.STRETCH_MILLISECONDS_SCAN_DELAY_MIN, maxValue = PreferenceSupplier.STRETCH_MILLISECONDS_SCAN_DELAY_MAX)
	private int scanDelay = 0;
	@JsonProperty(value = "Chromatogram Runtime (Minutes)", defaultValue = "300000")
	@JsonPropertyDescription(value = "Set the length of the chromatogram.")
	@RetentionTimeMinutesProperty(minValue = PreferenceSupplier.STRETCH_MILLISECONDS_LENGTH_MIN, maxValue = PreferenceSupplier.STRETCH_MILLISECONDS_LENGTH_MAX)
	private int chromatogramLength = 300000;

	public SupplierFilterStretchSettings() {
		// Default constructor is needed, see filter extension point: filterSettings=
	}

	public SupplierFilterStretchSettings(int chromatogramLength) {
		this.chromatogramLength = chromatogramLength;
	}

	@Override
	public int getScanDelay() {

		return scanDelay;
	}

	@Override
	public void setScanDelay(int scanDelay) {

		this.scanDelay = scanDelay;
	}

	@Override
	public int getChromatogramLength() {

		return chromatogramLength;
	}

	@Override
	public void setChromatogramLength(int chromatogramLength) {

		this.chromatogramLength = chromatogramLength;
	}
}
