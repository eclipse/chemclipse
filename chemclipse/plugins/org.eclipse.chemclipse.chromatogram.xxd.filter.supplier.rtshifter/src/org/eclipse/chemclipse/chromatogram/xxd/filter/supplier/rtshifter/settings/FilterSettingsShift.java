/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsShift extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Shift Retention Time [ms]", defaultValue = "0")
	@JsonPropertyDescription(value = "Set retention time shift.")
	@IntSettingsProperty(minValue = PreferenceSupplier.SHIFT_MILLISECONDS_MIN, maxValue = PreferenceSupplier.SHIFT_MILLISECONDS_MAX)
	private int millisecondsToShift = 0;
	@JsonProperty(value = "Shift All Scans", defaultValue = "true")
	@JsonPropertyDescription(value = "Set shift all scans.")
	private boolean shiftAllScans = true;

	public FilterSettingsShift() {
		// Default constructor is needed, see filter extension point: filterSettings=
	}

	public FilterSettingsShift(int millisecondsToShift, boolean shiftAllScans) {
		this.millisecondsToShift = millisecondsToShift;
		this.shiftAllScans = shiftAllScans;
	}

	public int getMillisecondsToShift() {

		return millisecondsToShift;
	}

	public void setMillisecondsToShift(int millisecondsToShift) {

		this.millisecondsToShift = millisecondsToShift;
	}

	public boolean isShiftAllScans() {

		return shiftAllScans;
	}

	public void setShiftAllScans(boolean shiftAllScans) {

		this.shiftAllScans = shiftAllScans;
	}
}
