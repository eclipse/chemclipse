/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.support;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.core.IPeakMaxPeakIntegrator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.core.PeakMaxPeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIntegratorSupport {

	public IPeakIntegrationResults calculatePeakIntegrationResults(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		IPeakMaxPeakIntegrator peakIntegrator = new PeakMaxPeakIntegrator();
		IPeakIntegrationResults peakIntegrationResults = peakIntegrator.integrate(peaks, peakIntegrationSettings, monitor);
		return peakIntegrationResults;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public IPeakIntegrationResults calculatePeakIntegrationResults(IChromatogramSelection chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		List<? extends IPeak> peaks = chromatogram.getPeaks(chromatogramSelection);
		return calculatePeakIntegrationResults(peaks, peakIntegrationSettings, monitor);
	}

	public IPeakIntegrationResult calculatePeakIntegrationResult(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		IPeakMaxPeakIntegrator peakIntegrator = new PeakMaxPeakIntegrator();
		IPeakIntegrationResult peakIntegrationResult = peakIntegrator.integrate(peak, peakIntegrationSettings, monitor);
		return peakIntegrationResult;
	}
}
