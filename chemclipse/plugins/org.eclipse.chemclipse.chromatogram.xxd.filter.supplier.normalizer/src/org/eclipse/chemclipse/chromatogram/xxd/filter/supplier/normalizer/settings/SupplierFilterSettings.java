/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.normalizer.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class SupplierFilterSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterSettings {

	@JsonProperty(value = "Normalization Base", defaultValue = "1000")
	@JsonPropertyDescription(value = "Use this value to normalize the chromatogram.")
	private float normalizationBase = ISupplierFilterSettings.DEFAULT_NORMALIZATION_BASE;

	@Override
	public float getNormalizationBase() {

		return normalizationBase;
	}

	@Override
	public void setNormalizationBase(float normalizationBase) {

		if(normalizationBase >= MIN_NORMALIZATION_BASE && !Float.isNaN(normalizationBase) && !Float.isInfinite(normalizationBase)) {
			this.normalizationBase = normalizationBase;
		}
	}
}
