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
package org.eclipse.chemclipse.chromatogram.wsd.filter.settings;

import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@SystemSettings(SystemSettingsStrategy.NONE)
public class WaveSpectrumSignalRemoverSettings {

	@JsonProperty(value = "Wavelengths", defaultValue = "200 202")
	@JsonPropertyDescription(value = "List the wavelengths, separated by a white space.")
	@StringSettingsProperty(regExp = "(\\d+[;|\\s]?)+", description = "must be space separated digits.", isMultiLine = false, allowEmpty = false)
	private String wavelengthsToRemove = "200 202";
	@JsonProperty(value = "Mode", defaultValue = "INCLUDE")
	@JsonPropertyDescription(value = "Gives the mode to use (include = remove all wavelengths given in the list, exclude = remove all wavelengths not in the list)")
	private MarkedTraceModus markMode = MarkedTraceModus.INCLUDE;

	public String getWavelengthsToRemove() {

		return wavelengthsToRemove;
	}

	public void setWavelengthsToRemove(String wavelengthsToRemove) {

		this.wavelengthsToRemove = wavelengthsToRemove;
	}

	public MarkedTraceModus getMarkMode() {

		return markMode;
	}

	public void setMarkMode(MarkedTraceModus markMode) {

		this.markMode = markMode;
	}
}