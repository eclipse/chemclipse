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
import org.eclipse.chemclipse.xxd.model.support.PeakShapeFilterSelectionCriterion;
import org.eclipse.chemclipse.xxd.model.support.ValueFilterTreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ShapeFilterSettings {

	@JsonProperty(value = "Leading:", defaultValue = "0.9")
	@JsonPropertyDescription(value = "The leading value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double leadingValue = 0.9d;

	@JsonProperty(value = "Tailing:", defaultValue = "0.9")
	@JsonPropertyDescription(value = "The tailing value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double tailingValue = 0.9d;

	@JsonProperty(value = "Peak Treatment Option:")
	@EnumSelectionSettingProperty
	private ValueFilterTreatmentOption filterTreatmentOption = ValueFilterTreatmentOption.DEACTIVATE_PEAK;

	@JsonProperty(value = "Peak Selection Criterion:")
	@EnumSelectionSettingProperty
	private PeakShapeFilterSelectionCriterion filterSelectionCriterion = PeakShapeFilterSelectionCriterion.TAILING_GREATER_THAN_LIMIT;

	public double getLeadingValue() {

		return leadingValue;
	}

	public void setLeadingValue(double leadingValue) {

		this.leadingValue = leadingValue;
	}

	public double getTailingValue() {

		return tailingValue;
	}

	public void setTailingValue(double tailingValue) {

		this.tailingValue = tailingValue;
	}

	public ValueFilterTreatmentOption getFilterTreatmentOption() {

		return filterTreatmentOption;
	}

	public void setFilterTreatmentOption(ValueFilterTreatmentOption filterTreatmentOption) {

		this.filterTreatmentOption = filterTreatmentOption;
	}

	public PeakShapeFilterSelectionCriterion getFilterSelectionCriterion() {

		return filterSelectionCriterion;
	}

	public void setFilterSelectionCriterion(PeakShapeFilterSelectionCriterion filterSelectionCriterion) {

		this.filterSelectionCriterion = filterSelectionCriterion;
	}
}