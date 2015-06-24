/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.PeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core.IPeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core.PeakIntegrator;

public class TrapezoidPeakIntegratorSupport implements ITrapezoidPeakIntegratorSupport {

	@Override
	public IPeakIntegrationResults calculatePeakIntegrationResults(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		/*
		 * Get the peak integration results.
		 */
		monitor.subTask("Integrate the peaks");
		IPeakIntegrationResults peakIntegrationResults;
		if(peaks != null) {
			IPeakIntegrator peakIntegrator = new PeakIntegrator();
			peakIntegrationResults = peakIntegrator.integrate(peaks, peakIntegrationSettings, monitor);
		} else {
			peakIntegrationResults = new PeakIntegrationResults();
		}
		return peakIntegrationResults;
	}

	@Override
	public IPeakIntegrationResults calculatePeakIntegrationResults(IChromatogramSelection chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		/*
		 * Get the chromatogram.
		 */
		List<? extends IPeak> peaks = null;
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
			peaks = chromatogramMSD.getPeaks(chromatogramSelectionMSD);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			IChromatogramSelectionCSD chromatogramSelectionFID = (IChromatogramSelectionCSD)chromatogramSelection;
			IChromatogramCSD chromatogramFID = chromatogramSelectionFID.getChromatogramCSD();
			peaks = chromatogramFID.getPeaks(chromatogramSelectionFID);
		}
		//
		return calculatePeakIntegrationResults(peaks, peakIntegrationSettings, monitor);
	}

	@Override
	public IPeakIntegrationResult calculatePeakIntegrationResult(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		monitor.subTask("Integrate the peak");
		IPeakIntegrator peakIntegrator = new PeakIntegrator();
		IPeakIntegrationResult peakIntegrationResult = peakIntegrator.integrate(peak, peakIntegrationSettings, monitor);
		return peakIntegrationResult;
	}
}
