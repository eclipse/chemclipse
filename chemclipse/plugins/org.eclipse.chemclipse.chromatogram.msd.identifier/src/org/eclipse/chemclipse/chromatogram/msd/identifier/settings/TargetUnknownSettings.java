/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

public class TargetUnknownSettings {

	/*
	 * Unknown [57,71,43,85,41]
	 */
	private String targetName = "Unknown";
	private float matchQuality = 100.0f;
	private int numberMZ = 5;
	private boolean includeIntensityPercent = false;
	private String markerStart = "[";
	private String markerStop = "]";
	private boolean includeRetentionTime = false;
	private boolean includeRetentionIndex = false;

	public String getTargetName() {

		return targetName;
	}

	public void setTargetName(String targetName) {

		this.targetName = (targetName == null) ? "" : targetName;
	}

	public float getMatchQuality() {

		return matchQuality;
	}

	public void setMatchQuality(float matchQuality) {

		this.matchQuality = matchQuality;
	}

	public int getNumberMZ() {

		return numberMZ;
	}

	public void setNumberMZ(int numberMZ) {

		this.numberMZ = (numberMZ < 0) ? 0 : numberMZ;
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

		this.markerStart = (markerStart == null) ? "" : markerStart;
	}

	public String getMarkerStop() {

		return markerStop;
	}

	public void setMarkerStop(String markerStop) {

		this.markerStop = (markerStop == null) ? "" : markerStop;
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
