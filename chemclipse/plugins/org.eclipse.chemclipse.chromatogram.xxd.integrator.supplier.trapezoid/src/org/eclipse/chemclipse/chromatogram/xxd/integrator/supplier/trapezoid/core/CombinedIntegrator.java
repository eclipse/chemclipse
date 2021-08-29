/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.core;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.AbstractCombinedIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.CombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ICombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.ChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.PeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.CombinedIntegrationSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class CombinedIntegrator extends AbstractCombinedIntegrator {

	private static final Logger logger = Logger.getLogger(CombinedIntegrator.class);

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo<ICombinedIntegrationResult> integrate(IChromatogramSelection chromatogramSelection, ICombinedIntegrationSettings integrationSettings, IProgressMonitor monitor) {

		IProcessingInfo<ICombinedIntegrationResult> processingInfo = validate(chromatogramSelection, integrationSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(integrationSettings instanceof CombinedIntegrationSettings) {
				try {
					CombinedIntegrationSettings combinedIntegrationSettings = (CombinedIntegrationSettings)integrationSettings;
					/*
					 * Peak Integration Results.
					 */
					PeakIntegratorSupport peakIntegratorSupport = new PeakIntegratorSupport();
					IPeakIntegrationResults peakIntegrationResults = peakIntegratorSupport.calculatePeakIntegrationResults(chromatogramSelection, combinedIntegrationSettings.getPeakIntegrationSettings(), monitor);
					/*
					 * Chromatogram Integration Results.
					 */
					ChromatogramIntegratorSupport chromatogramIntegratorSupport = new ChromatogramIntegratorSupport();
					IChromatogramIntegrationResults chromatogramIntegrationResults = chromatogramIntegratorSupport.calculateChromatogramIntegrationResults(chromatogramSelection, combinedIntegrationSettings.getChromatogramIntegrationSettings(), monitor);
					/*
					 * Build the result and return it.
					 */
					CombinedIntegrationResult combinedIntegrationResult = new CombinedIntegrationResult(chromatogramIntegrationResults, peakIntegrationResults);
					processingInfo.setProcessingResult(combinedIntegrationResult);
				} catch(ValueMustNotBeNullException e) {
					logger.warn(e);
				}
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<ICombinedIntegrationResult> integrate(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		CombinedIntegrationSettings combinedIntegrationSettings = PreferenceSupplier.getCombinedIntegrationSettings();
		return integrate(chromatogramSelection, combinedIntegrationSettings, monitor);
	}
}
