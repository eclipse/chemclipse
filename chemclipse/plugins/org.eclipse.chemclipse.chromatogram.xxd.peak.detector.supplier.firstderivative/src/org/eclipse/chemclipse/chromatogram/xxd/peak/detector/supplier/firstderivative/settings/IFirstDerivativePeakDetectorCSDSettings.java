/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorCSDSettings;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

public interface IFirstDerivativePeakDetectorCSDSettings extends IPeakDetectorCSDSettings {

	/**
	 * Return the threshold.
	 * 
	 * @return {@link Threshold}
	 */
	Threshold getThreshold();

	/**
	 * Set the threshold.
	 * 
	 * @param threshold
	 */
	void setThreshold(Threshold threshold);

	/*
	 * Initial Area Reject Initial Peak Width Shoulder Detection Area Reject
	 * Baseline All Valleys On Baseline All Valleys Off Tangent Skim
	 * RTE Data Point Sampling Smoothing, DetectionFiltering (Scans 5,7,9)
	 * StartThreshold, StopThreshold Peak Location (Top, Centroid) Baseline
	 * reset, Baseline drop else tangent, Tangent drop else baseline
	 */
	/**
	 * If false, VB or BV will be calculated.
	 * If true, VV will be used.
	 * 
	 * @return
	 */
	boolean isIncludeBackground();

	/**
	 * Set the include background parameter.
	 * 
	 * @param includeBackground
	 */
	void setIncludeBackground(boolean includeBackground);

	/**
	 * Peaks only >= the min S/N value will be added.
	 * 
	 * @return int
	 */
	float getMinimumSignalToNoiseRatio();

	/**
	 * Set the in S/N value for adding peaks.
	 * 
	 * @param minimumSignalToNoiseRatio
	 */
	void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio);

	WindowSize getMovingAverageWindowSize();

	void setMovingAverageWindowSize(WindowSize windowSize);
}
