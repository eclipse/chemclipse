/*******************************************************************************
 * Copyright (c) 2008, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Type hierarchy
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.core.IChromatogramPeak;

/**
 * This interface describes the most important functions that should be
 * available by an IPeak instance.
 *
 * @author eselmeister
 */
public interface IChromatogramPeakMSD extends IPeakMSD, IChromatogramPeak {

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
	float getPurity();

	/**
	 * Returns the signal to noise ratio of the peak.
	 *
	 * @return float
	 */
	float getSignalToNoiseRatio();

	/**
	 * Returns the chromatogram to which this peak belongs to.
	 *
	 * @return {@link IChromatogramMSD}
	 */
	IChromatogramMSD getChromatogram();

	/**
	 * Returns the genuine, non extracted mass spectrum from the parent
	 * chromatogram.<br/>
	 * See also getExtractedMassSpectrum().
	 *
	 * @return {@link IScanMSD}
	 */
	IScanMSD getChromatogramMassSpectrum();

	// ---------------------------------------------------------------------
	/**
	 * Returns the scan number of the peak maximum.
	 *
	 * @return int
	 */
	int getScanMax();

	/**
	 * Returns the width of the actual peak at its absolute baseline.<br/>
	 * The width is given in scan units.<br/>
	 * The width is not measured at the points of inflection.<br/>
	 * If the peak is out of limits or something has gone wrong, 0 will be
	 * returned.
	 *
	 * @return int
	 */
	int getWidthBaselineTotalInScans();

	/**
	 * Returns the background abundance at the given scan.<br/>
	 * If the given scan is out of peak borders, 0 will be returned.<br/>
	 * The abundance of the background is still 0 based.<br/>
	 *
	 * @param scan
	 * @return float
	 */
	float getBackgroundAbundanceAtScan(int scan);

	/**
	 * Returns the peak abundance at the given scan.<br/>
	 * If the given scan is out of peak borders, 0 will be returned.<br/>
	 * If you would like to present the peak graphically, add the peak abundance
	 * on top of the background abundance.<br/>
	 *
	 * @param scan
	 * @return float
	 */
	float getPeakAbundanceAtScan(int scan);
}
