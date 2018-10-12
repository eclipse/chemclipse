/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.AbstractPeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.PeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.PeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIntegrator extends AbstractPeakIntegrator {

	private static final Logger logger = Logger.getLogger(PeakIntegrator.class);

	@Override
	public IProcessingInfo integrate(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(peak, peakIntegrationSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(peakIntegrationSettings instanceof PeakIntegrationSettings) {
				try {
					PeakIntegratorSupport firstDerivativePeakIntegratorSupport = new PeakIntegratorSupport();
					IPeakIntegrationResult peakIntegrationResult = firstDerivativePeakIntegratorSupport.calculatePeakIntegrationResult(peak, (PeakIntegrationSettings)peakIntegrationSettings, monitor);
					IPeakIntegrationResults peakIntegrationResults = new PeakIntegrationResults();
					peakIntegrationResults.add(peakIntegrationResult);
					processingInfo.setProcessingResult(peakIntegrationResults);
				} catch(ValueMustNotBeNullException e) {
					logger.warn(e);
				}
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo integrate(IPeak peak, IProgressMonitor monitor) {

		PeakIntegrationSettings peakIntegrationSettings = PreferenceSupplier.getPeakIntegrationSettings();
		return integrate(peak, peakIntegrationSettings, monitor);
	}

	@Override
	public IProcessingInfo integrate(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(peaks, peakIntegrationSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(peakIntegrationSettings instanceof PeakIntegrationSettings) {
				try {
					PeakIntegratorSupport firstDerivativePeakIntegratorSupport = new PeakIntegratorSupport();
					IPeakIntegrationResults peakIntegrationResults = firstDerivativePeakIntegratorSupport.calculatePeakIntegrationResults(peaks, (PeakIntegrationSettings)peakIntegrationSettings, monitor);
					processingInfo.setProcessingResult(peakIntegrationResults);
				} catch(ValueMustNotBeNullException e) {
					logger.warn(e);
				}
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo integrate(List<? extends IPeak> peaks, IProgressMonitor monitor) {

		PeakIntegrationSettings peakIntegrationSettings = PreferenceSupplier.getPeakIntegrationSettings();
		return integrate(peaks, peakIntegrationSettings, monitor);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(chromatogramSelection, peakIntegrationSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(peakIntegrationSettings instanceof PeakIntegrationSettings) {
				try {
					PeakIntegratorSupport firstDerivativePeakIntegratorSupport = new PeakIntegratorSupport();
					IPeakIntegrationResults peakIntegrationResults = firstDerivativePeakIntegratorSupport.calculatePeakIntegrationResults(chromatogramSelection, (PeakIntegrationSettings)peakIntegrationSettings, monitor);
					processingInfo.setProcessingResult(peakIntegrationResults);
				} catch(ValueMustNotBeNullException e) {
					logger.warn(e);
				}
			}
		}
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		PeakIntegrationSettings peakIntegrationSettings = PreferenceSupplier.getPeakIntegrationSettings();
		return integrate(chromatogramSelection, peakIntegrationSettings, monitor);
	}
}
