/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 * Philip Wenig - the exact name option has been added
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import org.eclipse.chemclipse.support.settings.StringSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ClassifierAssignFilterSettings {

	private final static String REGULAR_EXPRESSION = "([a-zA-Z0-9,'-\\{\\[\\]\\}\\(\\)\\s*]+{2,})";
	//
	@JsonProperty(value = "Use Regular Expression", defaultValue = "false")
	@JsonPropertyDescription(value = "Use a regular expression to match the target names.")
	private boolean useRegularExpression = false;
	@JsonProperty(value = "Case Sensitive", defaultValue = "false")
	@JsonPropertyDescription(value = "Search case sensitive or ignore the case.")
	private boolean caseSensitive = false;
	@JsonProperty(value = "Match Partly", defaultValue = "true")
	@JsonPropertyDescription(value = "If true, the peak name only needs to be matched partly.")
	private boolean matchPartly = true;
	@JsonProperty(value = "Match Expression(s)", defaultValue = "")
	@JsonPropertyDescription(value = "List the identification target names to search for.")
	@StringSettingsProperty(regExp = REGULAR_EXPRESSION, isMultiLine = true)
	private String matchExpressions = "";
	@JsonProperty(value = "Match Classification(s)", defaultValue = "")
	@JsonPropertyDescription(value = "If the identification target name is matched, set the classifier.")
	@StringSettingsProperty(regExp = REGULAR_EXPRESSION, isMultiLine = true)
	private String matchClassifications = "";

	public boolean isUseRegularExpression() {

		return useRegularExpression;
	}

	public void setUseRegularExpression(boolean useRegularExpression) {

		this.useRegularExpression = useRegularExpression;
	}

	public boolean isCaseSensitive() {

		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {

		this.caseSensitive = caseSensitive;
	}

	public boolean isMatchPartly() {

		return matchPartly;
	}

	public void setMatchPartly(boolean matchPartly) {

		this.matchPartly = matchPartly;
	}

	public String getMatchExpressions() {

		return matchExpressions;
	}

	public void setMatchExpressions(String matchExpressions) {

		this.matchExpressions = matchExpressions;
	}

	public String getMatchClassifications() {

		return matchClassifications;
	}

	public void setMatchClassification(String matchClassifications) {

		this.matchClassifications = matchClassifications;
	}
}