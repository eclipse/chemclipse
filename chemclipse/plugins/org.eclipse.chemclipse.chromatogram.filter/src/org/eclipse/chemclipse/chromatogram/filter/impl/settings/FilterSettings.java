/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettings extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Start RT (Minutes)", defaultValue = "1")
	@JsonPropertyDescription(value = "Start Retention Time (Minutes)")
	private double startRetentionTimeMinutes;
	@JsonProperty(value = "Stop RT (Minutes)", defaultValue = "10")
	@JsonPropertyDescription(value = "Stop Retention Time (Minutes)")
	private double stopRetentionTimeMinutes;

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
}
