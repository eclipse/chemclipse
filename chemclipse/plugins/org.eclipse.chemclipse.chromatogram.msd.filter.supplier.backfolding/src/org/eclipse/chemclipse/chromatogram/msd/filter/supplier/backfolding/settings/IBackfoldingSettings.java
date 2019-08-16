/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings;

public interface IBackfoldingSettings {

	/**
	 * Returns the number of backfolding runs.
	 * 
	 * @return int
	 */
	int getNumberOfBackfoldingRuns();

	/**
	 * Sets the number of backfolding runs.<br/>
	 * The number must not exceed MIN_BACKFOLDING_RUNS and MAX_BACKFOLDING_RUNS.
	 * 
	 * @param numberOfBackfoldingRuns
	 */
	void setNumberOfBackfoldingRuns(int numberOfBackfoldingRuns);

	/**
	 * Determines how much the retention time for each ion value could be
	 * shifted.
	 * 
	 * @return int
	 */
	int getMaximumRetentionTimeShift();

	/**
	 * Sets how much the retention time for each ion value could be shifted. The
	 * maximumRetentionTimeShift must not exceed MIN_RETENTION_TIME_SHIFT and
	 * MAX_RETENTION_TIME_SHIFT.
	 * 
	 * @return int
	 */
	void setMaximumRetentionTimeShift(int maximumRetentionTimeShift);
}
