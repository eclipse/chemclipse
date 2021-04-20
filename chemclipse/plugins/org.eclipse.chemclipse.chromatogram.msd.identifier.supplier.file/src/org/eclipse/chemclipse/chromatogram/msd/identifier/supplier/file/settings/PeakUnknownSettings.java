/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.PeakIdentifierAdapterSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.identifier.GeneratedIdentifierSettings;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@GeneratedIdentifierSettings
public class PeakUnknownSettings extends PeakIdentifierAdapterSettingsMSD {

	@JsonProperty(value = "Limit Match Factor", defaultValue = "80.0")
	@JsonPropertyDescription(value = "Run an identification if no target exists with a Match Factor >= the given limit.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float limitMatchFactor = 80.0f;
	@JsonProperty(value = "Target Name", defaultValue = "Unknown")
	private String targetName = "Unknown";
	@JsonProperty(value = "Match Quality", defaultValue = "80.0")
	@JsonPropertyDescription(value = "The match quality is set as the Match Factor.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float matchQuality = 80.0f;
	@JsonProperty(value = "Number of m/z", defaultValue = "5")
	@JsonPropertyDescription(value = "This is the number of m/z printed, sorted asc by intensity.")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_NUMBER_OF_MZ, maxValue = PreferenceSupplier.MAX_NUMBER_OF_MZ)
	private int numberOfMZ = 5;
	@JsonProperty(value = "Include Intensity [%]", defaultValue = "false")
	private boolean includeIntensityPercent = false;
	@JsonProperty(value = "Marker Start", defaultValue = PreferenceSupplier.DEF_MARKER_START_UNKNOWN)
	private String markerStart = "[";
	@JsonProperty(value = "Marker Stop", defaultValue = PreferenceSupplier.DEF_MARKER_STOP_UNKNOWN)
	private String markerStop = "]";
	@JsonProperty(value = "Include Retention Time", defaultValue = "false")
	private boolean includeRetentionTime = false;
	@JsonProperty(value = "Include Retention Index", defaultValue = "false")
	private boolean includeRetentionIndex = false;

	public float getLimitMatchFactor() {

		return limitMatchFactor;
	}

	public void setLimitMatchFactor(float limitMatchFactor) {

		this.limitMatchFactor = limitMatchFactor;
	}

	public String getTargetName() {

		return targetName;
	}

	public void setTargetName(String targetName) {

		this.targetName = targetName;
	}

	public float getMatchQuality() {

		return matchQuality;
	}

	public void setMatchQuality(float matchQuality) {

		this.matchQuality = matchQuality;
	}

	public int getNumberOfMZ() {

		return numberOfMZ;
	}

	public void setNumberOfMZ(int numberOfMZ) {

		this.numberOfMZ = numberOfMZ;
	}

	public boolean isIncludeIntensityPercent() {

		return includeIntensityPercent;
	}

	public void setIncludeIntensityPercent(boolean includeIntensityPercent) {

		this.includeIntensityPercent = includeIntensityPercent;
	}

	public String getMarkerStart() {

		return markerStart;
	}

	public void setMarkerStart(String markerStart) {

		this.markerStart = markerStart;
	}

	public String getMarkerStop() {

		return markerStop;
	}

	public void setMarkerStop(String markerStop) {

		this.markerStop = markerStop;
	}

	public boolean isIncludeRetentionTime() {

		return includeRetentionTime;
	}

	public void setIncludeRetentionTime(boolean includeRetentionTime) {

		this.includeRetentionTime = includeRetentionTime;
	}

	public boolean isIncludeRetentionIndex() {

		return includeRetentionIndex;
	}

	public void setIncludeRetentionIndex(boolean includeRetentionIndex) {

		this.includeRetentionIndex = includeRetentionIndex;
	}
}
