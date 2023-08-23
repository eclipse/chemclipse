/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.system;

import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ModelSettingsMSD implements ISystemProcessSettings {

	@JsonProperty(value = "Use Nominal m/z", defaultValue = "true")
	@JsonPropertyDescription(value = "Use unit mass.")
	private boolean useNominalMZ = true;
	@JsonProperty(value = "Use Normalized Scan", defaultValue = "true")
	@JsonPropertyDescription(value = "When merging scan, normalize the intensities.")
	private boolean useNormalizedScan = true;
	@JsonProperty(value = "Calculation Type", defaultValue = "SUM")
	@JsonPropertyDescription(value = "Defines how to create combined scans.")
	private CalculationType calculationType = CalculationType.SUM;
	@JsonProperty(value = "Use Peaks Instead Of Scans", defaultValue = "false")
	@JsonPropertyDescription(value = "Use peaks instead of scans to calculate the combined spectrum.")
	private boolean usePeaksInsteadOfScans = false;
	@JsonProperty(value = "Copy Traces Clipboard", defaultValue = "5")
	@JsonPropertyDescription(value = "The number of n highest traces that shall be copied to clipboard.")
	@IntSettingsProperty(minValue = 1, maxValue = Integer.MAX_VALUE)
	private int copyTracesClipboard = 5;

	public boolean isUseNormalizedScan() {

		return useNormalizedScan;
	}

	public void setUseNormalizedScan(boolean useNormalizedScan) {

		this.useNormalizedScan = useNormalizedScan;
	}

	public CalculationType getCalculationType() {

		return calculationType;
	}

	public void setCalculationType(CalculationType calculationType) {

		this.calculationType = calculationType;
	}

	public boolean isUsePeaksInsteadOfScans() {

		return usePeaksInsteadOfScans;
	}

	public void setUsePeaksInsteadOfScans(boolean usePeaksInsteadOfScans) {

		this.usePeaksInsteadOfScans = usePeaksInsteadOfScans;
	}

	public int getCopyTracesClipboard() {

		return copyTracesClipboard;
	}

	public void setCopyTracesClipboard(int copyTracesClipboard) {

		this.copyTracesClipboard = copyTracesClipboard;
	}

	public boolean isUseNominalMZ() {

		return useNominalMZ;
	}

	public void setUseNominalMZ(boolean useNominalMZ) {

		this.useNominalMZ = useNominalMZ;
	}
}