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
package org.eclipse.chemclipse.msd.model.core;

import java.util.List;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public interface IChromatogramPeaksMSD {

	/**
	 * Returns a list of the stored peaks.
	 * 
	 * @return List<IPeak>
	 */
	List<IChromatogramPeakMSD> getPeaks();

	/**
	 * Returns the peaks only given by the borders of the chromatogram
	 * selection.
	 * 
	 * @param chromatogramSelection
	 * @return List<IPeak>
	 */
	List<IChromatogramPeakMSD> getPeaks(IChromatogramSelectionMSD chromatogramSelection);

	/**
	 * Adds a peak to the actually stored peak list.
	 * 
	 * @param peak
	 */
	void addPeak(IChromatogramPeakMSD peak);

	/**
	 * Removes the peak from the actually stored peaks.
	 * 
	 * @param peak
	 */
	void removePeak(IChromatogramPeakMSD peak);

	/**
	 * Removes the peak list from the actually stored peaks.
	 * 
	 * @param peaks
	 */
	void removePeaks(List<IChromatogramPeakMSD> peaksToDelete);

	/**
	 * This method may return null.
	 * 
	 * @param retentionTime
	 * @return {@link IChromatogramPeakMSD}
	 */
	IChromatogramPeakMSD getPeak(int retentionTime);
}
