/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring ILabel support
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class MinimumTracesFilterSettings {

	@JsonProperty(value = "Minimum m/z", defaultValue = "3")
	@JsonPropertyDescription(value = "The minimum number of m/z a peak must contain to be retained")
	@IntSettingsProperty(minValue = 1, maxValue = 100)
	private int minNumberOfIons = 3;

	public int getMinNumberOfIons() {

		return minNumberOfIons;
	}

	public void setMinNumberOfIons(int minNumberOfIons) {

		this.minNumberOfIons = minNumberOfIons;
	}
}