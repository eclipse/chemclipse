/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core;

import java.util.List;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;

public interface IChromatogramPeaksCSD {

	/**
	 * Returns a list of the stored peaks.
	 * 
	 * @return List<IPeak>
	 */
	List<IChromatogramPeakCSD> getPeaks();

	/**
	 * Returns the peaks only given by the borders of the chromatogram
	 * selection.
	 * 
	 * @param chromatogramSelection
	 * @return List<IPeak>
	 */
	List<IChromatogramPeakCSD> getPeaks(IChromatogramSelectionCSD chromatogramSelection);

	/**
	 * Adds a peak to the actually stored peak list.
	 * 
	 * @param peak
	 */
	void addPeak(IChromatogramPeakCSD peak);

	/**
	 * Removes the peak from the actually stored peaks.
	 * 
	 * @param peak
	 */
	void removePeak(IChromatogramPeakCSD peak);

	/**
	 * Removes the peak list from the actually stored peaks.
	 * 
	 * @param peaks
	 */
	void removePeaks(List<IChromatogramPeakCSD> peaksToDelete);

	/**
	 * Removes all peaks.
	 */
	void removeAllPeaks();

	/**
	 * Returns the number of peaks.
	 * 
	 * @return int
	 */
	int getNumberOfPeaks();

	/**
	 * Returns the peak within the given retention time.
	 * This method may return null.
	 * 
	 * @param retentionTime
	 * @return {@link IChromatogramPeakCSD}
	 */
	IChromatogramPeakCSD getPeak(int retentionTime);
}
