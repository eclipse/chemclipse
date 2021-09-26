/*******************************************************************************
 * Copyright (c) 2013, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - remove the window size enum
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.settings;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.AbstractBaselineDetectorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.chemclipse.support.settings.serialization.WindowSizeDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class BaselineDetectorSettings extends AbstractBaselineDetectorSettings {

	@JsonProperty(value = "Number of Iterations", defaultValue = "100")
	@JsonPropertyDescription(value = "The number of iterations to apply the SNIP filter.")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_ITERATIONS, maxValue = PreferenceSupplier.MAX_ITERATIONS)
	private int iterations = PreferenceSupplier.DEF_ITERATIONS;
	@JsonProperty(value = "Window Size", defaultValue = "5")
	@JsonPropertyDescription(value = "Window Size: 3, 5, 7, ..., 45")
	@JsonDeserialize(using = WindowSizeDeserializer.class)
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_WINDOW_SIZE, maxValue = PreferenceSupplier.MAX_WINDOW_SIZE, validation = Validation.ODD_NUMBER_INCLUDING_ZERO)
	private int windowSize = PreferenceSupplier.DEF_WINDOW_SIZE;

	public int getWindowSize() {

		return windowSize;
	}

	public void setWindowSize(int windowSize) {

		this.windowSize = windowSize;
	}

	public int getIterations() {

		return iterations;
	}

	public void setIterations(int iterations) {

		this.iterations = iterations;
	}
}
