/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - remove the window size enum
 * Matthias Mailänder - add wavelength filters
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.FilterMode;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.AbstractPeakDetectorWSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.chemclipse.support.settings.serialization.WindowSizeDeserializer;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class PeakDetectorSettingsWSD extends AbstractPeakDetectorWSDSettings {

	@JsonProperty(value = "Threshold", defaultValue = "MEDIUM")
	private Threshold threshold = Threshold.MEDIUM;
	@JsonProperty(value = "Include Background", defaultValue = "false")
	@JsonPropertyDescription("VV: true, BV|VB: false")
	private boolean includeBackground = false;
	@JsonProperty(value = "Min S/N Ratio", defaultValue = "0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_SN_RATIO_MIN, maxValue = PreferenceSupplier.MIN_SN_RATIO_MAX)
	private float minimumSignalToNoiseRatio;
	@JsonProperty(value = "Window Size", defaultValue = "5")
	@JsonPropertyDescription(value = "Window Size: 3, 5, 7, ..., 45")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_WINDOW_SIZE, maxValue = PreferenceSupplier.MAX_WINDOW_SIZE, validation = Validation.ODD_NUMBER_INCLUDING_ZERO)
	@JsonDeserialize(using = WindowSizeDeserializer.class)
	private int windowSize;
	@JsonProperty(value = "Use Noise-Segments", defaultValue = "false")
	@JsonPropertyDescription(value = "Whether to use noise segments to decide where peaks should be detected. This can improve the sensitivity of the algorithm.")
	private boolean useNoiseSegments = false;
	@JsonProperty(value = "Filter Mode", defaultValue = "EXCLUDE")
	private FilterMode filterMode = FilterMode.EXCLUDE;
	@JsonProperty(value = "Wavelengths to filter", defaultValue = "")
	private String filterWavelengths;
	@JsonProperty(value = "Use Individual Wavelengths", defaultValue = "false")
	@JsonPropertyDescription("Uses each wavelength in the filter-list individualy to detect peaks")
	private boolean useIndividualWavelengths = false;
	@JsonProperty(value = "Optimize Baseline (VV)", defaultValue = "false")
	private boolean optimizeBaseline = false;

	public PeakDetectorSettingsWSD() {

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

	public FilterMode getFilterMode() {

		return filterMode == null ? FilterMode.EXCLUDE : filterMode;
	}

	public void setFilterMode(FilterMode filterMode) {

		this.filterMode = filterMode;
	}

	static Collection<Number> parseWavelengths(String input) {

		if(StringUtils.isBlank(input)) {
			return Collections.emptyList();
		}
		List<Number> waveLengths = new ArrayList<>();
		String[] split = input.trim().split("[\\s.,;]+");
		for(String s : split) {
			try {
				waveLengths.add(new BigDecimal(s));
			} catch(NumberFormatException e) {
				// invalid or empty string
			}
		}
		return waveLengths;
	}

	public boolean isIndividualWavelengths() {

		return useIndividualWavelengths;
	}

	public void setUseIndividualTraces(boolean useIndividualWavelengths) {

		this.useIndividualWavelengths = useIndividualWavelengths;
	}

	@JsonIgnore
	public Collection<IMarkedWavelengths> getFilterWavelengths() {

		MarkedTraceModus markedTraceModus;
		switch(getFilterMode()) {
			case EXCLUDE:
				markedTraceModus = MarkedTraceModus.INCLUDE;
				break;
			case INCLUDE:
				markedTraceModus = MarkedTraceModus.EXCLUDE;
				break;
			default:
				throw new IllegalArgumentException("Unsupported filter mode " + getFilterMode());
		}
		Set<IMarkedWavelength> parsedWavelengths = parseWavelengths(filterWavelengths).stream().map(e -> new MarkedWavelength(e.doubleValue())).collect(Collectors.toSet());
		if(isIndividualWavelengths()) {
			List<IMarkedWavelengths> listedWavelengths = new ArrayList<>();
			for(IMarkedWavelength wavelength : parsedWavelengths) {
				IMarkedWavelengths markedWavelengths = new MarkedWavelengths(markedTraceModus);
				markedWavelengths.add(wavelength);
				listedWavelengths.add(markedWavelengths);
			}
			return listedWavelengths;
		} else {
			IMarkedWavelengths markedWavelengths = new MarkedWavelengths(markedTraceModus);
			markedWavelengths.addAll(parsedWavelengths);
			return Collections.singleton(markedWavelengths);
		}
	}

	public boolean isOptimizeBaseline() {

		return optimizeBaseline;
	}

	public void setOptimizeBaseline(boolean optimizeBaseline) {

		this.optimizeBaseline = optimizeBaseline;
	}
}
