/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.AbstractChromatogramIntegrationSettings;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ChromatogramIntegrationSettings extends AbstractChromatogramIntegrationSettings {

	@JsonProperty(value = "Scale Factor", defaultValue = "1.0")
	@JsonPropertyDescription(value = "To enable a comparison of the area with other systems, a scale factor can be used.")
	@DoubleSettingsProperty(minValue = Double.MIN_VALUE, maxValue = Double.MAX_VALUE)
	private double scaleFactor = 1.0d;

	public double getScaleFactor() {

		return scaleFactor;
	}

	public void setScaleFactor(double scaleFactor) {

		this.scaleFactor = scaleFactor;
	}
}