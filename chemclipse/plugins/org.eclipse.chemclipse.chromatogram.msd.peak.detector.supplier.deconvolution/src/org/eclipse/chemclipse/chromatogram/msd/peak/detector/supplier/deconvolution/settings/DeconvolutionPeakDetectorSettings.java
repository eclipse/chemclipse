/*******************************************************************************
 * Copyright (c) 2013, 2016
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

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorMSDSettings;

public class DeconvolutionPeakDetectorSettings extends AbstractPeakDetectorMSDSettings implements IDeconvolutionPeakDetectorSettings {

	private Sensitivity sensitivity = INITIAL_SENSITIVITY;
	private double minimumSignalToNoiseRatio;
	private int minPeakRising; // between 1,4
	private int minimalPeakWidth;
	private int baselineIterations;
	private int quantityNoiseSegments;

	@Override
	public Sensitivity getSensitivity() {

		return sensitivity;
	}

	@Override
	public void setSensitivity(Sensitivity sensitivity) {

		if(sensitivity != null) {
			this.sensitivity = sensitivity;
		}
	}

	public void setMinimumSignalToNoiseRatio(double minimumSignalToNoiseRatio) {

		this.minimumSignalToNoiseRatio = minimumSignalToNoiseRatio;
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

	/*
	 * Getter
	 */
	public double getMinimumSignalToNoiseRatio() {

		return minimumSignalToNoiseRatio;
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
}
