/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChromatogramFilterSettings extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Backfolding Runs", defaultValue = "3")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_BACKFOLDING_RUNS, maxValue = PreferenceSupplier.MAX_BACKFOLDING_RUNS)
	private int numberOfBackfoldingRuns = 3;
	@JsonProperty(value = "Max Retention Time Shift (Milliseconds)", defaultValue = "5000")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_RETENTION_TIME_SHIFT, maxValue = PreferenceSupplier.MAX_RETENTION_TIME_SHIFT)
	private int maximumRetentionTimeShift = 5000;

	public int getNumberOfBackfoldingRuns() {

		return numberOfBackfoldingRuns;
	}

	public void setNumberOfBackfoldingRuns(int numberOfBackfoldingRuns) {

		this.numberOfBackfoldingRuns = numberOfBackfoldingRuns;
	}

	public int getMaximumRetentionTimeShift() {

		return maximumRetentionTimeShift;
	}

	public void setMaximumRetentionTimeShift(int maximumRetentionTimeShift) {

		this.maximumRetentionTimeShift = maximumRetentionTimeShift;
	}
}
