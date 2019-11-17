/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.settings;

import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.support.SegmentWidth;
import org.eclipse.chemclipse.support.settings.ComboSettingsProperty;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@SystemSettings(SystemSettingsStrategy.NEW_INSTANCE)
public class NoiseChromatogramClassifierSettings implements IChromatogramClassifierSettings {

	@JsonProperty(value = "Chromatogram Segmentation Width")
	private SegmentWidth segmentWidth;
	@JsonProperty(value = "Noise Calculator")
	@ComboSettingsProperty(NoiseCalculatorComboSupplier.class)
	private String noiseCalculatorId;

	public NoiseChromatogramClassifierSettings() {
		segmentWidth = PreferenceSupplier.getDefaultSegmentWidth();
		noiseCalculatorId = PreferenceSupplier.getSelectedNoiseCalculatorId();
	}

	public SegmentWidth getSegmentWidth() {

		return segmentWidth;
	}

	public void setSegmentWidth(SegmentWidth segmentWidth) {

		this.segmentWidth = segmentWidth;
	}

	public String getNoiseCalculatorId() {

		return noiseCalculatorId;
	}

	public void setNoiseCalculatorId(String noiseCalculatorId) {

		this.noiseCalculatorId = noiseCalculatorId;
	}

	@JsonIgnore
	public INoiseCalculator getNoiseCalculator() {

		return NoiseCalculator.getNoiseCalculator(getNoiseCalculatorId());
	}
}
