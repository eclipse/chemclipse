/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.vsd.filter.settings;

import org.eclipse.chemclipse.chromatogram.vsd.filter.model.WavenumberSignals;
import org.eclipse.chemclipse.chromatogram.vsd.filter.validators.WavenumberSignalsValidator;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;
import org.eclipse.chemclipse.support.settings.ValidatorSettingsProperty;

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
	@JsonProperty(value = "Wavenumber Signals", defaultValue = "")
	@JsonPropertyDescription(value = "Use the following signals to clean the chromatogram.")
	@ValidatorSettingsProperty(validator = WavenumberSignalsValidator.class)
	private WavenumberSignals wavenumberSignals;

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

	public WavenumberSignals getWavenumberSignals() {

		return wavenumberSignals;
	}

	public void setWavenumberSignals(WavenumberSignals wavenumberSignals) {

		this.wavenumberSignals = wavenumberSignals;
	}
}