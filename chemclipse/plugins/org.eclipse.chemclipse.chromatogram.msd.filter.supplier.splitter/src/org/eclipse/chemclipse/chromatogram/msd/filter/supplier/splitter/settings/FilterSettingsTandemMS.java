/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.model.CondenseOption;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsTandemMS extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Header Field", defaultValue = "DATA_NAME")
	@JsonPropertyDescription(value = "Store the extracted transition in the selected header field.")
	private HeaderField headerField = HeaderField.DATA_NAME;
	@JsonProperty(value = "Specific Transition", defaultValue = "")
	@JsonPropertyDescription(value = "Extract specific transitions, e.g.: '267 > 159.0 @15'")
	@StringSettingsProperty(allowEmpty = true, isMultiLine = true)
	private String specificTransitions = "";
	@JsonProperty(value = "Condense Option", defaultValue = "STANDARD")
	@JsonPropertyDescription(value = "Some instruments store each transition in a separate scan, which leads to slightly deviating retention times.")
	private CondenseOption condenseOption = CondenseOption.STANDARD;

	public HeaderField getHeaderField() {

		return headerField;
	}

	public void setHeaderField(HeaderField headerField) {

		this.headerField = headerField;
	}

	public String getSpecificTransitions() {

		return specificTransitions;
	}

	public void setSpecificTransitions(String specificTransitions) {

		this.specificTransitions = specificTransitions;
	}

	public CondenseOption getCondenseOption() {

		return condenseOption;
	}

	public void setCondenseOption(CondenseOption condenseOption) {

		this.condenseOption = condenseOption;
	}
}