/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.detector;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArrayView;

public interface IDetectorArraysView {

	void add(IArrayView yScale);

	/*
	 * Size
	 */
	int size();

	int sizePeakRanges();

	int sizePeakRangesEndPoints();

	int sizeYScaleOriginalChromatogram();

	int sizeBaseline();

	int sizeNoise();

	int sizeSmoothed();

	int sizeFirstDeriv();

	int sizeSecondDeriv();

	int sizeThirdDeriv();

	/*
	 * Add
	 */
	void addxValues(IArrayView yScale);

	void addPeakValues(IArrayView yScale);

	void addPeakRangesEndPoint(IArrayView yScale);

	void addOriginalChromatogramValues(IArrayView yScale);

	void addBaselineValues(IArrayView yScale);

	void addNoiseValues(IArrayView yScale);

	void addSmoothedValues(IArrayView yScale);

	void addFirstDeriv(IArrayView yScale);

	void addSecondDeriv(IArrayView yScale);

	void addThirdDeriv(IArrayView yScale);

	/*
	 * Get
	 */
	int getStartScan();

	int getStopScan();

	IArrayView getPeakValues(int value);

	IArrayView getPeakValuesEndPoint(int value);

	IArrayView getxValues(int value);

	IArrayView getyValuesChromatogram(int value);

	IArrayView getyBaselineValues(int value);

	IArrayView getyNoiseValues(int value);

	IArrayView getySmoothedValues(int value);

	IArrayView getyFirstDeriv(int value);

	IArrayView getySecondDeriv(int value);

	IArrayView getyThirdDeriv(int value);
}
