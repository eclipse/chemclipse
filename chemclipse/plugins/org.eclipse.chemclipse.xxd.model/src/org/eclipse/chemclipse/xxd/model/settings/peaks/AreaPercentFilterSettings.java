/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 * Philip Wenig - refactoring ILabel support
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.xxd.model.support.AreaCriterion;
import org.eclipse.chemclipse.xxd.model.support.TreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class AreaPercentFilterSettings {

	@JsonProperty(value = "Minimum Area [%]")
	@JsonPropertyDescription(value = "The minimum percentage area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double minimumPercentageAreaValue = 1.0d;
	@JsonProperty(value = "Maximum Area [%]")
	@JsonPropertyDescription(value = "The maximum percentage area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double maximumPercentageAreaValue = 10.0d;
	@JsonProperty(value = "Treatment Option")
	private TreatmentOption treatmentOption = TreatmentOption.DEACTIVATE_PEAK;
	@JsonProperty(value = "Area Criterion")
	private AreaCriterion areaCriterion = AreaCriterion.AREA_LESS_THAN_MINIMUM;

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

	public TreatmentOption getTreatmentOption() {

		return treatmentOption;
	}

	public void setTreatmentOption(TreatmentOption treatmentOption) {

		this.treatmentOption = treatmentOption;
	}

	public AreaCriterion getAreaCriterion() {

		return areaCriterion;
	}

	public void setAreaCriterion(AreaCriterion areaCriterion) {

		this.areaCriterion = areaCriterion;
	}
}