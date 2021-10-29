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
import org.eclipse.chemclipse.xxd.model.support.TreatmentOption;
import org.eclipse.chemclipse.xxd.model.support.WidthCriterion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class WidthFilterSettings {

	@JsonProperty(value = "Width [min]")
	@JsonPropertyDescription(value = "The width value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double width = 0.0d;
	@JsonProperty(value = "Treatment Option")
	private TreatmentOption treatmentOption = TreatmentOption.DEACTIVATE_PEAK;
	@JsonProperty(value = "Width Criterion")
	private WidthCriterion widthCriterion = WidthCriterion.WIDTH_GREATER_THAN_LIMIT;

	public double getWidth() {

		return width;
	}

	public void setWidth(double width) {

		this.width = width;
	}

	public TreatmentOption getTreatmentOption() {

		return treatmentOption;
	}

	public void setTreatmentOption(TreatmentOption treatmentOption) {

		this.treatmentOption = treatmentOption;
	}

	public WidthCriterion getWidthCriterion() {

		return widthCriterion;
	}

	public void setWidthCriterion(WidthCriterion widthCriterion) {

		this.widthCriterion = widthCriterion;
	}
}