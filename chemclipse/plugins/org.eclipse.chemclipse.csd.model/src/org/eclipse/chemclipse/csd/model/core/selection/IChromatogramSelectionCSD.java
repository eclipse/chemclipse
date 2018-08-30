/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.selection;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

@SuppressWarnings("rawtypes")
public interface IChromatogramSelectionCSD extends IChromatogramSelection {

	/**
	 * Returns the stored chromatogram.
	 * May return null.
	 * 
	 * @return {@link IChromatogramCSD}
	 */
	IChromatogramCSD getChromatogramCSD();

	/**
	 * Returns the selected scan of the current chromatogram or null, if none is
	 * stored.
	 * 
	 * @return {@link IScanCSD}
	 */
	IScanCSD getSelectedScan();

	/**
	 * Sets the selected scan of the current chromatogram.<br/>
	 * The scan must not be null.
	 */
	void setSelectedScan(IScanCSD selectedScan);

	/**
	 * Use this convenient method, if you don't want to fire and update.
	 * 
	 * @param selectedScan
	 * @param update
	 */
	void setSelectedScan(IScanCSD selectedScan, boolean update);

	/**
	 * Returns the selected peak of the current chromatogram or null, if none is
	 * stored.
	 * 
	 * @return {@link IChromatogramPeakCSD}
	 */
	IChromatogramPeakCSD getSelectedPeak();

	/**
	 * Sets the selected peak of the current chromatogram.<br/>
	 * The peak must not be null.
	 */
	void setSelectedPeak(IChromatogramPeakCSD selectedPeak);

	/**
	 * Use this convenient method, if you don't want to fire and update.
	 * 
	 * @param selectedScan
	 * @param update
	 */
	void setSelectedPeak(IChromatogramPeakCSD selectedPeak, boolean update);
}
