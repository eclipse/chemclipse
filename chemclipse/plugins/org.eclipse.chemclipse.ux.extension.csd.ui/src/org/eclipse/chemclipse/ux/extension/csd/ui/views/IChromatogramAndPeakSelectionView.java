/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;

public interface IChromatogramAndPeakSelectionView extends ISelectionView {

	/**
	 * Returns the stored chromatogram selection.
	 * The method may return null.
	 * 
	 * @return {@link IChromatogramSelectionCSD}
	 */
	IChromatogramSelectionCSD getChromatogramSelection();

	/**
	 * Sets a chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 */
	void setChromatogramSelection(IChromatogramSelectionCSD chromatogramSelection);

	/**
	 * Returns the stored chromatogram peak.
	 * The method may return null.
	 * 
	 * @return {@link IChromatogramPeakCSD}
	 */
	IChromatogramPeakCSD getChromatogramPeak();

	/**
	 * Sets a chromatogram peak.
	 * 
	 * @param chromatogramPeak
	 */
	void setChromatogramPeak(IChromatogramPeakCSD chromatogramPeak);

	/**
	 * Updates the view by using values from the chromatogram selection and chromatogram peak.
	 * 
	 * @param chromatogramSelection
	 * @param chromatogramPeak
	 * @param forceReload
	 */
	void update(IChromatogramSelectionCSD chromatogramSelection, IChromatogramPeakCSD chromatogramPeak, boolean forceReload);

	/**
	 * Checks if the part is visible, the chromatogram and the peak is not null.
	 * 
	 * @param chromatogramSelection
	 * @param chromatogramPeak
	 */
	boolean doUpdate(IChromatogramSelectionCSD chromatogramSelection, IChromatogramPeakCSD chromatogramPeak);
}
