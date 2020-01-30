/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 * Christoph Läubrich - add option to detect on individual traces
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
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.FilterMode;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons.IonMarkMode;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.support.settings.EnumSelectionRadioButtonsSettingProperty;
import org.eclipse.chemclipse.support.settings.EnumSelectionSettingProperty;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class PeakDetectorSettingsMSD extends AbstractPeakDetectorSettingsMSD {

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
	@JsonProperty(value = "Use Noise-Segments", defaultValue = "false")
	@JsonPropertyDescription(value = "Whether to use Nois-Segments to decide where peaks should be detected, this can improve the sensitivity of the algorithm")
	private boolean useNoiseSegments = false;
	@JsonProperty(value = "Filter Mode", defaultValue = "EXCLUDE")
	private FilterMode filterMode = FilterMode.EXCLUDE;
	@JsonProperty(value = "m/z values to filter", defaultValue = "")
	private String filterIonsString;
	@JsonProperty(value = "Use Individual Traces", defaultValue = "false")
	@JsonPropertyDescription("Uses each ion in the filter-list individualy to detect peaks")
	private boolean useIndividualTraces = false;
	@JsonProperty(value = "Optimize Baseline (VV)", defaultValue = "false")
	private boolean optimizeBaseline = false;

	@JsonIgnore
	public Collection<IMarkedIons> getFilterIons() {

		// if(filterIonsString == null || filterIonsString.isEmpty()) {
		// return Collections.singleton(new MarkedIons(IonMarkMode.INCLUDE));
		// }
		IonMarkMode ionMarkMode;
		switch(getFilterMode()) {
			case EXCLUDE:
				ionMarkMode = IonMarkMode.INCLUDE;
				break;
			case INCLUDE:
				ionMarkMode = IonMarkMode.EXCLUDE;
				break;
			default:
				throw new IllegalArgumentException("Unsupported filter mode " + getFilterMode());
		}
		Set<MarkedIon> ions = parseIons(filterIonsString).stream().map(e -> new MarkedIon(e.doubleValue())).collect(Collectors.toSet());
		if(isUseIndividualTraces()) {
			List<IMarkedIons> list = new ArrayList<>();
			for(MarkedIon ion : ions) {
				MarkedIons ionlist = new MarkedIons(ionMarkMode);
				ionlist.add(ion);
				list.add(ionlist);
			}
			return list;
		} else {
			MarkedIons ionlist = new MarkedIons(ionMarkMode);
			ionlist.addAll(ions);
			return Collections.singleton(ionlist);
		}
	}

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

	public float getMinimumSignalToNoiseRatio() {

		return minimumSignalToNoiseRatio;
	}

	public void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio) {

		this.minimumSignalToNoiseRatio = minimumSignalToNoiseRatio;
	}

	public WindowSize getMovingAverageWindowSize() {

		return windowSize;
	}

	public void setMovingAverageWindowSize(WindowSize windowSize) {

		this.windowSize = windowSize;
	}

	public FilterMode getFilterMode() {

		return filterMode == null ? FilterMode.EXCLUDE : filterMode;
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

	static Collection<Number> parseIons(String filterIonsString) {

		if(StringUtils.isBlank(filterIonsString)) {
			return Collections.emptyList();
		}
		List<Number> ionNumbers = new ArrayList<>();
		String[] split = filterIonsString.trim().split("[\\s.,;]+");
		for(String s : split) {
			try {
				ionNumbers.add(new BigDecimal(s));
			} catch(NumberFormatException e) {
				// invalid or empty string
			}
		}
		return ionNumbers;
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

	public boolean isUseIndividualTraces() {

		return useIndividualTraces;
	}

	public void setUseIndividualTraces(boolean useIndividualTraces) {

		this.useIndividualTraces = useIndividualTraces;
	}
}
