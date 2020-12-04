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
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DivisorSettings extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Divisor", defaultValue = "1")
	@JsonPropertyDescription(value = "The factor to divide the signals.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_DIVISOR, maxValue = PreferenceSupplier.MAX_DIVISOR)
	private float divisor = 1.0f;

	public float getDivisor() {

		return divisor;
	}

	public void setDivisor(float divisor) {

		this.divisor = divisor;
	}
}
