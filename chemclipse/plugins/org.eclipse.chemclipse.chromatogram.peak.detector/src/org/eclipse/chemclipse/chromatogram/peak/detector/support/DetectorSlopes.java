/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add constructor
 * Lorenz Gerber - implement additional smooth method
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayFilter;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.numeric.statistics.Calculations;

/**
 * @author Philip Wenig
 */
public class DetectorSlopes implements IDetectorSlopes {

	private List<IDetectorSlope> slopes;
	private int startScan;
	private int stopScan;

	public DetectorSlopes(ITotalScanSignals totalIonSignals) {

		this(totalIonSignals.getStartScan(), totalIonSignals.getStopScan() - 1);
	}

	protected DetectorSlopes(int startScan, int stopScan) {

		this.startScan = startScan;
		this.stopScan = stopScan;
		int amount = stopScan - startScan + 1;
		slopes = new ArrayList<IDetectorSlope>(amount);
	}

	// ----------------------------------------IFirstDerivativeSlopes
	// TODO JUnit
	@Override
	public int getStartScan() {

		return startScan;
	}

	// TODO JUnit
	@Override
	public int getStopScan() {

		return stopScan;
	}

	@Override
	public void add(IDetectorSlope detectorSlope) {

		slopes.add(detectorSlope);
	}

	@Override
	public void calculateMovingAverage(int windowSize) {

		/*
		 * Return if the windowSize is NONE.
		 */
		if(windowSize == 0) {
			return;
		}
		/*
		 * Return if the available number of slopes are lower than the window
		 * size.
		 */
		if(slopes.size() < windowSize) {
			return;
		}
		int diff = windowSize / 2;
		int windowStop = windowSize - diff;
		/*
		 * Moving average calculation.
		 */
		int size = slopes.size() - diff;
		double[] values = new double[windowSize];
		for(int i = diff; i < size; i++) {
			for(int j = -diff, k = 0; j < windowStop; j++, k++) {
				values[k] = slopes.get(i + j).getSlope();
			}
			/*
			 * Set the new slope value.
			 */
			slopes.get(i).setSlope(Calculations.getMean(values));
		}
	}

	public void calculateSavitzkyGolaySmooth(int windowSize) {

		int SAVITZKYGOLAY_DERIVATIVE = 0;
		int SAVITZKYGOLAY_ORDER = 3;
		int filterWidth = windowSize;
		double[] initialSlopes = new double[slopes.size()];
		double[] smoothedSlopes = new double[slopes.size()];
		for(int i = 0; i < slopes.size(); i++)
			initialSlopes[i] = slopes.get(i).getSlope();
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(SAVITZKYGOLAY_ORDER, filterWidth, SAVITZKYGOLAY_DERIVATIVE);
		smoothedSlopes = filter.apply(initialSlopes);
		for(int i = 0; i < slopes.size(); i++)
			slopes.get(i).setSlope(smoothedSlopes[i]);
	}

	@Override
	public IDetectorSlope getDetectorSlope(int scan) {

		if(scan >= startScan && scan <= stopScan) {
			scan -= startScan;
			return slopes.get(scan);
		} else {
			return null;
		}
	}

	@Override
	public int size() {

		return slopes.size();
	}
	// ----------------------------------------IFirstDerivativeSlopes
}
