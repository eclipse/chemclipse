/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PeakDetectorSettings extends AbstractPeakDetectorSettingsMSD {

	@JsonIgnore
	private Sensitivity sensitivity = Sensitivity.MEDIUM;
	@JsonProperty(value = "Min S/N Ratio", defaultValue = "0")
	@FloatSettingsProperty(minValue = 0f, maxValue = Float.MAX_VALUE)
	private float minimumSignalToNoiseRatio;
	private int minPeakRising; // between 1,4
	private int minimalPeakWidth;
	private int baselineIterations;
	private int quantityNoiseSegments;
	private int sensitivityOfDeconvolution;

	public Sensitivity getSensitivity() {

		return sensitivity;
	}

	public void setSensitivity(Sensitivity sensitivity) {

		if(sensitivity != null) {
			this.sensitivity = sensitivity;
		}
	}

	public void setMinimumPeakRising(int minPeakRising) {

		this.minPeakRising = minPeakRising;
	}

	public void setMinimumPeakWidth(int minimalPeakWidth) {

		this.minimalPeakWidth = minimalPeakWidth;
	}

	public void setBaselineIterations(int baselineIterations) {

		this.baselineIterations = baselineIterations;
	}

	public void setQuantityNoiseSegments(int quantityNoiseSegments) {

		this.quantityNoiseSegments = quantityNoiseSegments;
	}

	public void setSensitivityOfDeconvolution(int sensitivityDeconvolution) {

		this.sensitivityOfDeconvolution = sensitivityDeconvolution;
	}

	public int getMinimumPeakRising() {

		return minPeakRising;
	}

	public int getMinimumPeakWidth() {

		return minimalPeakWidth;
	}

	public int getBaselineIterations() {

		return baselineIterations;
	}

	public int getQuantityNoiseSegments() {

		return quantityNoiseSegments;
	}

	public int getSensitivityOfDeconvolution() {

		return sensitivityOfDeconvolution;
	}

	public float getMinimumSignalToNoiseRatio() {

		return minimumSignalToNoiseRatio;
	}

	public void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio) {

		this.minimumSignalToNoiseRatio = minimumSignalToNoiseRatio;
	}
}
