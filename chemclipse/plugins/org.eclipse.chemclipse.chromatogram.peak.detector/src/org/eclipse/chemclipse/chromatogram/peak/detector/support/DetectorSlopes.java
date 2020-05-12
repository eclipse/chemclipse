/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add constructor
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

/**
 * @author eselmeister
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
	public void calculateMovingAverage(WindowSize windowSize) {

		/*
		 * Return if the windowSize is null or NONE.
		 */
		if(windowSize == null || WindowSize.NONE.equals(windowSize)) {
			return;
		}
		/*
		 * Return if the available number of slopes are lower than the window
		 * size.
		 */
		if(slopes.size() < windowSize.getSize()) {
			return;
		}
		int diff = windowSize.getSize() / 2;
		int windowStop = windowSize.getSize() - diff;
		/*
		 * Moving average calculation.
		 */
		int size = slopes.size() - diff;
		double[] values = new double[windowSize.getSize()];
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
