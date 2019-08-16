/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class PeakFilterSettings extends AbstractPeakFilterSettings {

	@JsonProperty(value = "Iterations", defaultValue = "1")
	@JsonPropertyDescription(value = "The number of iterations to run the filter.")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_ITERATIONS, maxValue = PreferenceSupplier.MAX_ITERATIONS)
	private int iterations = 100;
	@JsonProperty(value = "Magnification Factor", defaultValue = "1.0")
	@JsonPropertyDescription(value = "The magnification factor run the filter.")
	@DoubleSettingsProperty(minValue = PreferenceSupplier.MIN_MAGNIFICATION_FACTOR, maxValue = PreferenceSupplier.MAX_MAGNIFICATION_FACTOR)
	private double magnificationFactor = 1.0d;
	@JsonProperty(value = "Transitions", defaultValue = "1")
	@JsonPropertyDescription(value = "The number of transitions run the filter.")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_TRANSITIONS, maxValue = PreferenceSupplier.MAX_TRANSITIONS)
	private int transitions = 1;

	public int getIterations() {

		return iterations;
	}

	public void setIterations(int iterations) {

		this.iterations = iterations;
	}

	public double getMagnificationFactor() {

		return magnificationFactor;
	}

	public void setMagnificationFactor(double magnificationFactor) {

		this.magnificationFactor = magnificationFactor;
	}

	public int getTransitions() {

		return transitions;
	}

	public void setTransitions(int transitions) {

		this.transitions = transitions;
	}
}
