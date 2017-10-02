/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorMSDSettings;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FirstDerivativePeakDetectorMSDSettings extends AbstractPeakDetectorMSDSettings implements IFirstDerivativePeakDetectorMSDSettings {

	@JsonProperty(value = "Threshold", defaultValue = "3")
	@JsonPropertyDescription(value = "Threshold (Off -> High): 1, 2, 3, 4")
	private String threshold = Threshold.MEDIUM.toString();
	@JsonProperty(value = "Include Background", defaultValue = "false")
	private boolean includeBackground = false;
	@JsonProperty(value = "Min S/N Ratio", defaultValue = "0")
	private float minimumSignalToNoiseRatio;
	@JsonProperty(value = "Window Size", defaultValue = "5")
	@JsonPropertyDescription(value = "Window Size: 3, 5, 7, ..., 45")
	private String windowSize = WindowSize.SCANS_5.toString();

	@Override
	public String getThreshold() {

		return threshold;
	}

	@Override
	public void setThreshold(String threshold) {

		this.threshold = threshold;
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
	public String getWindowSize() {

		return windowSize;
	}

	@Override
	public void setWindowSize(String windowSize) {

		this.windowSize = windowSize;
	}
}
