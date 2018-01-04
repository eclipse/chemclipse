/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.support.IPeakMaxPeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.support.PeakMaxPeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.AbstractPeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.IPeakIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.PeakIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.PeakIntegrationResults;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class PeakIntegrator extends AbstractPeakIntegrator {

	private static final Logger logger = Logger.getLogger(PeakIntegrator.class);

	@Override
	public IPeakIntegratorProcessingInfo integrate(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) {

		IPeakIntegratorProcessingInfo processingInfo = new PeakIntegratorProcessingInfo();
		try {
			super.validate(peak, peakIntegrationSettings);
			if(peak instanceof IPeakMSD) {
				IPeakMSD peakMSD = (IPeakMSD)peak;
				IPeakMaxPeakIntegratorSupport peakMaxPeakIntegratorSupport = new PeakMaxPeakIntegratorSupport();
				IPeakIntegrationResult peakIntegrationResult = peakMaxPeakIntegratorSupport.calculatePeakIntegrationResult(peakMSD, peakIntegrationSettings, monitor);
				IPeakIntegrationResults peakIntegrationResults = new PeakIntegrationResults();
				peakIntegrationResults.add(peakIntegrationResult);
				processingInfo.setPeakIntegrationResults(peakIntegrationResults);
			} else {
				addIntegratorExceptionInfo(processingInfo);
			}
		} catch(ValueMustNotBeNullException e) {
			logger.warn(e);
			addIntegratorExceptionInfo(processingInfo);
		}
		return processingInfo;
	}

	@Override
	public IPeakIntegratorProcessingInfo integrate(IPeak peak, IProgressMonitor monitor) {

		IPeakIntegrationSettings peakIntegrationSettings = PreferenceSupplier.getPeakIntegrationSettings();
		return integrate(peak, peakIntegrationSettings, monitor);
	}

	@Override
	public IPeakIntegratorProcessingInfo integrate(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) {

		IPeakIntegratorProcessingInfo processingInfo = new PeakIntegratorProcessingInfo();
		try {
			super.validate(peaks, peakIntegrationSettings);
			IPeakMaxPeakIntegratorSupport peakMaxPeakIntegratorSupport = new PeakMaxPeakIntegratorSupport();
			//
			List<IPeakMSD> peaksMSD = new ArrayList<IPeakMSD>();
			for(IPeak peak : peaks) {
				if(peak instanceof IPeakMSD) {
					IPeakMSD peakMSD = (IPeakMSD)peak;
					peaksMSD.add(peakMSD);
				}
			}
			//
			IPeakIntegrationResults peakIntegrationResults = peakMaxPeakIntegratorSupport.calculatePeakIntegrationResults(peaksMSD, peakIntegrationSettings, monitor);
			processingInfo.setPeakIntegrationResults(peakIntegrationResults);
		} catch(ValueMustNotBeNullException e) {
			logger.warn(e);
			addIntegratorExceptionInfo(processingInfo);
		}
		return processingInfo;
	}

	@Override
	public IPeakIntegratorProcessingInfo integrate(List<? extends IPeak> peaks, IProgressMonitor monitor) {

		IPeakIntegrationSettings peakIntegrationSettings = PreferenceSupplier.getPeakIntegrationSettings();
		return integrate(peaks, peakIntegrationSettings, monitor);
	}

	@Override
	public IPeakIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) {

		IPeakIntegratorProcessingInfo processingInfo = new PeakIntegratorProcessingInfo();
		try {
			super.validate(chromatogramSelection, peakIntegrationSettings);
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				IPeakMaxPeakIntegratorSupport peakMaxPeakIntegratorSupport = new PeakMaxPeakIntegratorSupport();
				IPeakIntegrationResults peakIntegrationResults = peakMaxPeakIntegratorSupport.calculatePeakIntegrationResults((IChromatogramSelectionMSD)chromatogramSelection, peakIntegrationSettings, monitor);
				processingInfo.setPeakIntegrationResults(peakIntegrationResults);
			} else {
				addIntegratorExceptionInfo(processingInfo);
			}
		} catch(ValueMustNotBeNullException e) {
			logger.warn(e);
			addIntegratorExceptionInfo(processingInfo);
		}
		return processingInfo;
	}

	@Override
	public IPeakIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IPeakIntegrationSettings peakIntegrationSettings = PreferenceSupplier.getPeakIntegrationSettings();
		return integrate(chromatogramSelection, peakIntegrationSettings, monitor);
	}

	private void addIntegratorExceptionInfo(IProcessingInfo processingInfo) {

		processingInfo.addErrorMessage("Peak Max Integrator", "The peak(s) or settings couldn't be validated correctly.");
	}
}
