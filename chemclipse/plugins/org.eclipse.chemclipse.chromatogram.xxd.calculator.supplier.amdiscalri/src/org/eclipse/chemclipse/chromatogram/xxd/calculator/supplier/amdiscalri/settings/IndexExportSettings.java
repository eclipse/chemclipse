/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class IndexExportSettings {

	@JsonProperty(value = "Curate Names", defaultValue = "true")
	@JsonPropertyDescription(value = "Names are matched e.g. to C6 (Hexane).")
	private boolean useCuratedNames = true;
	@JsonProperty(value = "Derive Missing Indices", defaultValue = "true")
	@JsonPropertyDescription(value = "If alkane indices are missing, try to calculate them existing peak retention indices.")
	private boolean deriveMissingIndices = true;

	public boolean isUseCuratedNames() {

		return useCuratedNames;
	}

	public void setUseCuratedNames(boolean useCuratedNames) {

		this.useCuratedNames = useCuratedNames;
	}

	public boolean isDeriveMissingIndices() {

		return deriveMissingIndices;
	}

	public void setDeriveMissingIndices(boolean deriveMissingIndices) {

		this.deriveMissingIndices = deriveMissingIndices;
	}
}