/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.EnumSelectionSettingProperty;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterAreaTypeSelection;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterSelectionCriterion;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterTreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ProcessPeaksByAreaFilterSettings {

	@JsonProperty(value = "Minimum area [integrated]", defaultValue = "5000000.0")
	@JsonPropertyDescription(value = "The minimum area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 1.0d, maxValue = Double.MAX_VALUE)
	private double minimumAreaValue = 5000000.0d;

	@JsonProperty(value = "Maximum area [integrated]", defaultValue = "10000000.0")
	@JsonPropertyDescription(value = "The maximum area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 1.0d, maxValue = Double.MAX_VALUE)
	private double maximumAreaValue = 10000000.0d;
	
	@JsonProperty(value = "Minimum area [%]", defaultValue = "1.0")
	@JsonPropertyDescription(value = "The minimum percentage area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double minimumPercentageAreaValue = 5000000.0d;

	@JsonProperty(value = "Maximum area [%]", defaultValue = "10.0")
	@JsonPropertyDescription(value = "The maximum percentage area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double maximumPercentageAreaValue = 100.0d;

	@JsonProperty(value = "Peak Area Selection", defaultValue = "INTEGRATED_AREA")
	@EnumSelectionSettingProperty
	private ProcessPeaksByAreaFilterAreaTypeSelection filterAreaTypeSelection = ProcessPeaksByAreaFilterAreaTypeSelection.INTEGRATED_AREA;

	@JsonProperty(value = "Peak Treatment Option", defaultValue = "DISABLE_PEAK")
	@EnumSelectionSettingProperty
	private ProcessPeaksByAreaFilterTreatmentOption filterTreatmentOption = ProcessPeaksByAreaFilterTreatmentOption.DISABLE_PEAK;

	@JsonProperty(value = "Peak Selection Criterion", defaultValue = "AREA_LESS_THAN_MINIMUM")
	@EnumSelectionSettingProperty
	private ProcessPeaksByAreaFilterSelectionCriterion filterSelectionCriterion = ProcessPeaksByAreaFilterSelectionCriterion.AREA_LESS_THAN_MINIMUM;

	public double getMinimumAreaValue() {
		return minimumAreaValue;
	}

	public void setMinimumAreaValue(double minimumAreaValue) {
		this.minimumAreaValue = minimumAreaValue;
	}

	public double getMaximumAreaValue() {
		return maximumAreaValue;
	}

	public void setMaximumAreaValue(double maximumAreaValue) {
		this.maximumAreaValue = maximumAreaValue;
	}

	public double getMinimumPercentageAreaValue() {
		return minimumPercentageAreaValue;
	}

	public void setMinimumPercentageAreaValue(double minimumPercentageAreaValue) {
		this.minimumPercentageAreaValue = minimumPercentageAreaValue;
	}

	public double getMaximumPercentageAreaValue() {
		return maximumPercentageAreaValue;
	}

	public void setMaximumPercentageAreaValue(double maximumPercentageAreaValue) {
		this.maximumPercentageAreaValue = maximumPercentageAreaValue;
	}

	public ProcessPeaksByAreaFilterAreaTypeSelection getFilterAreaTypeSelection() {
		return filterAreaTypeSelection;
	}

	public void setFilterAreaTypeSelection(ProcessPeaksByAreaFilterAreaTypeSelection filterAreaTypeSelection) {
		this.filterAreaTypeSelection = filterAreaTypeSelection;
	}

	public ProcessPeaksByAreaFilterTreatmentOption getFilterTreatmentOption() {
		return filterTreatmentOption;
	}

	public void setFilterTreatmentOption(ProcessPeaksByAreaFilterTreatmentOption filterTreatmentOption) {
		this.filterTreatmentOption = filterTreatmentOption;
	}

	public ProcessPeaksByAreaFilterSelectionCriterion getFilterSelectionCriterion() {
		return filterSelectionCriterion;
	}

	public void setFilterSelectionCriterion(ProcessPeaksByAreaFilterSelectionCriterion filterSelectionCriterion) {
		this.filterSelectionCriterion = filterSelectionCriterion;
	}
}
