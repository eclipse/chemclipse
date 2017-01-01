/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;

public abstract class AbstractCombinedIntegrator implements ICombinedIntegrator {

	protected void validate(IChromatogramSelection chromatogramSelection, ICombinedIntegrationSettings combinedIntegrationSettings) throws ValueMustNotBeNullException {

		/*
		 * Test that the values are not null.
		 */
		if(chromatogramSelection == null) {
			throw new ValueMustNotBeNullException("The given chromatogram selection must not be null.");
		}
		testChromatogramIntegrationSettings(combinedIntegrationSettings.getChromatogramIntegrationSettings());
		testPeakIntegrationSettings(combinedIntegrationSettings.getPeakIntegrationSettings());
	}

	private void testChromatogramIntegrationSettings(IChromatogramIntegrationSettings chromatogramIntegrationSettings) throws ValueMustNotBeNullException {

		if(chromatogramIntegrationSettings == null) {
			throw new ValueMustNotBeNullException("The given chromatogram integration settings must not be null");
		}
	}

	private void testPeakIntegrationSettings(IPeakIntegrationSettings peakIntegrationSettings) throws ValueMustNotBeNullException {

		if(peakIntegrationSettings == null) {
			throw new ValueMustNotBeNullException("The given peak integration settings must not be null");
		}
	}
}
