/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.AbstractPeakDetectorWSDSettings;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

public class PeakDetectorSettingsWSD extends AbstractPeakDetectorWSDSettings {

	private Threshold threshold = Threshold.MEDIUM;
	private boolean includeBackground = false;
	private float minimumSignalToNoiseRatio;
	private WindowSize windowSize;

	public PeakDetectorSettingsWSD() {
		windowSize = WindowSize.WIDTH_5;
	}

	public Threshold getThreshold() {

		return threshold;
	}

	public void setThreshold(Threshold threshold) {

		if(threshold != null) {
			this.threshold = threshold;
		}
	}

	public boolean isIncludeBackground() {

		return includeBackground;
	}

	public void setIncludeBackground(boolean includeBackground) {

		this.includeBackground = includeBackground;
	}

	public float getMinimumSignalToNoiseRatio() {

		return minimumSignalToNoiseRatio;
	}

	public void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio) {

		this.minimumSignalToNoiseRatio = minimumSignalToNoiseRatio;
	}

	public WindowSize getMovingAverageWindowSize() {

		return windowSize;
	}

	public void setMovingAverageWindowSize(WindowSize windowSize) {

		this.windowSize = windowSize;
	}
}
