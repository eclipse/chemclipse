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
import org.eclipse.chemclipse.xxd.model.support.AreaSelection;
import org.eclipse.chemclipse.xxd.model.support.TreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class AreaFilterSettings {

	@JsonProperty(value = "Minimum area:")
	@JsonPropertyDescription(value = "The minimum area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 1.0d, maxValue = Double.MAX_VALUE)
	private double minimumAreaValue = 1.0d;

	@JsonProperty(value = "Maximum area:")
	@JsonPropertyDescription(value = "The maximum area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 1.0d, maxValue = Double.MAX_VALUE)
	private double maximumAreaValue = 1.0d;

	@JsonProperty(value = "Peak Treatment Option:")
	@EnumSelectionSettingProperty
	private TreatmentOption filterTreatmentOption = TreatmentOption.DEACTIVATE_PEAK;

	@JsonProperty(value = "Peak Selection Criterion:")
	@EnumSelectionSettingProperty
	private AreaSelection filterSelectionCriterion = AreaSelection.AREA_LESS_THAN_MINIMUM;

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

	public TreatmentOption getFilterTreatmentOption() {
		
		return filterTreatmentOption;
	}

	public void setFilterTreatmentOption(TreatmentOption filterTreatmentOption) {
		
		this.filterTreatmentOption = filterTreatmentOption;
	}

	public AreaSelection getFilterSelectionCriterion() {
		
		return filterSelectionCriterion;
	}

	public void setFilterSelectionCriterion(AreaSelection filterSelectionCriterion) {
		
		this.filterSelectionCriterion = filterSelectionCriterion;
	}
}
