/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.unitsumnormalizer.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class SupplierFilterSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterSettings {

	@JsonProperty(value = "Multiplication Factor", defaultValue = "1")
	@JsonPropertyDescription(value = "Set the multiplication factor.")
	private float multiplicationFactor;

	@Override
	public float getMultiplicationFactor() {

		return multiplicationFactor;
	}

	@Override
	public void setMultiplicationFactor(float multiplicationFactor) {

		this.multiplicationFactor = multiplicationFactor;
	}
}
