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

public interface IAreaSupport extends IReportDecider {

	double INITIAL_AREA_REJECT = 0.0d;

	/**
	 * Takes the sum of the area within the start and stop retention time.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 */
	void setAreaSumOn(int startRetentionTime, int stopRetentionTime);

	/**
	 * Returns true, if the area should be summed for the given retention time.
	 * 
	 * @param startRetentionTime
	 * @return boolean
	 */
	boolean isAreaSumOn(int startRetentionTime);

	/**
	 * Rejects peak areas which are lower than the given minimum area.<br/>
	 * The area must be at least greater or equal than 0.
	 * 
	 * @param minimumArea
	 */
	void setMinimumArea(double minimumArea);

	/**
	 * Returns the minimum peak area, that peaks must have to be reported.
	 * 
	 * @return float
	 */
	double getMinimumArea();

	/**
	 * Sets all settings to the default.
	 */
	void reset();

	/**
	 * Resets all retention time parts which should be summed on.
	 */
	void resetAreaSumOn();
}
