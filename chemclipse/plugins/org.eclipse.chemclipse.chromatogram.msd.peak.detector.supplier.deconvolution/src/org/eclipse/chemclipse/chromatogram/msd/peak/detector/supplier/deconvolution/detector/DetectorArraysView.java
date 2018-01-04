/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.detector;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArrayView;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;

public class DetectorArraysView implements IDetectorArraysView {

	private List<IArrayView> xValues;
	private List<IArrayView> OriginalChromatogramValues;
	private List<IArrayView> BaselineValues;
	private List<IArrayView> NoiseValues;
	private List<IArrayView> SmoothedValues;
	private List<IArrayView> firstDerivative;
	private List<IArrayView> secondDerivative;
	private List<IArrayView> thirdDerivative;
	private List<IArrayView> peakRanges;
	private List<IArrayView> peakRangesEndPoints;
	private int startScan;
	private int stopScan;

	public DetectorArraysView(ITotalScanSignals signals) {
		stopScan = signals.getStopScan();
		startScan = signals.getStartScan();
		int size = signals.size();
		xValues = new ArrayList<IArrayView>(size);
		OriginalChromatogramValues = new ArrayList<IArrayView>(size);
		BaselineValues = new ArrayList<IArrayView>(size);
		NoiseValues = new ArrayList<IArrayView>(size);
		SmoothedValues = new ArrayList<IArrayView>(size);
		firstDerivative = new ArrayList<IArrayView>(size);
		secondDerivative = new ArrayList<IArrayView>(size);
		thirdDerivative = new ArrayList<IArrayView>(size);
		peakRanges = new ArrayList<IArrayView>(size);
		peakRangesEndPoints = new ArrayList<IArrayView>(size);
	}

	/*
	 * SIZE
	 */
	public int size() {

		return xValues.size();
	}

	public int sizePeakRangesEndPoints() {

		return peakRangesEndPoints.size();
	}

	public int sizePeakRanges() {

		return peakRanges.size();
	}

	public int sizeYScaleOriginalChromatogram() {

		return OriginalChromatogramValues.size();
	}

	public int sizeBaseline() {

		return BaselineValues.size();
	}

	public int sizeNoise() {

		return NoiseValues.size();
	}

	public int sizeSmoothed() {

		return SmoothedValues.size();
	}

	public int sizeFirstDeriv() {

		return firstDerivative.size();
	}

	public int sizeSecondDeriv() {

		return secondDerivative.size();
	}

	public int sizeThirdDeriv() {

		return thirdDerivative.size();
	}

	/*
	 * ADD
	 */
	public void addxValues(IArrayView yScale) {

		xValues.add(yScale);
	}

	public void addPeakRangesEndPoint(IArrayView yScale) {

		peakRangesEndPoints.add(yScale);
	}

	public void addPeakValues(IArrayView yScale) {

		peakRanges.add(yScale);
	}

	public void addOriginalChromatogramValues(IArrayView yScale) {

		OriginalChromatogramValues.add(yScale);
	}

	public void addBaselineValues(IArrayView yScale) {

		BaselineValues.add(yScale);
	}

	public void addNoiseValues(IArrayView yScale) {

		NoiseValues.add(yScale);
	}

	public void addSmoothedValues(IArrayView yScale) {

		SmoothedValues.add(yScale);
	}

	public void addFirstDeriv(IArrayView yScale) {

		firstDerivative.add(yScale);
	}

	public void addSecondDeriv(IArrayView yScale) {

		secondDerivative.add(yScale);
	}

	public void addThirdDeriv(IArrayView yScale) {

		thirdDerivative.add(yScale);
	}

	@Override
	public void add(IArrayView yScale) {

	}

	/*
	 * GET
	 */
	public int getStartScan() {

		return startScan;
	}

	public IArrayView getPeakValues(int value) {

		return peakRanges.get(value);
	}

	public IArrayView getPeakValuesEndPoint(int value) {

		return peakRangesEndPoints.get(value);
	}

	public int getStopScan() {

		return stopScan;
	}

	public IArrayView getxValues(int value) {

		return xValues.get(value);
	}

	public IArrayView getyValuesChromatogram(int value) {

		return OriginalChromatogramValues.get(value);
	}

	public IArrayView getyBaselineValues(int value) {

		return BaselineValues.get(value);
	}

	public IArrayView getyNoiseValues(int value) {

		return NoiseValues.get(value);
	}

	public IArrayView getySmoothedValues(int value) {

		return SmoothedValues.get(value);
	}

	public IArrayView getyFirstDeriv(int value) {

		return firstDerivative.get(value);
	}

	public IArrayView getySecondDeriv(int value) {

		return secondDerivative.get(value);
	}

	public IArrayView getyThirdDeriv(int value) {

		return thirdDerivative.get(value);
	}
}
