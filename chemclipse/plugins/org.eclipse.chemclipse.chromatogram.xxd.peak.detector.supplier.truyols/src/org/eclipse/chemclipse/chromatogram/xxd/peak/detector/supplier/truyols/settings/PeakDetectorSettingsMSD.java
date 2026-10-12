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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.FilterMode;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.model.DerivativeOption;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.model.DetectorType;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.core.MarkedTraces;
import org.eclipse.chemclipse.support.literature.LiteratureReference;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.chemclipse.support.settings.LabelProperty;
import org.eclipse.chemclipse.support.traces.ITrace;
import org.eclipse.chemclipse.support.traces.TraceNominalMSD;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PeakDetectorSettingsMSD extends AbstractPeakDetectorSettingsMSD {

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

	@JsonProperty(value = "Filter Mode", defaultValue = "EXCLUDE")
	@LabelProperty(value = "%FilterMode")
	private FilterMode filterMode = FilterMode.EXCLUDE;

	@JsonProperty(value = "m/z values to filter", defaultValue = "")
	@LabelProperty(value = "%FilterMasses")
	private String filterIonsString;

	@JsonProperty(value = "Use Individual Traces", defaultValue = "false")
	@LabelProperty(value = "%UseIndividualTraces", tooltip = "%UseIndividualTracesDescription")
	private boolean useIndividualTraces = false;

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

	public FilterMode getFilterMode() {

		return filterMode == null ? FilterMode.EXCLUDE : filterMode;
	}

	public void setFilterMode(FilterMode filterMode) {

		this.filterMode = filterMode;
	}

	public String getFilterIonsString() {

		return filterIonsString;
	}

	public void setFilterIonsString(String filterIonsString) {

		this.filterIonsString = filterIonsString;
	}

	public boolean isUseIndividualTraces() {

		return useIndividualTraces;
	}

	public void setUseIndividualTraces(boolean useIndividualTraces) {

		this.useIndividualTraces = useIndividualTraces;
	}

	/**
	 * Translates the user's filter string + mode + individual-trace flag into
	 * the {@link IMarkedTraces} collection consumed by the MSD detector. The
	 * mapping mirrors the first-derivative supplier:
	 * {@link FilterMode#EXCLUDE} → {@link MarkedTraceModus#INCLUDE} and
	 * vice-versa (we mark the traces we want to keep in the signal).
	 */
	@JsonIgnore
	public Collection<IMarkedTraces<ITrace>> getFilterIons() {

		MarkedTraceModus markedTraceModus;
		switch(getFilterMode()) {
			case EXCLUDE:
				markedTraceModus = MarkedTraceModus.INCLUDE;
				break;
			case INCLUDE:
				markedTraceModus = MarkedTraceModus.EXCLUDE;
				break;
			default:
				throw new IllegalArgumentException("Unsupported filter mode " + getFilterMode());
		}
		Set<ITrace> traces = parseIons(filterIonsString).stream().map(mz -> new TraceNominalMSD(mz)).collect(Collectors.toSet());
		if(isUseIndividualTraces()) {
			List<IMarkedTraces<ITrace>> list = new ArrayList<>();
			for(ITrace trace : traces) {
				IMarkedTraces<ITrace> markedTraces = new MarkedTraces(markedTraceModus);
				markedTraces.add(trace);
				list.add(markedTraces);
			}
			return list;
		}
		IMarkedTraces<ITrace> markedTraces = new MarkedTraces(markedTraceModus);
		markedTraces.addAll(traces);
		return Collections.singleton(markedTraces);
	}

	static Collection<Double> parseIons(String filterIonsString) {

		if(filterIonsString == null || filterIonsString.isBlank()) {
			return Collections.emptyList();
		}
		List<Double> ionValues = new ArrayList<>();
		for(String token : filterIonsString.trim().split("[\\s,;]+")) {
			if(token.isEmpty()) {
				continue;
			}
			try {
				ionValues.add(Double.valueOf(token));
			} catch(NumberFormatException e) {
				// skip invalid tokens
			}
		}
		return ionValues;
	}

	@Override
	public List<LiteratureReference> getLiteratureReferences() {

		return LiteratureSupport.getLiteratureReferences();
	}
}