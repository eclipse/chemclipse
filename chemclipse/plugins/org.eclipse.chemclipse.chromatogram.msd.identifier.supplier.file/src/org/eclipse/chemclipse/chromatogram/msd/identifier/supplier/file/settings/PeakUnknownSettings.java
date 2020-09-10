/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.PeakIdentifierAdapterSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.identifier.GeneratedIdentifierSettings;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

@GeneratedIdentifierSettings
public class PeakUnknownSettings extends PeakIdentifierAdapterSettingsMSD {

	@JsonProperty(value = "Min Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float minMatchFactor = 80.0f;
	@JsonProperty(value = "Min Reverse Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float minReverseMatchFactor = 80.0f;

	public float getMinMatchFactor() {

		return minMatchFactor;
	}

	public void setMinMatchFactor(float minMatchFactor) {

		this.minMatchFactor = minMatchFactor;
	}

	public float getMinReverseMatchFactor() {

		return minReverseMatchFactor;
	}

	public void setMinReverseMatchFactor(float minReverseMatchFactor) {

		this.minReverseMatchFactor = minReverseMatchFactor;
	}
}
