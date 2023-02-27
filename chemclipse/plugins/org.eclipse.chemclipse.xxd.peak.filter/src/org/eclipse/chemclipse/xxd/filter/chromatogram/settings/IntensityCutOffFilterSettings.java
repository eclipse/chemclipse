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
package org.eclipse.chemclipse.xxd.filter.chromatogram.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class IntensityCutOffFilterSettings {

	@JsonProperty(value = "Intensity Option", defaultValue = "RELATIVE")
	@JsonPropertyDescription(value = "Define whether to use absolute or rleative intensities.")
	private IntensityOption intensityOption = IntensityOption.RELATIVE;
	@JsonProperty(value = "Max Intensity", defaultValue = "75.0")
	@JsonPropertyDescription(value = "Cut off intensities higher than the given value.")
	private float maxIntensity = 75.0f;

	public IntensityOption getIntensityOption() {

		return intensityOption;
	}

	public void setIntensityOption(IntensityOption intensityOption) {

		this.intensityOption = intensityOption;
	}

	public float getMaxIntensity() {

		return maxIntensity;
	}

	public void setMaxIntensity(float maxIntensity) {

		this.maxIntensity = maxIntensity;
	}
}