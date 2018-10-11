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

public class FirstDerivativePeakDetectorWSDSettings extends AbstractPeakDetectorWSDSettings implements IFirstDerivativePeakDetectorWSDSettings {

	private Threshold threshold = IFirstDerivativePeakDetectorWSDSettings.INITIAL_THRESHOLD;
	private boolean includeBackground = false;
	private float minimumSignalToNoiseRatio;
	private WindowSize windowSize;

	public FirstDerivativePeakDetectorWSDSettings() {
		windowSize = WindowSize.WIDTH_5;
	}

	@Override
	public Threshold getThreshold() {

		return threshold;
	}

	@Override
	public void setThreshold(Threshold threshold) {

		if(threshold != null) {
			this.threshold = threshold;
		}
	}

	@Override
	public boolean isIncludeBackground() {

		return includeBackground;
	}

	@Override
	public void setIncludeBackground(boolean includeBackground) {

		this.includeBackground = includeBackground;
	}

	@Override
	public float getMinimumSignalToNoiseRatio() {

		return minimumSignalToNoiseRatio;
	}

	@Override
	public void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio) {

		this.minimumSignalToNoiseRatio = minimumSignalToNoiseRatio;
	}

	@Override
	public WindowSize getMovingAverageWindowSize() {

		return windowSize;
	}

	@Override
	public void setMovingAverageWindowSize(WindowSize windowSize) {

		this.windowSize = windowSize;
	}
}
