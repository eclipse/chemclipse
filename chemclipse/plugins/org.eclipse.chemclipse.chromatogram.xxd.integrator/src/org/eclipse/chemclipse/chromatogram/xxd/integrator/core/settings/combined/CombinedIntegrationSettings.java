/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
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

public class CombinedIntegrationSettings extends AbstractCombinedIntegrationSettings implements ICombinedIntegrationSettings {

	public CombinedIntegrationSettings(IChromatogramIntegrationSettings chromatogramIntegrationSettings, IPeakIntegrationSettings peakIntegrationSettings) {
		super(chromatogramIntegrationSettings, peakIntegrationSettings);
	}
}
