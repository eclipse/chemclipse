/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.timeranges.settings;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.timeranges.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.validators.TimeRangesValidator;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.ValidatorSettingsProperty;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.PeakFilterOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class PeakIdentifierFilterSettings {

	@JsonProperty(value = "Limit Match Factor", defaultValue = "80.0")
	@JsonPropertyDescription(value = "Run an identification if no target exists with a Match Factor >= the given limit.")
	@FloatSettingsProperty(minValue = IIdentifierSettings.MIN_LIMIT_MATCH_FACTOR, maxValue = IIdentifierSettings.MAX_LIMIT_MATCH_FACTOR)
	private float limitMatchFactor = 80.0f;
	@JsonProperty(value = "Match Quality", defaultValue = "80.0")
	@JsonPropertyDescription(value = "The match quality is set as the Match Factor.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float matchQuality = 80.0f;
	@JsonProperty(value = "Filter Option", defaultValue = "AREA")
	@JsonPropertyDescription(value = "Select the option to filter the peaks.")
	private PeakFilterOption peakFilterOption = PeakFilterOption.AREA;
	@JsonProperty(value = "Time Ranges", defaultValue = "")
	@JsonPropertyDescription(value = "Use the time ranges to identify the peaks.")
	@ValidatorSettingsProperty(validator = TimeRangesValidator.class)
	private TimeRanges timeRanges = new TimeRanges();

	public float getLimitMatchFactor() {

		return limitMatchFactor;
	}

	public void setLimitMatchFactor(float limitMatchFactor) {

		this.limitMatchFactor = limitMatchFactor;
	}

	public float getMatchQuality() {

		return matchQuality;
	}

	public void setMatchQuality(float matchQuality) {

		this.matchQuality = matchQuality;
	}

	public PeakFilterOption getPeakFilterOption() {

		return peakFilterOption;
	}

	public void setPeakFilterOption(PeakFilterOption peakFilterOption) {

		this.peakFilterOption = peakFilterOption;
	}

	public TimeRanges getTimeRanges() {

		return timeRanges;
	}

	public void setTimeRanges(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
	}
}