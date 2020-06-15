/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorSettingsMSD;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PeakDetectorSettings extends AbstractPeakDetectorSettingsMSD {

	@JsonIgnore
	private IOnsiteSettings onsiteSettings;
	@JsonProperty(value = "Min S/N Ratio", defaultValue = "0.0")
	private float minSignalToNoiseRatio = 0.0f;
	@JsonProperty(value = "Min Leading", defaultValue = "0.1")
	private float minLeading = 0.1f;
	@JsonProperty(value = "Max Leading", defaultValue = "2.0")
	private float maxLeading = 2.0f;
	@JsonProperty(value = "Min Tailing", defaultValue = "0.1")
	private float minTailing = 0.1f;
	@JsonProperty(value = "Max Tailing", defaultValue = "2.0")
	private float maxTailing = 2.0f;
	@JsonProperty(value = "Filter Model Peaks", defaultValue = "true")
	private boolean filterModelPeaks = true;

	public PeakDetectorSettings() {

		onsiteSettings = new OnsiteSettings();
	}

	public IOnsiteSettings getOnsiteSettings() {

		return onsiteSettings;
	}

	public float getMinSignalToNoiseRatio() {

		return minSignalToNoiseRatio;
	}

	public void setMinSignalToNoiseRatio(float minSignalToNoiseRatio) {

		this.minSignalToNoiseRatio = minSignalToNoiseRatio;
	}

	public float getMinLeading() {

		return minLeading;
	}

	public void setMinLeading(float minLeading) {

		this.minLeading = minLeading;
	}

	public float getMaxLeading() {

		return maxLeading;
	}

	public void setMaxLeading(float maxLeading) {

		this.maxLeading = maxLeading;
	}

	public float getMinTailing() {

		return minTailing;
	}

	public void setMinTailing(float minTailing) {

		this.minTailing = minTailing;
	}

	public float getMaxTailing() {

		return maxTailing;
	}

	public void setMaxTailing(float maxTailing) {

		this.maxTailing = maxTailing;
	}

	public boolean isFilterModelPeaks() {

		return filterModelPeaks;
	}

	public void setFilterModelPeaks(boolean filterModelPeaks) {

		this.filterModelPeaks = filterModelPeaks;
	}
}