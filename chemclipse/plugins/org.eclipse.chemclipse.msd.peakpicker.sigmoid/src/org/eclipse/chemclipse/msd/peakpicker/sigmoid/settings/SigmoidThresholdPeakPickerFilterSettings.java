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
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.peakpicker.sigmoid.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.AbstractMassSpectrumFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.MassSpectrumType;
import org.eclipse.chemclipse.support.literature.LiteratureReference;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class SigmoidThresholdPeakPickerFilterSettings extends AbstractMassSpectrumFilterSettings {

	@JsonProperty(value = "Start m/z", defaultValue = "3000")
	@DoubleSettingsProperty(minValue = 0)
	private double startMZ = 3000;

	@JsonProperty(value = "Stop m/z", defaultValue = "13000")
	@DoubleSettingsProperty(minValue = 0)
	private double stopMZ = 13000;

	@JsonProperty(value = "Resolution", defaultValue = "800")
	@JsonPropertyDescription(value = "Relative m/z resolution R. A peak apex is the most intense point within m/z \u00b1 m/z / R.")
	@IntSettingsProperty(minValue = 1)
	private int resolution = 800;

	@JsonProperty(value = "Scaling Factor", defaultValue = "1.5")
	@JsonPropertyDescription(value = "Height of the sigmoid relative to the mean of the 4th to 13th most intense peaks.")
	@DoubleSettingsProperty(minValue = 0, step = 0.1)
	private double scalingFactor = 1.5;

	@JsonProperty(value = "Slope", defaultValue = "7.5")
	@JsonPropertyDescription(value = "Slope parameter l of the sigmoid, its steepness is exp(-l).")
	@DoubleSettingsProperty(step = 0.5)
	private double slope = 7.5;

	@JsonProperty(value = "Center m/z", defaultValue = "7000")
	@JsonPropertyDescription(value = "Inflection point of the sigmoid.")
	@DoubleSettingsProperty(minValue = 0)
	private double centerMZ = 7000;

	@JsonProperty(value = "Noise Factor", defaultValue = "3")
	@JsonPropertyDescription(value = "Multiple of the estimated noise added to the threshold.")
	@DoubleSettingsProperty(minValue = 0, step = 0.5)
	private double noiseFactor = 3;

	@JsonProperty(value = "Number of Peaks", defaultValue = "30")
	@JsonPropertyDescription(value = "If larger than 0, the scaling factor is adjusted until this many peaks are picked.")
	@IntSettingsProperty(minValue = 0)
	private int numberOfPeaks = 30;

	public double getStartMZ() {

		return startMZ;
	}

	public void setStartMZ(double startMZ) {

		this.startMZ = startMZ;
	}

	public double getStopMZ() {

		return stopMZ;
	}

	public void setStopMZ(double stopMZ) {

		this.stopMZ = stopMZ;
	}

	public int getResolution() {

		return resolution;
	}

	public void setResolution(int resolution) {

		this.resolution = resolution;
	}

	public double getScalingFactor() {

		return scalingFactor;
	}

	public void setScalingFactor(double scalingFactor) {

		this.scalingFactor = scalingFactor;
	}

	public double getSlope() {

		return slope;
	}

	public void setSlope(double slope) {

		this.slope = slope;
	}

	public double getCenterMZ() {

		return centerMZ;
	}

	public void setCenterMZ(double centerMZ) {

		this.centerMZ = centerMZ;
	}

	public double getNoiseFactor() {

		return noiseFactor;
	}

	public void setNoiseFactor(double noiseFactor) {

		this.noiseFactor = noiseFactor;
	}

	public int getNumberOfPeaks() {

		return numberOfPeaks;
	}

	public void setNumberOfPeaks(int numberOfPeaks) {

		this.numberOfPeaks = numberOfPeaks;
	}

	@Override
	public List<MassSpectrumType> appliesToMassSpectrumTypes() {

		return Arrays.asList(MassSpectrumType.PROFILE);
	}

	@Override
	public List<LiteratureReference> getLiteratureReferences() {

		List<LiteratureReference> literatureReferences = new ArrayList<>();
		literatureReferences.add(createLiteratureReference("2026.05.08.723807.ris", "https://doi.org/10.64898/2026.05.08.723807"));
		return literatureReferences;
	}

	private static LiteratureReference createLiteratureReference(String file, String url) {

		String content;
		try {
			content = new String(SigmoidThresholdPeakPickerFilterSettings.class.getResourceAsStream(file).readAllBytes());
		} catch(Exception e) {
			content = url;
			Logger.getLogger(SigmoidThresholdPeakPickerFilterSettings.class).warn(e);
		}
		return new LiteratureReference(content);
	}
}
