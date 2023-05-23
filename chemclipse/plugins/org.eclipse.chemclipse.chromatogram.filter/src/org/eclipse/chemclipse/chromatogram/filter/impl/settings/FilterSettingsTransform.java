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
package org.eclipse.chemclipse.chromatogram.filter.impl.settings;

import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsTransform extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "m/z", defaultValue = "28")
	@JsonPropertyDescription(value = "Use the following m/z when transforming the chromatogram to MSD.")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_MZ, maxValue = PreferenceSupplier.MAX_MZ)
	private int mz;

	public int getMz() {

		return mz;
	}

	public void setMz(int mz) {

		this.mz = mz;
	}
}