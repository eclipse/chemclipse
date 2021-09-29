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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings;

import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class CutOffScanFilterSettings {

	public enum CutDirection {
		HIGH, LOW;
	}

	@JsonProperty(value = "Cut Number", defaultValue = "5")
	@JsonPropertyDescription(value = "Number of Ions to cut off")
	@IntSettingsProperty(minValue = 2, maxValue = 50)
	private int cutNumber = 5;
	@JsonProperty(value = "Cut direction", defaultValue = "LOW")
	@JsonPropertyDescription(value = "The direction to cut off")
	private CutDirection cutDirection = CutDirection.LOW;

	public int getCutNumber() {

		return cutNumber;
	}

	public void setCutNumber(int cutNumber) {

		this.cutNumber = cutNumber;
	}

	public CutDirection getCutDirection() {

		return cutDirection;
	}

	public void setCutDirection(CutDirection cutDirection) {

		this.cutDirection = cutDirection;
	}
}
