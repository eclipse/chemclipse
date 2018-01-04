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
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;

public interface IMassSpectrumSelectionView extends ISelectionView {

	/**
	 * Get the stored mass spectrum.
	 * 
	 * @return {@link IScanMSD}
	 */
	IScanMSD getMassSpectrum();

	/**
	 * Stores the given mass spectrum.
	 * 
	 * @param massSpectrum
	 */
	void setMassSpectrum(IScanMSD massSpectrum);

	/**
	 * Updates the view by using the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @param forceReload
	 */
	void update(IScanMSD massSpectrum, boolean forceReload);

	/**
	 * Checks if the part is visible and the mass spectrum is not null.
	 * 
	 * @param massSpectrum
	 */
	boolean doUpdate(IScanMSD massSpectrum);
}
