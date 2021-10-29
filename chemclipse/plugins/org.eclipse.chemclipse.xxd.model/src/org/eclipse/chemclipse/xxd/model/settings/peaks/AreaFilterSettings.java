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

public class AreaFilterSettings {

	@JsonProperty(value = "Minimum Area")
	@JsonPropertyDescription(value = "The minimum area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 1.0d, maxValue = Double.MAX_VALUE)
	private double minimumAreaValue = 1.0d;
	@JsonProperty(value = "Maximum Area")
	@JsonPropertyDescription(value = "The maximum area value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 1.0d, maxValue = Double.MAX_VALUE)
	private double maximumAreaValue = 1.0d;
	@JsonProperty(value = "Treatment Option")
	private TreatmentOption treatmentOption = TreatmentOption.DEACTIVATE_PEAK;
	@JsonProperty(value = "Selection Criterion")
	private AreaCriterion areaCriterion = AreaCriterion.AREA_LESS_THAN_MINIMUM;

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