/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks.settings;

import org.eclipse.chemclipse.xxd.filter.peaks.PeakModelOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeletePeaksByModelFilterSettings {

	@JsonProperty(value = "Peak Model Option", defaultValue = "NON_STRICT")
	@JsonPropertyDescription(value = "Peaks with the given peak model will be deleted.")
	private PeakModelOption peakModelOption = PeakModelOption.NON_STRICT;

	public PeakModelOption getPeakModelOption() {

		return peakModelOption;
	}

	public void setPeakModelOption(PeakModelOption peakModelOption) {

		this.peakModelOption = peakModelOption;
	}
}