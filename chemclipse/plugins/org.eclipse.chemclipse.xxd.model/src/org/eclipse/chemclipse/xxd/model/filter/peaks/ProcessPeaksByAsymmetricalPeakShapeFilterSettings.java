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
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAsymmetricalPeakShapeFilterSelectionCriterion;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAsymmetricalPeakShapeFilterTreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ProcessPeaksByAsymmetricalPeakShapeFilterSettings {

	@JsonProperty(value = "Leading value:", defaultValue = "0.9")
	@JsonPropertyDescription(value = "The leading value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double leadingValue = 0.9d;

	@JsonProperty(value = "Tailing value:", defaultValue = "0.9")
	@JsonPropertyDescription(value = "The tailing value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double tailingValue = 0.9d;

	@JsonProperty(value = "Peak asymmetry factor:", defaultValue = "1.02")
	@JsonPropertyDescription(value = "The peak asymmetry factor of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 10.0d)
	// peak asymmetry factor (As) rating — As = 1.0-1.05 [excellent] - As = 1.2 [acceptable] - As >= 2 [unacceptable]
	private double  peakAsymmetryFactor = 1.02d;

	@JsonProperty(value = "Peak Treatment Option:")
	@EnumSelectionSettingProperty
	private ProcessPeaksByAsymmetricalPeakShapeFilterTreatmentOption filterTreatmentOption = ProcessPeaksByAsymmetricalPeakShapeFilterTreatmentOption.DISABLE_PEAK;

	@JsonProperty(value = "Peak Selection Criterion:")
	@EnumSelectionSettingProperty
	private ProcessPeaksByAsymmetricalPeakShapeFilterSelectionCriterion filterSelectionCriterion = ProcessPeaksByAsymmetricalPeakShapeFilterSelectionCriterion.TAILING_GREATER_THAN_LIMIT;

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

	public double getPeakAsymmetryFactor() {
		return peakAsymmetryFactor;
	}

	public void setPeakAsymmetryFactor(double peakAsymmetryFactor) {
		
		this.peakAsymmetryFactor = peakAsymmetryFactor;
	}

	public ProcessPeaksByAsymmetricalPeakShapeFilterTreatmentOption getFilterTreatmentOption() {
		
		return filterTreatmentOption;
	}

	public void setFilterTreatmentOption(ProcessPeaksByAsymmetricalPeakShapeFilterTreatmentOption filterTreatmentOption) {
		
		this.filterTreatmentOption = filterTreatmentOption;
	}

	public ProcessPeaksByAsymmetricalPeakShapeFilterSelectionCriterion getFilterSelectionCriterion() {
		
		return filterSelectionCriterion;
	}

	public void setFilterSelectionCriterion(ProcessPeaksByAsymmetricalPeakShapeFilterSelectionCriterion filterSelectionCriterion) {
		
		this.filterSelectionCriterion = filterSelectionCriterion;
	}
}