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
import org.eclipse.chemclipse.support.settings.RetentionTimeMinutesProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SupplierFilterSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterSettings {

	@JsonProperty(value = "Backfolding Runs", defaultValue = "3")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_BACKFOLDING_RUNS, maxValue = PreferenceSupplier.MAX_BACKFOLDING_RUNS)
	private int numberOfBackfoldingRuns;
	@JsonProperty(value = "Max Retention Time Shift (Minutes)", defaultValue = "5000")
	@RetentionTimeMinutesProperty(minValue = PreferenceSupplier.MIN_RETENTION_TIME_SHIFT, maxValue = PreferenceSupplier.MAX_RETENTION_TIME_SHIFT)
	private int maximumRetentionTimeShift;

	@Override
	public int getNumberOfBackfoldingRuns() {

		return numberOfBackfoldingRuns;
	}

	@Override
	public void setNumberOfBackfoldingRuns(int numberOfBackfoldingRuns) {

		this.numberOfBackfoldingRuns = numberOfBackfoldingRuns;
	}

	@Override
	public int getMaximumRetentionTimeShift() {

		return maximumRetentionTimeShift;
	}

	@Override
	public void setMaximumRetentionTimeShift(int maximumRetentionTimeShift) {

		this.maximumRetentionTimeShift = maximumRetentionTimeShift;
	}
}
