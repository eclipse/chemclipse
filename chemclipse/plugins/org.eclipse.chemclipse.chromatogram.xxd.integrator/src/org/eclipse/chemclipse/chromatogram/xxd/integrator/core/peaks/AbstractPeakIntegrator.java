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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;

public abstract class AbstractPeakIntegrator implements IPeakIntegrator {

	protected void validate(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings) throws ValueMustNotBeNullException {

		/*
		 * Test that the values are not null.
		 */
		if(peak == null) {
			throw new ValueMustNotBeNullException("The given peak must not be null.");
		}
		testPeakIntegrationSettings(peakIntegrationSettings);
	}

	protected void validate(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings) throws ValueMustNotBeNullException {

		/*
		 * Test that the values are not null.
		 */
		if(peaks == null) {
			throw new ValueMustNotBeNullException("The given list of peaks must not be null.");
		}
		testPeakIntegrationSettings(peakIntegrationSettings);
	}

	protected void validate(IChromatogramSelection chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings) throws ValueMustNotBeNullException {

		if(chromatogramSelection == null) {
			throw new ValueMustNotBeNullException("The chromatogram selection must not be null.");
		}
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		if(chromatogram == null) {
			throw new ValueMustNotBeNullException("The chromatogram must not be null.");
		}
	}

	private void testPeakIntegrationSettings(IPeakIntegrationSettings peakIntegrationSettings) throws ValueMustNotBeNullException {

		if(peakIntegrationSettings == null) {
			throw new ValueMustNotBeNullException("The given peak integration settings must not be null");
		}
	}
}
