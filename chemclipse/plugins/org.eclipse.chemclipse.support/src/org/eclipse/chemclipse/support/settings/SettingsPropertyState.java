/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
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
package org.eclipse.chemclipse.support.settings;

/**
 * Overrides the static settings annotations, e.g. @JsonPropertyDescription(...)
 * and @StringSettingsProperty(...), of a field on demand.
 */
public class SettingsPropertyState {

	private boolean enabled = true;
	private String description = null;
	private boolean allowEmpty = true;
	private boolean multiLine = false;
	private String regExp = "";
	private String regExpDescription = "must match the pattern.";

	public static SettingsPropertyState create() {

		return new SettingsPropertyState();
	}

	public boolean isEnabled() {

		return enabled;
	}

	public SettingsPropertyState enabled(boolean enabled) {

		this.enabled = enabled;
		return this;
	}

	/**
	 * Returns null if the description defined via @JsonPropertyDescription shall be used.
	 * 
	 * @return String
	 */
	public String getDescription() {

		return description;
	}

	public SettingsPropertyState description(String description) {

		this.description = description;
		return this;
	}

	public boolean isAllowEmpty() {

		return allowEmpty;
	}

	public SettingsPropertyState allowEmpty(boolean allowEmpty) {

		this.allowEmpty = allowEmpty;
		return this;
	}

	/**
	 * The text widget style can't be changed afterwards.
	 * Hence, this option is used for the validation only.
	 * 
	 * @return boolean
	 */
	public boolean isMultiLine() {

		return multiLine;
	}

	public SettingsPropertyState multiLine(boolean multiLine) {

		this.multiLine = multiLine;
		return this;
	}

	/**
	 * "" skips the regular expression validation.
	 * 
	 * @return String
	 */
	public String getRegExp() {

		return regExp;
	}

	public SettingsPropertyState regExp(String regExp) {

		this.regExp = (regExp == null) ? "" : regExp;
		return this;
	}

	public String getRegExpDescription() {

		return regExpDescription;
	}

	public SettingsPropertyState regExpDescription(String regExpDescription) {

		this.regExpDescription = regExpDescription;
		return this;
	}
}