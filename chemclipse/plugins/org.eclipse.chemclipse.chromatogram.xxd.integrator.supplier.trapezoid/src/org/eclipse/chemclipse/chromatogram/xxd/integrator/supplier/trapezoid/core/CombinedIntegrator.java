/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.AbstractCombinedIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.CombinedIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.ICombinedIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.CombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.TrapezoidChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.TrapezoidPeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.ITrapezoidChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.ITrapezoidPeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class CombinedIntegrator extends AbstractCombinedIntegrator {

	private static final Logger logger = Logger.getLogger(CombinedIntegrator.class);

	@Override
	public ICombinedIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, ICombinedIntegrationSettings combinedIntegrationSettings, IProgressMonitor monitor) {

		ICombinedIntegratorProcessingInfo processingInfo = new CombinedIntegratorProcessingInfo();
		try {
			validate(chromatogramSelection, combinedIntegrationSettings);
			/*
			 * Peak Integration Results.
			 */
			ITrapezoidPeakIntegratorSupport firstDerivativePeakIntegratorSupport = new TrapezoidPeakIntegratorSupport();
			IPeakIntegrationResults peakIntegrationResults = firstDerivativePeakIntegratorSupport.calculatePeakIntegrationResults(chromatogramSelection, combinedIntegrationSettings.getPeakIntegrationSettings(), monitor);
			/*
			 * Chromatogram Integration Results.
			 */
			ITrapezoidChromatogramIntegratorSupport firstDerivativeChromatogramIntegratorSupport = new TrapezoidChromatogramIntegratorSupport();
			IChromatogramIntegrationResults chromatogramIntegrationResults = firstDerivativeChromatogramIntegratorSupport.calculateChromatogramIntegrationResults(chromatogramSelection, combinedIntegrationSettings.getChromatogramIntegrationSettings(), monitor);
			/*
			 * Build the result and return it.
			 */
			CombinedIntegrationResult combinedIntegrationResult = new CombinedIntegrationResult(chromatogramIntegrationResults, peakIntegrationResults);
			processingInfo.setCombinedIntegrationResult(combinedIntegrationResult);
		} catch(ValueMustNotBeNullException e) {
			logger.warn(e);
			addIntegratorExceptionInfo(processingInfo);
		}
		return processingInfo;
	}

	@Override
	public ICombinedIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		ICombinedIntegrationSettings combinedIntegrationSettings = PreferenceSupplier.getCombinedIntegrationSettings();
		return integrate(chromatogramSelection, combinedIntegrationSettings, monitor);
	}

	private void addIntegratorExceptionInfo(IProcessingInfo processingInfo) {

		processingInfo.addErrorMessage("Combined Integrator Trapezoid", "The peak(s) or settings couldn't be validated correctly.");
	}
}
