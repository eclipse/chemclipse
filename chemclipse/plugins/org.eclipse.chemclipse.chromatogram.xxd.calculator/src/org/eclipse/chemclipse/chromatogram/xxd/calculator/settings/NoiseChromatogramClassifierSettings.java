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
 * Matthias Mailänder - removed the segment width enum
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.settings;

import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.ComboSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;
import org.eclipse.chemclipse.support.settings.serialization.SegmentWidthDeserializier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@SystemSettings(SystemSettingsStrategy.NEW_INSTANCE)
public class NoiseChromatogramClassifierSettings implements IChromatogramClassifierSettings {

	@JsonProperty(value = "Chromatogram Segmentation Width", defaultValue = "9")
	@JsonPropertyDescription(value = "Segment Width: 5, 7, 9, ..., 19")
	@JsonDeserialize(using = SegmentWidthDeserializier.class)
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_SEGMENT_SIZE, maxValue = PreferenceSupplier.MAX_SEGMENT_SIZE, validation = Validation.ODD_NUMBER)
	private int segmentWidth;
	@JsonProperty(value = "Noise Calculator")
	@ComboSettingsProperty(NoiseCalculatorComboSupplier.class)
	private String noiseCalculatorId;

	public NoiseChromatogramClassifierSettings() {

		segmentWidth = PreferenceSupplier.getSelectedSegmentWidth();
		noiseCalculatorId = PreferenceSupplier.getSelectedNoiseCalculatorId();
	}

	public int getSegmentWidth() {

		return segmentWidth;
	}

	public void setSegmentWidth(int segmentWidth) {

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
