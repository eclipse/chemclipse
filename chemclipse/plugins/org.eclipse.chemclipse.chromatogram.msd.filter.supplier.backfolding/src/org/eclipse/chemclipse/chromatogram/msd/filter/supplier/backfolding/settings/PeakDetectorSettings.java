/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PeakDetectorSettings extends AbstractPeakDetectorSettingsMSD {

	@JsonIgnore
	private IBackfoldingSettings backfoldingSettings;
	@JsonIgnore
	private Threshold threshold = Threshold.MEDIUM;
	@JsonIgnore
	private WindowSize windowSize = WindowSize.WIDTH_5;
	@JsonIgnore
	private Collection<Number> filterIons = new ArrayList<>();
	@JsonIgnore
	private FilterMode filterMode = FilterMode.EXCLUDE;

	public PeakDetectorSettings() {

		backfoldingSettings = new BackfoldingSettings();
	}

	public IBackfoldingSettings getBackfoldingSettings() {

		return backfoldingSettings;
	}

	public Threshold getThreshold() {

		return threshold;
	}

	public void setThreshold(Threshold threshold) {

		if(threshold != null) {
			this.threshold = threshold;
		}
	}

	@Override
	public WindowSize getMovingAverageWindowSize() {

		return windowSize;
	}

	public void setMovingAverageWindowSize(WindowSize windowSize) {

		this.windowSize = windowSize;
	}

	@Override
	public FilterMode getFilterMode() {

		return filterMode;
	}

	public void setFilterMode(FilterMode filterMode) {

		this.filterMode = filterMode;
	}

	@Override
	public Collection<Number> getFilterIon() {

		return filterIons;
	}

	public void setFilterIon(Collection<Number> filterIon) {

		this.filterIons = filterIon;
	}
}
