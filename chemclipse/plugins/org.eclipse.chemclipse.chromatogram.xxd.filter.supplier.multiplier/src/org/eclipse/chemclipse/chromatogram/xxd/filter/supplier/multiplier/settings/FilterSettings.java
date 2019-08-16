/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettings extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Multiplier", defaultValue = "1")
	@JsonPropertyDescription(value = "The factor to multiply the signals.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_MULTIPLIER, maxValue = PreferenceSupplier.MAX_MULTIPLIER)
	private float multiplier = 1.0f;
	@JsonProperty(value = "Divisor", defaultValue = "1")
	@JsonPropertyDescription(value = "The factor to divede the signals.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_MULTIPLIER, maxValue = PreferenceSupplier.MAX_MULTIPLIER)
	private float divisor = 1.0f;

	public float getMultiplier() {

		return multiplier;
	}

	public void setMultiplier(float multiplier) {

		this.multiplier = multiplier;
	}

	public float getDivisor() {

		return divisor;
	}

	public void setDivisor(float divisor) {

		this.divisor = divisor;
	}
}
