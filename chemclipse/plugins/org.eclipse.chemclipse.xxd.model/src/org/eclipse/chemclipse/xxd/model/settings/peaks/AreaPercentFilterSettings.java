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
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.EnumSelectionSettingProperty;
import org.eclipse.chemclipse.xxd.model.support.AreaSelection;
import org.eclipse.chemclipse.xxd.model.support.TreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class AreaPercentFilterSettings {

	@JsonProperty(value = "Minimum area:")
	@JsonPropertyDescription(value = "The minimum percentage area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double minimumPercentageAreaValue = 1.0d;
	@JsonProperty(value = "Maximum area:")
	@JsonPropertyDescription(value = "The maximum percentage area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double maximumPercentageAreaValue = 10.0d;
	@JsonProperty(value = "Peak Treatment Option:")
	@EnumSelectionSettingProperty
	private TreatmentOption filterTreatmentOption = TreatmentOption.DEACTIVATE_PEAK;
	@JsonProperty(value = "Peak Selection Criterion:")
	@EnumSelectionSettingProperty
	private AreaSelection filterSelectionCriterion = AreaSelection.AREA_LESS_THAN_MINIMUM;

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
