/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import org.eclipse.chemclipse.xxd.model.support.PeaksDeleteOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeletePeaksByTargetFilterSettings {

	@JsonProperty(value = "Target")
	private PeaksDeleteOption peaksDeleteOption = PeaksDeleteOption.NAME;
	@JsonProperty(value = "Value", defaultValue = "")
	@JsonPropertyDescription(value = "Delete peak(s) if at least one target matches the value on the selected option.")
	private String value = "";
	@JsonProperty(value = "Case Sensitive", defaultValue = "false")
	@JsonPropertyDescription(value = "Case sensitive search and matching of targets.")
	private boolean caseSensitive = false;
	@JsonProperty(value = "Regular Expression", defaultValue = "false")
	@JsonPropertyDescription(value = "Use the regular expression for search and matching of targets.")
	private boolean regularExpression = false;

	public PeaksDeleteOption getPeaksDeleteOption() {

		return peaksDeleteOption;
	}

	public void setPeaksDeleteOption(PeaksDeleteOption peaksDeleteOption) {

		this.peaksDeleteOption = peaksDeleteOption;
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public boolean isCaseSensitive() {

		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {

		this.caseSensitive = caseSensitive;
	}

	public boolean isRegularExpression() {

		return regularExpression;
	}

	public void setRegularExpression(boolean regularExpression) {

		this.regularExpression = regularExpression;
	}
}