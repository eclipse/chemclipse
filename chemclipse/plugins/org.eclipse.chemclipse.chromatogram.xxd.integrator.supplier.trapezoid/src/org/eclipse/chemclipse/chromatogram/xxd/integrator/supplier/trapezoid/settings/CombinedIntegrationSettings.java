/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.AbstractCombinedIntegrationSettings;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CombinedIntegrationSettings extends AbstractCombinedIntegrationSettings {

	/*
	 * There are problems with nested classes. Maybe try:
	 * @JsonDeserialize(using = ClassDeserializer.class)
	 */
	@JsonIgnore
	private ChromatogramIntegrationSettings chromatogramIntegrationSettings = new ChromatogramIntegrationSettings();
	@JsonIgnore
	private PeakIntegrationSettings peakIntegrationSettings = new PeakIntegrationSettings();

	public CombinedIntegrationSettings() {
	}

	public CombinedIntegrationSettings(ChromatogramIntegrationSettings chromatogramIntegrationSettings, PeakIntegrationSettings peakIntegrationSettings) {
		this.chromatogramIntegrationSettings = chromatogramIntegrationSettings;
		this.peakIntegrationSettings = peakIntegrationSettings;
	}

	public ChromatogramIntegrationSettings getChromatogramIntegrationSettings() {

		return chromatogramIntegrationSettings;
	}

	public PeakIntegrationSettings getPeakIntegrationSettings() {

		return peakIntegrationSettings;
	}
}
