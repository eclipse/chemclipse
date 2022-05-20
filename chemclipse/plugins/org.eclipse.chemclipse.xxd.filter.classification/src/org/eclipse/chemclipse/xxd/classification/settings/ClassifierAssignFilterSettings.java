/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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
package org.eclipse.chemclipse.xxd.classification.settings;

import org.eclipse.chemclipse.support.settings.ValidatorSettingsProperty;
import org.eclipse.chemclipse.xxd.classification.model.ClassificationDictionary;
import org.eclipse.chemclipse.xxd.classification.validators.ClassificationDictionaryValidator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ClassifierAssignFilterSettings {

	@JsonProperty(value = "Use Regular Expression", defaultValue = "false")
	@JsonPropertyDescription(value = "Use a regular expression to match the target names.")
	private boolean useRegularExpression = false;
	@JsonProperty(value = "Case Sensitive", defaultValue = "false")
	@JsonPropertyDescription(value = "Search case sensitive or ignore the case.")
	private boolean caseSensitive = false;
	@JsonProperty(value = "Match Partly", defaultValue = "true")
	@JsonPropertyDescription(value = "If true, the peak name only needs to be matched partly.")
	private boolean matchPartly = true;
	@JsonProperty(value = "Matching Rules", defaultValue = "")
	@JsonPropertyDescription(value = "List the regular expressions for target names to set classifications.")
	@ValidatorSettingsProperty(validator = ClassificationDictionaryValidator.class)
	private ClassificationDictionary classificationDictionary;

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

	public ClassificationDictionary getClassificationDictionary() {

		return classificationDictionary;
	}

	public void setClassificationDictionary(ClassificationDictionary classificationDictionary) {

		this.classificationDictionary = classificationDictionary;
	}
}