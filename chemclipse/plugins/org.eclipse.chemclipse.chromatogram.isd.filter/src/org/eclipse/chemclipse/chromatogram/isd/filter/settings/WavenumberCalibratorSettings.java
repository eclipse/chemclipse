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
public class WavenumberCalibratorSettings {

	/*
	 * regExp = "((-?\\d+\\.?\\d?)[\\s]?)+",
	 */
	@JsonProperty(value = "Wavenumbers", defaultValue = "")
	@JsonPropertyDescription(value = "List of the wavenumbers.")
	@StringSettingsProperty(isMultiLine = true, allowEmpty = false)
	private String wavenumbers = "";

	public String getWavenumbers() {

		return wavenumbers;
	}

	public void setWavenumbers(String wavenumbers) {

		this.wavenumbers = wavenumbers;
	}
}