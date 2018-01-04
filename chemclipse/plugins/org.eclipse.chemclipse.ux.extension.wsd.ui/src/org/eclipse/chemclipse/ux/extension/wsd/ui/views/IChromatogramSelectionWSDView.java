/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.views;

import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

public interface IChromatogramSelectionWSDView extends ISelectionView {

	/**
	 * Returns the stored chromatogram selection.
	 * The method may return null.
	 * 
	 * @return {@link IChromatogramSelectionMSD}
	 */
	IChromatogramSelectionWSD getChromatogramSelection();

	/**
	 * Sets a chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 */
	void setChromatogramSelection(IChromatogramSelectionWSD chromatogramSelection);

	/**
	 * Updates the view by using values from the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param forceReload
	 */
	void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload);
}
