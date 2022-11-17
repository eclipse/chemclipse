/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsRetentionIndexSelector extends AbstractChromatogramFilterSettings {

	public static final String CHROMATOGRAM_COLUMN_TYPE = "{chromatogram}";
	//
	@JsonProperty(value = "Search Column", defaultValue = CHROMATOGRAM_COLUMN_TYPE)
	@JsonPropertyDescription(value = "Select the column that shall be used to select the retention index. Use e.g.:\n" + //
			CHROMATOGRAM_COLUMN_TYPE + "\n" + //
			"polar" + "\n" + //
			"semi-polar" + "\n" + //
			"non-polar (apolar)" + "\n" + //
			"DB 5")
	@StringSettingsProperty(allowEmpty = false)
	private String searchColumn = CHROMATOGRAM_COLUMN_TYPE;
	@JsonProperty(value = "Case Sensitive", defaultValue = "false")
	@JsonPropertyDescription(value = "Search case sensitive")
	private boolean caseSensitive = false;
	@JsonProperty(value = "Remove White-Space", defaultValue = "false")
	@JsonPropertyDescription(value = "Remove white space when searching.")
	private boolean removeWhiteSpace = false;

	public String getSearchColumn() {

		return searchColumn;
	}

	public void setSearchColumn(String searchColumn) {

		this.searchColumn = searchColumn;
	}

	public boolean isCaseSensitive() {

		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {

		this.caseSensitive = caseSensitive;
	}

	public boolean isRemoveWhiteSpace() {

		return removeWhiteSpace;
	}

	public void setRemoveWhiteSpace(boolean removeWhiteSpace) {

		this.removeWhiteSpace = removeWhiteSpace;
	}
}