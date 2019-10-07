/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.Threshold;
import org.eclipse.chemclipse.chromatogram.peak.detector.settings.AbstractPeakDetectorSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.support.settings.EnumSelectionRadioButtonsSettingProperty;
import org.eclipse.chemclipse.support.settings.EnumSelectionSettingProperty;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public abstract class AbstractPeakDetectorSettingsMSD extends AbstractPeakDetectorSettings implements IPeakDetectorSettingsMSD {

	private static final Logger logger = Logger.getLogger(AbstractPeakDetectorSettingsMSD.class);
	@JsonProperty(value = "Threshold", defaultValue = "MEDIUM")
	@EnumSelectionRadioButtonsSettingProperty
	private Threshold threshold = Threshold.MEDIUM;
	@JsonProperty(value = "Include Background (VV: true, BV|VB: false)", defaultValue = "false")
	private boolean includeBackground = false;
	@JsonProperty(value = "Min S/N Ratio", defaultValue = "0")
	@FloatSettingsProperty(minValue = 0f, maxValue = Float.MAX_VALUE)
	private float minimumSignalToNoiseRatio;
	@JsonProperty(value = "Window Size", defaultValue = "WIDTH_5")
	@JsonPropertyDescription(value = "Window Size: 3, 5, 7, ..., 45")
	@EnumSelectionSettingProperty
	private WindowSize windowSize = WindowSize.WIDTH_5;
	@JsonProperty(value = "Ion Filter Mode", defaultValue = "EXCLUDE")
	@EnumSelectionRadioButtonsSettingProperty
	private FilterMode filterMode = FilterMode.EXCLUDE;
	@JsonProperty(value = "Filter Ions", defaultValue = "")
	private String filterIonsString;

	@Override
	public boolean isIncludeBackground() {

		return includeBackground;
	}

	public void setIncludeBackground(boolean includeBackground) {

		this.includeBackground = includeBackground;
	}

	public Threshold getThreshold() {

		return threshold;
	}

	public void setThreshold(Threshold threshold) {

		this.threshold = threshold;
	}

	@Override
	public float getMinimumSignalToNoiseRatio() {

		return minimumSignalToNoiseRatio;
	}

	public void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio) {

		this.minimumSignalToNoiseRatio = minimumSignalToNoiseRatio;
	}

	@Override
	public WindowSize getMovingAverageWindowSize() {

		return windowSize;
	}

	public void setMovingAverageWindowSize(WindowSize windowSize) {

		this.windowSize = windowSize;
	}

	@Override
	public FilterMode getFilterMode() {

		return filterMode;
	}

	public void setFilterMode(FilterMode filterMode) {

		this.filterMode = filterMode;
	}

	public String getFilterIonsString() {

		return filterIonsString;
	}

	public void setFilterIonsString(String filterIonsString) {

		this.filterIonsString = filterIonsString;
	}

	@Override
	public Collection<Number> getFilterIon() {

		return parseIons(filterIonsString);
	}

	static Collection<Number> parseIons(String filterIonsString) {

		List<Number> ionNumbers = new ArrayList<>();
		String[] split = filterIonsString.trim().split("[\\s.,;]+");
		for(String s : split) {
			try {
				ionNumbers.add(new BigDecimal(s));
			} catch(NumberFormatException e) {
				logger.debug("Failed to parse valid input from " + s);
			}
		}
		return ionNumbers;
	}
}
