/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class LowPassFilterSettings extends AbstractMassSpectrumFilterSettings {

	@JsonProperty(value = "Number Lowest", defaultValue = "5")
	@JsonPropertyDescription(value = "This value defines the number of n lowest ions to be preserved.")
	@IntSettingsProperty(minValue = 2, maxValue = 1000)
	private int numberLowest = 5;

	public int getNumberLowest() {

		return numberLowest;
	}

	public void setNumberLowest(int numberLowest) {

		this.numberLowest = numberLowest;
	}
}
