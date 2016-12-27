/*******************************************************************************
 * Copyright (c) 2011, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;

public abstract class AbstractCombinedIntegrationSettings implements ICombinedIntegrationSettings {

	private IChromatogramIntegrationSettings chromatogramIntegrationSettings;
	private IPeakIntegrationSettings peakIntegrationSettings;

	public AbstractCombinedIntegrationSettings(IChromatogramIntegrationSettings chromatogramIntegrationSettings, IPeakIntegrationSettings peakIntegrationSettings) {
		this.chromatogramIntegrationSettings = chromatogramIntegrationSettings;
		this.peakIntegrationSettings = peakIntegrationSettings;
	}

	@Override
	public IChromatogramIntegrationSettings getChromatogramIntegrationSettings() {

		return chromatogramIntegrationSettings;
	}

	@Override
	public IPeakIntegrationSettings getPeakIntegrationSettings() {

		return peakIntegrationSettings;
	}
}
