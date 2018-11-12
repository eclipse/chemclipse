/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
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

public class MassSpectrumFilterSettings extends AbstractMassSpectrumFilterSettings {

	@JsonProperty(value = "Number Highes", defaultValue = "5")
	@IntSettingsProperty(minValue = 2, maxValue = 50)
	private int numberHighest = 5;
	@JsonProperty(value = "Number Lowest", defaultValue = "5")
	@IntSettingsProperty(minValue = 2, maxValue = 50)
	private int numberLowest = 5;

	public int getNumberHighest() {

		return numberHighest;
	}

	public void setNumberHighest(int numberHighest) {

		this.numberHighest = numberHighest;
	}

	public int getNumberLowest() {

		return numberLowest;
	}

	public void setNumberLowest(int numberLowest) {

		this.numberLowest = numberLowest;
	}
}
