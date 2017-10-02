/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.PeakIntegrationSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrapezoidPeakIntegrationSettings extends PeakIntegrationSettings implements ITrapezoidPeakIntegrationSettings {

	@JsonProperty(value = "Include Background", defaultValue = "false")
	private boolean includeBackground = false;

	@Override
	public boolean isIncludeBackground() {

		return includeBackground;
	}

	@Override
	public void setIncludeBackground(boolean includeBackground) {

		this.includeBackground = includeBackground;
	}
}
