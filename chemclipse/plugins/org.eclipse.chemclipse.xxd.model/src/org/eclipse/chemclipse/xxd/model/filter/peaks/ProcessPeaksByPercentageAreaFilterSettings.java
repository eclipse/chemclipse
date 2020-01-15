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
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterSelectionCriterion;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterTreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ProcessPeaksByPercentageAreaFilterSettings {

	@JsonProperty(value = "Minimum area [%]:", defaultValue = "1.0")
	@JsonPropertyDescription(value = "The minimum percentage area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double minimumPercentageAreaValue = 1.0d;

	@JsonProperty(value = "Maximum area [%]:", defaultValue = "10.0")
	@JsonPropertyDescription(value = "The maximum percentage area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double maximumPercentageAreaValue = 10.0d;

	@JsonProperty(value = "Peak Treatment Option:")
	@EnumSelectionSettingProperty
	private ProcessPeaksByAreaFilterTreatmentOption filterTreatmentOption = ProcessPeaksByAreaFilterTreatmentOption.DISABLE_PEAK;

	@JsonProperty(value = "Peak Selection Criterion:")
	@EnumSelectionSettingProperty
	private ProcessPeaksByAreaFilterSelectionCriterion filterSelectionCriterion = ProcessPeaksByAreaFilterSelectionCriterion.AREA_LESS_THAN_MINIMUM;

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
