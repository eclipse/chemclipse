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
import org.eclipse.chemclipse.xxd.model.support.PeakAsymmetryFilterSelectionCriterion;
import org.eclipse.chemclipse.xxd.model.support.ValueFilterTreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class AsymmetryFilterSettings {

	@JsonProperty(value = "As:", defaultValue = "1.02")
	@JsonPropertyDescription(value = "The peak asymmetry factor of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 10.0d)
	// peak asymmetry factor (As) rating — As = 1.0-1.05 [excellent] - As = 1.2 [acceptable] - As >= 2 [unacceptable]
	private double  peakAsymmetryFactor = 1.02d;

	@JsonProperty(value = "Peak Treatment Option:")
	@EnumSelectionSettingProperty
	private ValueFilterTreatmentOption filterTreatmentOption = ValueFilterTreatmentOption.DEACTIVATE_PEAK;

	@JsonProperty(value = "Peak Selection Criterion:")
	@EnumSelectionSettingProperty
	private PeakAsymmetryFilterSelectionCriterion filterSelectionCriterion = PeakAsymmetryFilterSelectionCriterion.ASYMMETRY_FACTOR_GREATER_THAN_LIMIT;

	public double getPeakAsymmetryFactor() {
		return peakAsymmetryFactor;
	}

	public void setPeakAsymmetryFactor(double peakAsymmetryFactor) {

		this.peakAsymmetryFactor = peakAsymmetryFactor;
	}

	public ValueFilterTreatmentOption getFilterTreatmentOption() {

		return filterTreatmentOption;
	}

	public void setFilterTreatmentOption(ValueFilterTreatmentOption filterTreatmentOption) {

		this.filterTreatmentOption = filterTreatmentOption;
	}

	public PeakAsymmetryFilterSelectionCriterion getFilterSelectionCriterion() {

		return filterSelectionCriterion;
	}

	public void setFilterSelectionCriterion(PeakAsymmetryFilterSelectionCriterion filterSelectionCriterion) {

		this.filterSelectionCriterion = filterSelectionCriterion;
	}
}
