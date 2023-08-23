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
package org.eclipse.chemclipse.chromatogram.isd.filter.settings;

import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@SystemSettings(SystemSettingsStrategy.NONE)
public class WavenumberSubtractorSettings {

	@JsonProperty(value = "Nominalize Wavenumber", defaultValue = "false")
	@JsonPropertyDescription(value = "If false, the exact wavenumber is matched.")
	private boolean nominalizeWavenumber = false;
	@JsonProperty(value = "Normalize Intensity", defaultValue = "false")
	@JsonPropertyDescription(value = "If true, the intensities are normalized first and then subtracted.")
	private boolean normalizeIntensity = false;
	@JsonProperty(value = "Subtract Signals", defaultValue = "")
	@JsonPropertyDescription(value = "Use the following signals to clean the chromatogram.")
	@StringSettingsProperty(isMultiLine = true, allowEmpty = false)
	private String subtractSignals = "";

	public boolean isNominalizeWavenumber() {

		return nominalizeWavenumber;
	}

	public void setNominalizeWavenumber(boolean nominalizeWavenumber) {

		this.nominalizeWavenumber = nominalizeWavenumber;
	}

	public boolean isNormalizeIntensity() {

		return normalizeIntensity;
	}

	public void setNormalizeIntensity(boolean normalizeIntensity) {

		this.normalizeIntensity = normalizeIntensity;
	}

	public String getSubtractSignals() {

		return subtractSignals;
	}

	public void setSubtractSignals(String subtractSignals) {

		this.subtractSignals = subtractSignals;
	}
}