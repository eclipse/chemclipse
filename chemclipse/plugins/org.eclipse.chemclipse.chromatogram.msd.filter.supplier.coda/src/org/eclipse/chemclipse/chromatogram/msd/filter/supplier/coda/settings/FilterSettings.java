/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilterSettings extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Coda Threshold", defaultValue = "0.75f")
	@FloatSettingsProperty(minValue = PreferenceSupplier.CODA_THRESHOLD_MIN_VALUE, maxValue = PreferenceSupplier.CODA_THRESHOLD_MAX_VALUE, step = 0.05f)
	private float codaThreshold;

	public float getCodaThreshold() {

		return codaThreshold;
	}

	public void setCodaThreshold(float codaThreshold) {

		this.codaThreshold = codaThreshold;
	}
}
