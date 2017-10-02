/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.AbstractPeakIdentifierSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VendorPeakIdentifierSettings extends AbstractPeakIdentifierSettings implements IVendorPeakIdentifierSettings {

	@JsonProperty(value = "Match Sensitivity", defaultValue = "80.0")
	private float matchSensitivity;

	@Override
	public float getMatchSensitivity() {

		return matchSensitivity;
	}

	@Override
	public void setMatchSensitivity(float matchSensitivity) {

		this.matchSensitivity = matchSensitivity;
	}
}
