/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.selection;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public interface IChromatogramSelectionWSD extends IChromatogramSelection {

	/**
	 * Returns the stored chromatogram.
	 * May return null.
	 * 
	 * @return {@link IChromatogramWSD}
	 */
	IChromatogramWSD getChromatogramWSD();

	/**
	 * Returns the selected scan of the current chromatogram or null, if none is
	 * stored.
	 * 
	 * @return {@link IScanWSD}
	 */
	IScanWSD getSelectedScan();

	/**
	 * Sets the selected scan of the current chromatogram.<br/>
	 * The scan must not be null.
	 */
	void setSelectedScan(IScanWSD selectedScan);

	/**
	 * Use this convenient method, if you don't want to fire and update.
	 * 
	 * @param selectedScan
	 * @param update
	 */
	void setSelectedScan(IScanWSD selectedScan, boolean update);
}
