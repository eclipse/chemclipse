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
import org.eclipse.chemclipse.xxd.filter.support.ShapeCriterion;
import org.eclipse.chemclipse.xxd.filter.support.TreatmentOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ShapeFilterSettings {

	@JsonProperty(value = "Leading", defaultValue = "0.9")
	@JsonPropertyDescription(value = "The leading value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double leading = 0.9d;
	@JsonProperty(value = "Tailing", defaultValue = "0.9")
	@JsonPropertyDescription(value = "The tailing value of a peak to be filtered accordingly.")
	@DoubleSettingsProperty(minValue = 0.0d, maxValue = 100.0d)
	private double tailing = 0.9d;
	@JsonProperty(value = "Treatment Option")
	private TreatmentOption treatmentOption = TreatmentOption.DEACTIVATE_PEAK;
	@JsonProperty(value = "Shape Criterion")
	private ShapeCriterion shapeCriterion = ShapeCriterion.TAILING_GREATER_THAN_LIMIT;

	public double getLeading() {

		return leading;
	}

	public void setLeading(double leading) {

		this.leading = leading;
	}

	public double getTailing() {

		return tailing;
	}

	public void setTailing(double tailing) {

		this.tailing = tailing;
	}

	public TreatmentOption getTreatmentOption() {

		return treatmentOption;
	}

	public void setTreatmentOption(TreatmentOption treatmentOption) {

		this.treatmentOption = treatmentOption;
	}

	public ShapeCriterion getShapeCriterion() {

		return shapeCriterion;
	}

	public void setShapeCriterion(ShapeCriterion shapeCriterion) {

		this.shapeCriterion = shapeCriterion;
	}
}
