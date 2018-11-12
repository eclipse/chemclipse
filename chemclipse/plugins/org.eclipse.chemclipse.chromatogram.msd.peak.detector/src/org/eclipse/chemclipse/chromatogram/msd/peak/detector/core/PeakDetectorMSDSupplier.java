/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.AbstractPeakDetectorSupplier;

public class PeakDetectorMSDSupplier extends AbstractPeakDetectorSupplier<IPeakDetectorSettingsMSD> implements IPeakDetectorMSDSupplier {

	public PeakDetectorMSDSupplier(String id, String description, String peakDetectorName) {
		super(id, description, peakDetectorName);
	}

	@Override
	public Class<? extends IPeakDetectorSettingsMSD> getSettingsClass() {

		return super.getSettingsClass();
	}
}
