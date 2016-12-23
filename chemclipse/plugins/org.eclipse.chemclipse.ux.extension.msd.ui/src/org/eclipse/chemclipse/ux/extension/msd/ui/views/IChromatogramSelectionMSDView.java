/*******************************************************************************
 * Copyright (c) 2012, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;

public interface IChromatogramSelectionMSDView extends ISelectionView {

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
	 * Updates the view by using values from the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param forceReload
	 */
	void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload);

	/**
	 * Checks if the part is visible and the chromatogram is not null.
	 * 
	 * @param chromatogramSelection
	 */
	boolean doUpdate(IChromatogramSelection chromatogramSelection);
}
