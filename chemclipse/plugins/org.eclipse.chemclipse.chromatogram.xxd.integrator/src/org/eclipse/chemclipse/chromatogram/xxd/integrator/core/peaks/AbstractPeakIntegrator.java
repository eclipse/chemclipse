/*******************************************************************************
 * Copyright (c) 2011, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractPeakIntegrator<T> implements IPeakIntegrator<T> {

	public static final String DESCRIPTION = "Peak Integrator";

	protected IProcessingInfo<T> validate(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		if(peak == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The given peak must not be null.");
		}
		testPeakIntegrationSettings(peakIntegrationSettings, processingInfo);
		return processingInfo;
	}

	protected IProcessingInfo<T> validate(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		if(peaks == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The given list of peaks must not be null.");
		}
		testPeakIntegrationSettings(peakIntegrationSettings, processingInfo);
		return processingInfo;
	}

	protected IProcessingInfo<T> validate(IChromatogramSelection<?, ?> chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The chromatogram selection must not be null.");
		}
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		if(chromatogram == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The chromatogram must not be null.");
		}
		testPeakIntegrationSettings(peakIntegrationSettings, processingInfo);
		return processingInfo;
	}

	private void testPeakIntegrationSettings(IPeakIntegrationSettings peakIntegrationSettings, IProcessingInfo<T> processingInfo) {

		if(peakIntegrationSettings == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The given peak integration settings must not be null");
		}
	}
}
