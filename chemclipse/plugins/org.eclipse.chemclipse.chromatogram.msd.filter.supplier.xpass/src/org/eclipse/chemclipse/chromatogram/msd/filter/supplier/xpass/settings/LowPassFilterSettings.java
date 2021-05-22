/*******************************************************************************
 * Copyright (c) 2014, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.AbstractMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class LowPassFilterSettings extends AbstractMassSpectrumFilterSettings {

	@JsonProperty(value = "Number Lowest", defaultValue = "5")
	@JsonPropertyDescription(value = "This value defines the number of n lowest ions to be preserved.")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_NUMBER_LOWEST, maxValue = PreferenceSupplier.MAX_NUMBER_LOWEST)
	private int numberLowest = PreferenceSupplier.DEF_NUMBER_LOWEST;

	public int getNumberLowest() {

		return numberLowest;
	}

	public void setNumberLowest(int numberLowest) {

		this.numberLowest = numberLowest;
	}
}
