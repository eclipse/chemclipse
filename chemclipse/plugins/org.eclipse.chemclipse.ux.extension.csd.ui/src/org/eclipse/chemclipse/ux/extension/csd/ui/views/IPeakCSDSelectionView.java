/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;

public interface IPeakCSDSelectionView extends ISelectionView {

	/**
	 * Get the stored peak.
	 * 
	 * @return {@link IPeakMSD}
	 */
	IPeakCSD getPeak();

	/**
	 * Stores the given peak.
	 * 
	 * @param peak
	 */
	void setPeak(IPeakCSD peak);

	/**
	 * Updates the view by using the peak.
	 * 
	 * @param peak
	 * @param forceReload
	 */
	void update(IPeakCSD peak, boolean forceReload);

	/**
	 * Checks if the part is visible and the peak is not null.
	 * 
	 * @param peak
	 */
	boolean doUpdate(IPeakCSD peak);
}
