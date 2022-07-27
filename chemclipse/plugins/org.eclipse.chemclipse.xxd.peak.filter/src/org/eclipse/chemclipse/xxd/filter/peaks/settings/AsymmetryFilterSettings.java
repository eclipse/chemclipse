/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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
package org.eclipse.chemclipse.xxd.filter.peaks.settings;

import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.xxd.filter.support.AsymmetryCriterion;
import org.eclipse.chemclipse.xxd.filter.support.TreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class AsymmetryFilterSettings {

	@JsonProperty(value = "Assymetry Factor", defaultValue = "1.02")
	@JsonPropertyDescription(value = "Factor: 1.0-1.05 [excellent], 1.05 - 1.99 [acceptable], >= 2 [unacceptable]")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 10.0d)
	private double asymmetryFactor = 1.02d;
	@JsonProperty(value = "Treatment Option")
	private TreatmentOption filterTreatmentOption = TreatmentOption.DEACTIVATE_PEAK;
	@JsonProperty(value = "Asymmetry Selection")
	private AsymmetryCriterion asymmetryCriterion = AsymmetryCriterion.ASYMMETRY_FACTOR_GREATER_THAN_LIMIT;

	public double getAsymmetryFactor() {

		return asymmetryFactor;
	}

	public void setAsymmetryFactor(double asymmetryFactor) {

		this.asymmetryFactor = asymmetryFactor;
	}

	public TreatmentOption getFilterTreatmentOption() {

		return filterTreatmentOption;
	}

	public void setFilterTreatmentOption(TreatmentOption filterTreatmentOption) {

		this.filterTreatmentOption = filterTreatmentOption;
	}

	public AsymmetryCriterion getAsymmetryCriterion() {

		return asymmetryCriterion;
	}

	public void setAsymmetryCriterion(AsymmetryCriterion asymmetryCriterion) {

		this.asymmetryCriterion = asymmetryCriterion;
	}
}
