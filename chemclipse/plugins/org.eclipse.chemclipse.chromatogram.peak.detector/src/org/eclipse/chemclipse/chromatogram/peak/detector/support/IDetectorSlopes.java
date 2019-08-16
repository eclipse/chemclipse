/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.support;

import org.eclipse.chemclipse.numeric.statistics.WindowSize;

/**
 * @author eselmeister
 */
public interface IDetectorSlopes {

	/**
	 * Adds a {@link DetectorSlope} object.
	 * 
	 * @param detectorSlope
	 */
	void add(IDetectorSlope detectorSlope);

	/**
	 * Returns a DetectorSlope object or null, if no object is available.
	 * 
	 * @param scan
	 * @return IDetectorSlope
	 */
	IDetectorSlope getDetectorSlope(int scan);

	/**
	 * Calculates for each stored slope value a smoothed moving average value.<br/>
	 * The window size declares the width of the moving window.
	 * 
	 * @param windowSize
	 */
	void calculateMovingAverage(WindowSize windowSize);

	/**
	 * Returns the size of the slope list.
	 * 
	 * @return int
	 */
	int size();

	/**
	 * Returns the start scan.
	 * 
	 * @return
	 */
	int getStartScan();

	/**
	 * Returns the stop scan.
	 * 
	 * @return
	 */
	int getStopScan();
}
