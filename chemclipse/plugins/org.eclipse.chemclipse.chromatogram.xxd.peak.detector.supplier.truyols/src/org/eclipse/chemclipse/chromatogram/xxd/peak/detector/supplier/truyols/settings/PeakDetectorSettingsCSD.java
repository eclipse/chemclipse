/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.settings;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.AbstractPeakDetectorCSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.model.DerivativeOption;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.model.DetectorType;
import org.eclipse.chemclipse.support.literature.LiteratureReference;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.chemclipse.support.settings.LabelProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PeakDetectorSettingsCSD extends AbstractPeakDetectorCSDSettings {

	@JsonProperty(value = "Detector Type", defaultValue = "VV")
	@LabelProperty(value = "Detector Type", tooltip = "Select the option to set the peak baseline.")
	private DetectorType detectorType = DetectorType.VV;

	@JsonProperty(value = "Derivative Option", defaultValue = "SECOND")
	@LabelProperty(value = "Derivative Option", tooltip = "Select which derivative drives the peak detection (Vivó-Truyols).")
	private DerivativeOption derivativeOption = DerivativeOption.SECOND;

	@JsonProperty(value = "Window Size", defaultValue = "9")
	@LabelProperty(value = "%WindowSize", tooltip = "%WindowSizeDescription")
	@IntSettingsProperty(useSpinner = true, minValue = 5, maxValue = 45, validation = Validation.ODD_NUMBER_INCLUDING_ZERO)
	private int windowSize = 9;

	@JsonProperty(value = "Polynomial Order", defaultValue = "2")
	@LabelProperty(value = "%PolynomialOrder", tooltip = "%PolynomialOrderDescription")
	@IntSettingsProperty(useSpinner = true, minValue = 2, maxValue = 5)
	private int polynomialOrder = 2;

	@JsonProperty(value = "Noise Factor", defaultValue = "5.0")
	@LabelProperty(value = "%NoiseFactor", tooltip = "%NoiseFactorDescription")
	@FloatSettingsProperty(minValue = 1.0f, maxValue = 100.0f)
	private float noiseFactor = 5.0f;

	@JsonProperty(value = "Min S/N Ratio", defaultValue = "0")
	@LabelProperty(value = "%MinSignalToNoiseRatio")
	@FloatSettingsProperty(minValue = 0f, maxValue = Float.MAX_VALUE)
	private float minimumSignalToNoiseRatio = 0.0f;

	public DetectorType getDetectorType() {

		return detectorType;
	}

	public void setDetectorType(DetectorType detectorType) {

		this.detectorType = detectorType;
	}

	public DerivativeOption getDerivativeOption() {

		return derivativeOption;
	}

	public void setDerivativeOption(DerivativeOption derivativeOption) {

		this.derivativeOption = derivativeOption;
	}

	public int getWindowSize() {

		return windowSize;
	}

	public void setWindowSize(int windowSize) {

		this.windowSize = windowSize;
	}

	public int getPolynomialOrder() {

		return polynomialOrder;
	}

	public void setPolynomialOrder(int polynomialOrder) {

		this.polynomialOrder = polynomialOrder;
	}

	public float getNoiseFactor() {

		return noiseFactor;
	}

	public void setNoiseFactor(float noiseFactor) {

		this.noiseFactor = noiseFactor;
	}

	public float getMinimumSignalToNoiseRatio() {

		return minimumSignalToNoiseRatio;
	}

	public void setMinimumSignalToNoiseRatio(float minimumSignalToNoiseRatio) {

		this.minimumSignalToNoiseRatio = minimumSignalToNoiseRatio;
	}

	@Override
	public List<LiteratureReference> getLiteratureReferences() {

		return LiteratureSupport.getLiteratureReferences();
	}
}