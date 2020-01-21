/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ProcessPeaksByPeakClassifierFilterSettings {

	private final static String REGULAR_EXPRESSION = "([a-zA-Z0-9,'-\\{\\[\\]\\}\\(\\)\\s*]+{2,})";
	private final static String USER_MATCH_EXPRESSION = "";
	private final static String MATCH_CLASSIFICATION = "";

	@JsonProperty(value = "Wildcard search:")
	@JsonPropertyDescription(value = "Use wildcard search to look for parts of names.")

	private boolean wildcardSearch = true;

	@JsonProperty(value = "Ignore uppercase/lowercase:")
	@JsonPropertyDescription(value = "Ignore uppercase/lowercase in the search process.")

	private boolean ignoreUppercase = true;

	@JsonProperty(value = "User defined match expression(s):", defaultValue = USER_MATCH_EXPRESSION)
	@JsonPropertyDescription(value = "User defined match expression(s) to search for.")
	@StringSettingsProperty(regExp = REGULAR_EXPRESSION, isMultiLine = true)

	private String userDefinedMatchExpression= USER_MATCH_EXPRESSION;

	@JsonProperty(value = "User defined match classification(s):", defaultValue = MATCH_CLASSIFICATION)
	@JsonPropertyDescription(value = "User defined match classification(s) to apply.")
	@StringSettingsProperty(regExp = REGULAR_EXPRESSION, isMultiLine = true)

	private String matchClassification= MATCH_CLASSIFICATION;

	public String getUserDefinedMatchExpression() {

		return userDefinedMatchExpression;
	}

	public void setUserDefinedMatchExpression(String userDefinedMatchExpression) {

		this.userDefinedMatchExpression = userDefinedMatchExpression;
	}

	public String getMatchClassification() {

		return matchClassification;
	}

	public void setMatchClassification(String matchClassification) {

		this.matchClassification = matchClassification;
	}

	public boolean isWildcardSearch() {

		return wildcardSearch;
	}

	public void setWildcardSearch(boolean wildcardSearch) {

		this.wildcardSearch = wildcardSearch;
	}

	public boolean isIgnoreUppercase() {

		return ignoreUppercase;
	}

	public void setIgnoreUppercase(boolean ignoreUppercase) {

		this.ignoreUppercase = ignoreUppercase;
	}
}
