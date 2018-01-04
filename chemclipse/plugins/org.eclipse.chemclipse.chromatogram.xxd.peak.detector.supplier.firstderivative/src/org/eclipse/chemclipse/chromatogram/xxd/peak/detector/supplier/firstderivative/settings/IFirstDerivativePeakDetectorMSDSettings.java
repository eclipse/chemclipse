/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorMSDSettings;

public interface IFirstDerivativePeakDetectorMSDSettings extends IPeakDetectorMSDSettings {

	Threshold INITIAL_THRESHOLD = Threshold.MEDIUM;

	String getThreshold();

	void setThreshold(String threshold);

	/**
	 * If false, VB or BV will be calculated.
	 * If true, VV will be used.
	 * 
	 * @return
	 */
	boolean isIncludeBackground();

	void setIncludeBackground(boolean includeBackground);

	/**
	 * Peaks only >= the min S/N value will be added.
	 * 
	 * @return int
	 */
	float getMinimumSignalToNoiseRatio();

	void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio);

	String getWindowSize();

	void setWindowSize(String windowSize);
}
