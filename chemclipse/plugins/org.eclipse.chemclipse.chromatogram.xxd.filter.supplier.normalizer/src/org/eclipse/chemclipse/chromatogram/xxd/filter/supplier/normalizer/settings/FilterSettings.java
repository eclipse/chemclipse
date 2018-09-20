/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.normalizer.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettings extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Normalization Base", defaultValue = "1000")
	@JsonPropertyDescription(value = "Use this value to normalize the chromatogram.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_NORMALIZATION_BASE, maxValue = PreferenceSupplier.MAX_NORMALIZATION_BASE)
	private float normalizationBase = PreferenceSupplier.DEF_NORMALIZATION_BASE;

	public float getNormalizationBase() {

		return normalizationBase;
	}

	public void setNormalizationBase(float normalizationBase) {

		if(normalizationBase >= PreferenceSupplier.MIN_NORMALIZATION_BASE && !Float.isNaN(normalizationBase) && !Float.isInfinite(normalizationBase)) {
			this.normalizationBase = normalizationBase;
		}
	}
}
