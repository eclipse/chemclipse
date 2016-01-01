/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.core.IPeakMaxPeakIntegrator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.core.PeakMaxPeakIntegrator;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;

public class PeakMaxPeakIntegratorSupport implements IPeakMaxPeakIntegratorSupport {

	@Override
	public IPeakIntegrationResults calculatePeakIntegrationResults(List<IPeakMSD> peaks, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		/*
		 * Get the peak integration results.
		 */
		monitor.subTask("Integrate the peaks");
		IPeakMaxPeakIntegrator peakIntegrator = new PeakMaxPeakIntegrator();
		IPeakIntegrationResults peakIntegrationResults = peakIntegrator.integrate(peaks, peakIntegrationSettings, monitor);
		return peakIntegrationResults;
	}

	@Override
	public IPeakIntegrationResults calculatePeakIntegrationResults(IChromatogramSelectionMSD chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		/*
		 * Get the chromatogram.
		 */
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		List<IChromatogramPeakMSD> peaks = chromatogram.getPeaks(chromatogramSelection);
		/*
		 * TODO make generic
		 * May use a better generic supertype, e.g <? extends IPeak>???
		 */
		List<IPeakMSD> peakList = new ArrayList<IPeakMSD>();
		for(IChromatogramPeakMSD chromatogramPeak : peaks) {
			peakList.add(chromatogramPeak);
		}
		return calculatePeakIntegrationResults(peakList, peakIntegrationSettings, monitor);
	}

	@Override
	public IPeakIntegrationResult calculatePeakIntegrationResult(IPeakMSD peak, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		monitor.subTask("Integrate the peak");
		IPeakMaxPeakIntegrator peakIntegrator = new PeakMaxPeakIntegrator();
		IPeakIntegrationResult peakIntegrationResult = peakIntegrator.integrate(peak, peakIntegrationSettings, monitor);
		return peakIntegrationResult;
	}
}
