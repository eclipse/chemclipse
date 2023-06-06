/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - abundance ranges
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsSelection extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Start RT (Minutes)", defaultValue = "1")
	@JsonPropertyDescription(value = "Start Retention Time (Minutes), the special value '-Infinity' can be used to express the minimum possible range")
	private double startRetentionTimeMinutes;
	//
	@JsonProperty(value = "Use relative Start RT", defaultValue = "false")
	@JsonPropertyDescription(value = "If checked, the start retention time is relative to the current selection")
	private boolean startRelative;
	//
	@JsonProperty(value = "Minimum Intensity", defaultValue = "0")
	@JsonPropertyDescription(value = "The lower Y-axis range limit")
	private float startAbundance;
	//
	@JsonProperty(value = "Use relative intensity minimum", defaultValue = "true")
	@JsonPropertyDescription(value = "If checked, the start intensity is a percentage relative to max intensity.")
	private boolean startAbundanceRelative;
	//
	@JsonProperty(value = "Stop RT (Minutes)", defaultValue = "10")
	@JsonPropertyDescription(value = "Stop Retention Time (Minutes), the special value 'Infinity' can be used to express the maximum possible range")
	private double stopRetentionTimeMinutes;
	//
	@JsonProperty(value = "Use relative Stop RT", defaultValue = "false")
	@JsonPropertyDescription(value = "If checked, the stop retention time is relative to the current selection")
	private boolean stopRelative;
	//
	@JsonProperty(value = "Maximum Intensity", defaultValue = "100")
	@JsonPropertyDescription(value = "The upper Y-axis range limit")
	private float stopAbundance;
	//
	@JsonProperty(value = "Use relative intensity maximum", defaultValue = "true")
	@JsonPropertyDescription(value = "If checked, the intensity is a percentage relative to max intensity.")
	private boolean stopAbundanceRelative;

	public double getStartRetentionTimeMinutes() {

		return startRetentionTimeMinutes;
	}

	public void setStartRetentionTimeMinutes(double startRetentionTimeMinutes) {

		this.startRetentionTimeMinutes = startRetentionTimeMinutes;
	}

	public double getStopRetentionTimeMinutes() {

		return stopRetentionTimeMinutes;
	}

	public void setStopRetentionTimeMinutes(double stopRetentionTimeMinutes) {

		this.stopRetentionTimeMinutes = stopRetentionTimeMinutes;
	}

	public boolean isStartRelative() {

		return startRelative;
	}

	public void setStartRelative(boolean startRelative) {

		this.startRelative = startRelative;
	}

	public boolean isStopRelative() {

		return stopRelative;
	}

	public void setStopRelative(boolean stopRelative) {

		this.stopRelative = stopRelative;
	}

	public float getStartAbundance() {

		return startAbundance;
	}

	public void setStartAbundance(float startAbundance) {

		this.startAbundance = startAbundance;
	}

	public float getStopAbundance() {

		return stopAbundance;
	}

	public void setStopAbundance(float stopAbundance) {

		this.stopAbundance = stopAbundance;
	}

	public boolean isStartAbundanceRelative() {

		return startAbundanceRelative;
	}

	public void setStartAbundanceRelative(boolean startAbundanceRelative) {

		this.startAbundanceRelative = startAbundanceRelative;
	}

	public boolean isStopAbundanceRelative() {

		return stopAbundanceRelative;
	}

	public void setStopAbundanceRelative(boolean stopAbundanceRelative) {

		this.stopAbundanceRelative = stopAbundanceRelative;
	}
}
