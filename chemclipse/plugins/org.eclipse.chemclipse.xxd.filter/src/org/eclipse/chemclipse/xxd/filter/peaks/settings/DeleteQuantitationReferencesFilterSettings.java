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
package org.eclipse.chemclipse.xxd.filter.peaks.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeleteQuantitationReferencesFilterSettings {

	@JsonProperty(value = "Delete Quantitation References", defaultValue = "")
	@JsonPropertyDescription(value = "If empty, all references are deleted. Otherwise the speficied reference if available.")
	private String quantitationReference = "";

	public String getQuantitationReference() {

		return quantitationReference;
	}

	public void setQuantitationReference(String quantitationReference) {

		this.quantitationReference = quantitationReference;
	}
}