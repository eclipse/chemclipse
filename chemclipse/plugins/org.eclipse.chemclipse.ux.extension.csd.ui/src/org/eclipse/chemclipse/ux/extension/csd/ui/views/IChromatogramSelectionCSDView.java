/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;

public interface IChromatogramSelectionCSDView extends ISelectionView {

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
	 * Updates the view by using values from the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param forceReload
	 */
	void update(IChromatogramSelectionCSD chromatogramSelection, boolean forceReload);
}
