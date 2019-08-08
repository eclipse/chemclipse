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

import org.eclipse.chemclipse.model.core.PeakPosition;
import org.eclipse.chemclipse.model.core.PeakType;

public interface IRawPeak extends PeakPosition {

	/**
	 * Returns the start scan of the raw peak.
	 * 
	 * @return int
	 */
	int getStartScan();

	/**
	 * Returns the peak maximum.
	 * 
	 * @return int
	 */
	int getMaximumScan();

	/**
	 * Returns the retention time at peak maximum.
	 * 
	 * @return int
	 */
	int getRetentionTimeAtMaximum();

	/**
	 * Sets the retention time at peak maximum.<br/>
	 * The retention time must be >= 0.
	 * 
	 * @param retentionTime
	 */
	void setRetentionTimeAtMaximum(int retentionTime);

	/**
	 * Returns the stop scan of the peak.
	 * 
	 * @return int
	 */
	int getStopScan();

	@Override
	default PeakType getPeakType() {

		return PeakType.DEFAULT;
	}

	@Override
	default int getPeakStart() {

		return getStartScan() - 1;
	}

	@Override
	default int getPeakMaximum() {

		return getMaximumScan() - 1;
	}

	@Override
	default int getPeakEnd() {

		return getStopScan() - 1;
	}
}
