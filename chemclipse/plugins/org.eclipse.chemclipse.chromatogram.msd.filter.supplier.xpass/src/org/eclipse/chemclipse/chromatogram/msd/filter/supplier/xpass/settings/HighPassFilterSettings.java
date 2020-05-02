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

public class HighPassFilterSettings extends AbstractMassSpectrumFilterSettings {

	@JsonProperty(value = "Number Highest", defaultValue = "5")
	@JsonPropertyDescription(value = "This value defines the number of n highest ions to be preserved.")
	@IntSettingsProperty(minValue = 2, maxValue = 100)
	private int numberHighest = 5;

	public int getNumberHighest() {

		return numberHighest;
	}

	public void setNumberHighest(int numberHighest) {

		this.numberHighest = numberHighest;
	}
}
