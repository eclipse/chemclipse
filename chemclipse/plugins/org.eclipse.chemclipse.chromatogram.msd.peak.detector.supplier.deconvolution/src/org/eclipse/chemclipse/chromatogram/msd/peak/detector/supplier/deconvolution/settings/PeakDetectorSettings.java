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

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PeakDetectorSettings extends AbstractPeakDetectorSettingsMSD {

	@JsonIgnore
	private Sensitivity sensitivity = Sensitivity.MEDIUM;
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
}
