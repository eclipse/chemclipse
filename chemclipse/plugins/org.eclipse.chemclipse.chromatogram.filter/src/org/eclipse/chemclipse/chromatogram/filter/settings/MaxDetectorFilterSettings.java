/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.settings;

import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class MaxDetectorFilterSettings extends AbstractChromatogramFilterSettings {

	@JsonPropertyDescription(value = "The target name marks the selected maximum/minimum.")
	@JsonProperty(value = "Target Name", defaultValue = "M")
	private String targetName = PreferenceSupplier.DEF_MAX_DETECTOR_TARGET_NAME;
	@JsonProperty(value = "Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float matchFactor = PreferenceSupplier.DEF_MAX_DETECTOR_MATCH_FACTOR;
	@JsonPropertyDescription(value = "Detect maxima or minima.")
	@JsonProperty(value = "Minima", defaultValue = "false")
	private boolean detectMinima = PreferenceSupplier.DEF_MAX_DETECTOR_MINIMA;
	@JsonPropertyDescription(value = "Detect the n maxima/minima. If 0 is selected, all values are taken.")
	@JsonProperty(value = "Count", defaultValue = "0")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_COUNT_MARKER, maxValue = PreferenceSupplier.MAX_COUNT_MARKER)
	private int count = PreferenceSupplier.DEF_MAX_DETECTOR_COUNT;

	public String getTargetName() {

		return targetName;
	}

	public void setTargetName(String targetName) {

		this.targetName = targetName;
	}

	public float getMatchFactor() {

		return matchFactor;
	}

	public void setMatchFactor(float matchFactor) {

		this.matchFactor = matchFactor;
	}

	public boolean isDetectMinima() {

		return detectMinima;
	}

	public void setDetectMinima(boolean detectMinima) {

		this.detectMinima = detectMinima;
	}

	public int getCount() {

		return count;
	}

	public void setCount(int count) {

		this.count = count;
	}
}