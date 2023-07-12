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

import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@SystemSettings(SystemSettingsStrategy.NONE)
public class WavenumberRemoverSettings {

	@JsonProperty(value = "Wavenumbers", defaultValue = "200 202")
	@JsonPropertyDescription(value = "List the wavenumbers, separated by a white space.")
	@StringSettingsProperty(regExp = "(\\d+[;|\\s]?)+", description = "must be space separated digits.", isMultiLine = false, allowEmpty = false)
	private String wavenumbers = "200 202";
	@JsonProperty(value = "Mode", defaultValue = "INCLUDE")
	@JsonPropertyDescription(value = "Gives the mode to use (include = remove all wavenumbers given in the list, exclude = remove all wavenumbers not in the list)")
	private MarkedTraceModus markMode = MarkedTraceModus.INCLUDE;

	public String getWavenumbers() {

		return wavenumbers;
	}

	public void setWavenumbers(String wavenumbers) {

		this.wavenumbers = wavenumbers;
	}

	public MarkedTraceModus getMarkMode() {

		return markMode;
	}

	public void setMarkMode(MarkedTraceModus markMode) {

		this.markMode = markMode;
	}
}