/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Kerner - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

public interface IChromatogramPeak extends IPeak {

	/**
	 * Returns the signal to noise ratio of the peak.
	 *
	 * @return float
	 */
	float getSignalToNoiseRatio();

	/**
	 * Returns the scan number of the peak maximum.
	 *
	 * @return int
	 */
	int getScanMax();

	/**
	 * Returns the purity of the peak in comparison to the recorded scan at peak
	 * maximum.<br/>
	 * E.g.: If a scan has 30 ions and the corresponding peak at the
	 * scan has only 15 of the ions recorded at the scan, the purity
	 * would be 0.5 (50%).<br/>
	 * The value that will be returned is in a range of 0 (0%) - 1 (100%).
	 *
	 * @return float
	 */
	default float getPurity() {

		return 1.0f;
	}
}
