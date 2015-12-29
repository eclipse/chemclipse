/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorMSDSettings;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

/**
 * @author eselmeister
 */
public class FirstDerivativePeakDetectorMSDSettings extends AbstractPeakDetectorMSDSettings implements IFirstDerivativePeakDetectorMSDSettings {

	private Threshold threshold = IFirstDerivativePeakDetectorMSDSettings.INITIAL_THRESHOLD;
	private boolean includeBackground = false;
	private float minimumSignalToNoiseRatio;
	private WindowSize windowSize;

	public FirstDerivativePeakDetectorMSDSettings() {
		windowSize = WindowSize.SCANS_5;
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
