/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.settings;

import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.xxd.filter.support.TargetsDeleteOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeleteTargetsFilterSettings {

	@JsonProperty(value = "Target Delete Option", defaultValue = "ALL_TARGETS")
	private TargetsDeleteOption targetDeleteOption = TargetsDeleteOption.ALL_TARGETS;
	@JsonProperty(value = "Property", defaultValue = "")
	@JsonPropertyDescription(value = "Define a property if the appropriate option is selected.")
	@StringSettingsProperty(allowEmpty = true, isMultiLine = false)
	private String property = "";

	public TargetsDeleteOption getTargetDeleteOption() {

		return targetDeleteOption;
	}

	public void setTargetDeleteOption(TargetsDeleteOption targetDeleteOption) {

		this.targetDeleteOption = targetDeleteOption;
	}

	public String getProperty() {

		return property;
	}

	public void setProperty(String property) {

		this.property = property;
	}
}