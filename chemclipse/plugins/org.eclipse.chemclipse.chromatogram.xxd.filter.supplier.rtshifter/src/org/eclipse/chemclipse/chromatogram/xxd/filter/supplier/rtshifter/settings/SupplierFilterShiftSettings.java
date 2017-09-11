/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class SupplierFilterShiftSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterShiftSettings {

	@JsonProperty(value = "Shift Retention Time (Milliseconds)", defaultValue = "0")
	@JsonPropertyDescription(value = "Set retention time shift.")
	private int millisecondsToShift = 0;
	@JsonProperty(value = "Shift All Scans", defaultValue = "true")
	@JsonPropertyDescription(value = "Set shift all scans.")
	private boolean shiftAllScans = true;

	public SupplierFilterShiftSettings() {
		// Default constructor is needed, see filter extension point: filterSettings=
	}

	public SupplierFilterShiftSettings(int millisecondsToShift, boolean shiftAllScans) {
		this.millisecondsToShift = millisecondsToShift;
		this.shiftAllScans = shiftAllScans;
	}

	@Override
	public int getMillisecondsToShift() {

		return millisecondsToShift;
	}

	@Override
	public void setMillisecondsToShift(int millisecondsToShift) {

		this.millisecondsToShift = millisecondsToShift;
	}

	@Override
	public boolean isShiftAllScans() {

		return shiftAllScans;
	}

	@Override
	public void setShiftAllScans(boolean shiftAllScans) {

		this.shiftAllScans = shiftAllScans;
	}
}
