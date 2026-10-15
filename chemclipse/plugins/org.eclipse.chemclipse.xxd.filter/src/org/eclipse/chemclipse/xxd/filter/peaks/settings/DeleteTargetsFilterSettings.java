/*******************************************************************************
 * Copyright (c) 2020, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks.settings;

import org.eclipse.chemclipse.support.settings.DependencySettingsProperty;
import org.eclipse.chemclipse.xxd.filter.support.TargetsDeleteOption;
import org.eclipse.chemclipse.xxd.filter.support.TargetsDeleteOptionDependency;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeleteTargetsFilterSettings {

	@JsonProperty(value = "Target Delete Option", defaultValue = "ALL_TARGETS")
	@DependencySettingsProperty(dependency = TargetsDeleteOptionDependency.class)
	private TargetsDeleteOption targetDeleteOption = TargetsDeleteOption.ALL_TARGETS;
	@JsonProperty(value = "Property", defaultValue = "")
	@JsonPropertyDescription(value = "Define a property if the appropriate option is selected.")
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