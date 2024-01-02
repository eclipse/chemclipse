/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsSIM extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Limit Ions", defaultValue = "5")
	@JsonPropertyDescription(value = "If the scan contains m/z values <= limit, then assume that it is a SIM.")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_LIMIT_IONS_SIM, maxValue = PreferenceSupplier.MAX_LIMIT_IONS_SIM)
	private int limitIons = PreferenceSupplier.DEF_LIMIT_IONS_SIM;

	public int getLimitIons() {

		return limitIons;
	}

	public void setLimitIons(int limitIons) {

		this.limitIons = limitIons;
	}
}