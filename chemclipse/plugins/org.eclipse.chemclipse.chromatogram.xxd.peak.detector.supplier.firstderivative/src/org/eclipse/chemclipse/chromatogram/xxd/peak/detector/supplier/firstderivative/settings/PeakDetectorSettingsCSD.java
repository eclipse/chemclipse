/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - remove the window size enum
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.AbstractPeakDetectorCSDSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.messages.IFirstDerivativeMessages;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.chemclipse.support.settings.LocalisationSettingsProperty;
import org.eclipse.chemclipse.support.settings.serialization.WindowSizeDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class PeakDetectorSettingsCSD extends AbstractPeakDetectorCSDSettings {

	@JsonProperty(value = "Threshold", defaultValue = "MEDIUM")
	@LocalisationSettingsProperty(value = IFirstDerivativeMessages.THRESHOLD)
	private Threshold threshold = Threshold.MEDIUM;
	@JsonProperty(value = "Include Background", defaultValue = "false")
	@JsonPropertyDescription("VV: true, BV|VB: false")
	@LocalisationSettingsProperty(value = IFirstDerivativeMessages.INCLUDE_BACKGROUND, description = IFirstDerivativeMessages.BACKGROUND_DESCRIPTION)
	private boolean includeBackground = false;
	@JsonProperty(value = "Min S/N Ratio", defaultValue = "0")
	@LocalisationSettingsProperty(value = IFirstDerivativeMessages.MIN_SN_RATIO)
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_SN_RATIO_MIN, maxValue = PreferenceSupplier.MIN_SN_RATIO_MAX)
	private float minimumSignalToNoiseRatio;
	@JsonProperty(value = "Window Size", defaultValue = "5")
	@JsonPropertyDescription(value = "Window Size: 3, 5, 7, ..., 45")
	@LocalisationSettingsProperty(value = IFirstDerivativeMessages.WINDOW_SIZE, description = IFirstDerivativeMessages.WINDOW_SIZE_DESCRIPTION)
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_WINDOW_SIZE, maxValue = PreferenceSupplier.MAX_WINDOW_SIZE, validation = Validation.ODD_NUMBER_INCLUDING_ZERO)
	@JsonDeserialize(using = WindowSizeDeserializer.class)
	private int windowSize = 5;
	@JsonProperty(value = "Use Noise-Segments", defaultValue = "false")
	@JsonPropertyDescription(value = "Whether to use noise segments to decide where peaks should be detected. This can improve the sensitivity of the algorithm.")
	private boolean useNoiseSegments = false;
	@JsonProperty(value = "Optimize Baseline (VV)", defaultValue = "false")
	@LocalisationSettingsProperty(value = IFirstDerivativeMessages.OPTIMIZE_BASELINE_VV)
	private boolean optimizeBaseline = false;

	public PeakDetectorSettingsCSD() {

		windowSize = 5;
	}

	public Threshold getThreshold() {

		return threshold;
	}

	public void setThreshold(Threshold threshold) {

		if(threshold != null) {
			this.threshold = threshold;
		}
	}

	public boolean isIncludeBackground() {

		return includeBackground;
	}

	public void setIncludeBackground(boolean includeBackground) {

		this.includeBackground = includeBackground;
	}

	public float getMinimumSignalToNoiseRatio() {

		return minimumSignalToNoiseRatio;
	}

	public void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio) {

		this.minimumSignalToNoiseRatio = minimumSignalToNoiseRatio;
	}

	public int getMovingAverageWindowSize() {

		return windowSize;
	}

	public void setMovingAverageWindowSize(int windowSize) {

		this.windowSize = windowSize;
	}

	public boolean isUseNoiseSegments() {

		return useNoiseSegments;
	}

	public void setUseNoiseSegments(boolean useNoiseSegments) {

		this.useNoiseSegments = useNoiseSegments;
	}

	public boolean isOptimizeBaseline() {

		return optimizeBaseline;
	}

	public void setOptimizeBaseline(boolean optimizeBaseline) {

		this.optimizeBaseline = optimizeBaseline;
	}
}
