/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class HighPassPeaksFilterSettings {

	@JsonProperty(value = "Number Highest", defaultValue = "5")
	@JsonPropertyDescription(value = "This value defines the number of n highest peak(s) to be preserved.")
	@IntSettingsProperty(minValue = 2, maxValue = 1000)
	private int numberHighest = 5;

	public int getNumberHighest() {

		return numberHighest;
	}

	public void setNumberHighest(int numberHighest) {

		this.numberHighest = numberHighest;
	}
}
