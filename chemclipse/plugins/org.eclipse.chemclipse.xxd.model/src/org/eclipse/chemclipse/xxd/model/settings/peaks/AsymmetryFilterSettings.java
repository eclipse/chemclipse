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
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.xxd.model.support.AsymmetrySelection;
import org.eclipse.chemclipse.xxd.model.support.TreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class AsymmetryFilterSettings {

	@JsonProperty(value = "As:", defaultValue = "1.02")
	@JsonPropertyDescription(value = "The peak asymmetry factor of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 10.0d)
	// peak asymmetry factor (As) rating � As = 1.0-1.05 [excellent] - As = 1.2 [acceptable] - As >= 2 [unacceptable]
	private double peakAsymmetryFactor = 1.02d;
	@JsonProperty(value = "Peak Treatment Option:")
	private TreatmentOption filterTreatmentOption = TreatmentOption.DEACTIVATE_PEAK;
	@JsonProperty(value = "Peak Selection Criterion:")
	private AsymmetrySelection filterSelectionCriterion = AsymmetrySelection.ASYMMETRY_FACTOR_GREATER_THAN_LIMIT;

	public double getPeakAsymmetryFactor() {

		return peakAsymmetryFactor;
	}

	public void setPeakAsymmetryFactor(double peakAsymmetryFactor) {

		this.peakAsymmetryFactor = peakAsymmetryFactor;
	}

	public TreatmentOption getFilterTreatmentOption() {

		return filterTreatmentOption;
	}

	public void setFilterTreatmentOption(TreatmentOption filterTreatmentOption) {

		this.filterTreatmentOption = filterTreatmentOption;
	}

	public AsymmetrySelection getFilterSelectionCriterion() {

		return filterSelectionCriterion;
	}

	public void setFilterSelectionCriterion(AsymmetrySelection filterSelectionCriterion) {

		this.filterSelectionCriterion = filterSelectionCriterion;
	}
}
