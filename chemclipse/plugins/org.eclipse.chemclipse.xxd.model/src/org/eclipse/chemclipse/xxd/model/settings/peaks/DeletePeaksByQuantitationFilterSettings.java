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
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.xxd.model.preferences.PreferenceSupplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeletePeaksByQuantitationFilterSettings {

	@JsonProperty(value = "Name", defaultValue = "")
	@JsonPropertyDescription(value = "Delete the peaks with a quantitation of the given name or all if empty.")
	@StringSettingsProperty(allowEmpty = true)
	private String name = "";
	@JsonProperty(value = "Min. Concentration", defaultValue = "")
	@JsonPropertyDescription(value = "Delete the peaks with a concentration lower the given limit.")
	@DoubleSettingsProperty(minValue = PreferenceSupplier.MIN_CONCENTRATION, maxValue = PreferenceSupplier.MAX_CONCENTRATION)
	private double concentration = 0.0d;
	@JsonProperty(value = "Unit", defaultValue = "")
	@JsonPropertyDescription(value = "Delete the peaks with a quantitation of the given unit.")
	@StringSettingsProperty(allowEmpty = false)
	private String unit = "";

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public double getConcentration() {

		return concentration;
	}

	public void setConcentration(double concentration) {

		this.concentration = concentration;
	}

	public String getUnit() {

		return unit;
	}

	public void setUnit(String unit) {

		this.unit = unit;
	}
}