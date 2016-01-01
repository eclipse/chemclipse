/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;

public interface IPeakMSDSelectionView extends ISelectionView {

	/**
	 * Get the stored peak.
	 * 
	 * @return {@link IPeakMSD}
	 */
	IPeakMSD getPeak();

	/**
	 * Stores the given peak.
	 * 
	 * @param peak
	 */
	void setPeak(IPeakMSD peak);

	/**
	 * Updates the view by using the peak.
	 * 
	 * @param peak
	 * @param forceReload
	 */
	void update(IPeakMSD peak, boolean forceReload);

	/**
	 * Checks if the part is visible and the peak is not null.
	 * 
	 * @param peak
	 */
	boolean doUpdate(IPeakMSD peak);
}
