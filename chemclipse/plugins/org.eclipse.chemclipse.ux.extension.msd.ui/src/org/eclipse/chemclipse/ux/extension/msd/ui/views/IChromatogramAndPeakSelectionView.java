/*******************************************************************************
 * Copyright (c) 2012, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;

public interface IChromatogramAndPeakSelectionView extends ISelectionView {

	/**
	 * Returns the stored chromatogram selection.
	 * The method may return null.
	 * 
	 * @return {@link IChromatogramSelectionMSD}
	 */
	IChromatogramSelectionMSD getChromatogramSelection();

	/**
	 * Sets a chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 */
	void setChromatogramSelection(IChromatogramSelectionMSD chromatogramSelection);

	/**
	 * Returns the stored chromatogram peak.
	 * The method may return null.
	 * 
	 * @return {@link IChromatogramPeakMSD}
	 */
	IChromatogramPeakMSD getChromatogramPeak();

	/**
	 * Sets a chromatogram peak.
	 * 
	 * @param chromatogramPeak
	 */
	void setChromatogramPeak(IChromatogramPeakMSD chromatogramPeak);

	/**
	 * Updates the view by using values from the chromatogram selection and chromatogram peak.
	 * 
	 * @param chromatogramSelection
	 * @param chromatogramPeak
	 * @param forceReload
	 */
	void update(IChromatogramSelectionMSD chromatogramSelection, IChromatogramPeakMSD chromatogramPeak, boolean forceReload);

	/**
	 * Checks if the part is visible, the chromatogram and the peak is not null.
	 * 
	 * @param chromatogramSelection
	 * @param chromatogramPeak
	 */
	boolean doUpdate(IChromatogramSelectionMSD chromatogramSelection, IChromatogramPeakMSD chromatogramPeak);
}
