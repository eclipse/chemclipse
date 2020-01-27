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
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByPeakWidthFilterSelectionCriterion;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByValueFilterTreatmentOption;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class PeakWidthFilterSettings {

	@JsonProperty(value = "Width value [min]:")
	@JsonPropertyDescription(value = "The width value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double widthValue = 0.0d;

	@JsonProperty(value = "Peak Treatment Option:")
	@EnumSelectionSettingProperty
	private ProcessPeaksByValueFilterTreatmentOption filterTreatmentOption = ProcessPeaksByValueFilterTreatmentOption.DISABLE_PEAK;

	@JsonProperty(value = "Width Selection Criterion:")
	@EnumSelectionSettingProperty
	private ProcessPeaksByPeakWidthFilterSelectionCriterion filterSelectionCriterion = ProcessPeaksByPeakWidthFilterSelectionCriterion.WIDTH_GREATER_THAN_LIMIT;

	public double getWidthValue() {
		
		return widthValue;
	}

	public void setWidthValue(double widthValue) {
		
		this.widthValue = widthValue;
	}

	public ProcessPeaksByValueFilterTreatmentOption getFilterTreatmentOption() {
		
		return filterTreatmentOption;
	}

	public void setFilterTreatmentOption(ProcessPeaksByValueFilterTreatmentOption filterTreatmentOption) {
		
		this.filterTreatmentOption = filterTreatmentOption;
	}

	public ProcessPeaksByPeakWidthFilterSelectionCriterion getFilterSelectionCriterion() {
		
		return filterSelectionCriterion;
	}

	public void setFilterSelectionCriterion(ProcessPeaksByPeakWidthFilterSelectionCriterion filterSelectionCriterion) {
		
		this.filterSelectionCriterion = filterSelectionCriterion;
	}
}
