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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.MassSpectrumIdentifierAdapterSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MassSpectrumIdentifierAlkaneSettings extends MassSpectrumIdentifierAdapterSettings {

	@JsonProperty(value = "Number of Targets", defaultValue = "15")
	private int numberOfTargets = 15;
	@JsonProperty(value = "Min Match Factor", defaultValue = "80.0")
	private float minMatchFactor = 80.0f;
	@JsonProperty(value = "Min Reverse Match Factor", defaultValue = "80.0")
	private float minReverseMatchFactor = 80.0f;

	public int getNumberOfTargets() {

		return numberOfTargets;
	}

	public void setNumberOfTargets(int numberOfTargets) {

		this.numberOfTargets = numberOfTargets;
	}

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
