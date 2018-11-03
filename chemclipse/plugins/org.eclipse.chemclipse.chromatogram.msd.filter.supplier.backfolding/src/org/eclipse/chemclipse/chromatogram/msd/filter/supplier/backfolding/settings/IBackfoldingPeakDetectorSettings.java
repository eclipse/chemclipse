/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.IBackfoldingSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;

/**
 * @author eselmeister
 */
public interface IBackfoldingPeakDetectorSettings extends IPeakDetectorSettingsMSD {

	Threshold INITIAL_THRESHOLD = Threshold.MEDIUM;

	/**
	 * Returns the backfolding settings.
	 * 
	 * @return {@link IBackfoldingSettings}
	 */
	IBackfoldingSettings getBackfoldingSettings();

	/**
	 * Return the threshold.
	 * 
	 * @return {@link Threshold}
	 */
	Threshold getThreshold();

	/**
	 * Set the threshold.
	 * 
	 * @param threshold
	 */
	void setThreshold(Threshold threshold);
}
