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

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorSettingsMSD;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PeakDetectorSettings extends AbstractPeakDetectorSettingsMSD {

	@JsonIgnore
	private IBackfoldingSettings backfoldingSettings;

	public PeakDetectorSettings() {

		backfoldingSettings = new BackfoldingSettings();
	}

	public IBackfoldingSettings getBackfoldingSettings() {

		return backfoldingSettings;
	}
}
