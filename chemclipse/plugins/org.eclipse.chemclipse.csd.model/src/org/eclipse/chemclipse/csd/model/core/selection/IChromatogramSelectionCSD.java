/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.selection;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public interface IChromatogramSelectionCSD extends IChromatogramSelection<IChromatogramPeakCSD, IChromatogramCSD> {

	/**
	 * Returns the stored chromatogram.
	 * May return null.
	 *
	 * @deprecated use {@link #getChromatogram()} instead
	 * @return {@link IChromatogramCSD}
	 */
	@Deprecated
	default IChromatogramCSD getChromatogramCSD() {

		return getChromatogram();
	}

	/**
	 * Returns the selected scan of the current chromatogram or null, if none is
	 * stored.
	 *
	 * @return {@link IScanCSD}
	 */
	@Override
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
}
