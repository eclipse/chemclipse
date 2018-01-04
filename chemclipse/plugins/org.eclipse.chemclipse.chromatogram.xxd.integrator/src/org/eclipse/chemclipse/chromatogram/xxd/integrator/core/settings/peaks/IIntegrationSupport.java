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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;

public interface IIntegrationSupport extends IReportDecider {

	int INITIAL_PEAK_WIDTH = 0;
	float MIN_SIGNAL_TO_NOISE_RATIO = 0.0f;

	/**
	 * The integrator will be disabled between start and stop retention time.<br/>
	 * The {@link IPeakIntegrationResult} will not contain a integration result
	 * within the borders of the disabled integrator.<br/>
	 * But the peak itself will be still integrated.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 */
	void setIntegratorOff(int startRetentionTime, int stopRetentionTime);

	/**
	 * Checks if the integrator is off at the given retention time.
	 * 
	 * @param startRetentionTime
	 * @return boolean
	 */
	boolean isIntegratorOff(int startRetentionTime);

	/**
	 * Sets all settings to the default.
	 */
	void reset();

	/**
	 * Clears all disables integrator time zones.
	 */
	void resetIntegratorOff();

	/**
	 * Sets the minimum peak width of a peak to be integrated.<br/>
	 * The width is stored in milliseconds.
	 * 
	 * @param minimumPeakWidth
	 */
	void setMinimumPeakWidth(int minimumPeakWidth);

	/**
	 * Returns the minimum peak width.
	 * 
	 * @return int
	 */
	int getMinimumPeakWidth();

	/**
	 * Sets the minimal signal to noise ratio of a peak to be integrated.
	 * 
	 * @param minimumSignalToNoiseRatio
	 */
	void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio);

	/**
	 * Returns the minimal signal to noise ratio that peaks should have to be integrated.
	 * 
	 * @return float
	 */
	float getMinimumSignalToNoiseRatio();
}
