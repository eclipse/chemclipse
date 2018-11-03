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

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;

public interface IDeconvolutionPeakDetectorSettings extends IPeakDetectorSettingsMSD {

	Sensitivity INITIAL_SENSITIVITY = Sensitivity.MEDIUM;

	void setSensitivity(Sensitivity sensitivity);

	void setMinimumSignalToNoiseRatio(double minimumSignalToNoiseRatio);

	void setMinimumPeakRising(int minPeakRising);

	void setMinimumPeakWidth(int minimalPeakWidth);

	void setBaselineIterations(int baselineIterations);

	void setQuantityNoiseSegments(int quantityNoiseSegments);

	void setSensitivityOfDeconvolution(int sensitivityDeconvolution);

	/*
	 * Getter
	 */
	Sensitivity getSensitivity();

	double getMinimumSignalToNoiseRatio();

	int getMinimumPeakRising();

	int getMinimumPeakWidth();

	int getBaselineIterations();

	int getQuantityNoiseSegments();

	int getSensitivityOfDeconvolution();
}
