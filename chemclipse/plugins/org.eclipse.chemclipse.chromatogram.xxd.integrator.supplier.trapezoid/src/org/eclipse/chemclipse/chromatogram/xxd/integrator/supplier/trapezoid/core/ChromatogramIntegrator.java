/*******************************************************************************
 * Copyright (c) 2011, 2016 Lablicate GmbH.
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

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.AbstractChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.ChromatogramIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.IChromatogramIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.TrapezoidChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.ITrapezoidChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class ChromatogramIntegrator extends AbstractChromatogramIntegrator {

	private static final Logger logger = Logger.getLogger(ChromatogramIntegrator.class);

	@Override
	public IChromatogramIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IChromatogramIntegrationSettings chromatogramIntegrationSettings, IProgressMonitor monitor) {

		/*
		 * Chromatogram Integration Results.
		 */
		IChromatogramIntegratorProcessingInfo processingInfo = new ChromatogramIntegratorProcessingInfo();
		try {
			super.validate(chromatogramSelection, chromatogramIntegrationSettings);
			ITrapezoidChromatogramIntegratorSupport firstDerivativeChromatogramIntegratorSupport = new TrapezoidChromatogramIntegratorSupport();
			IChromatogramIntegrationResults chromatogramIntegrationResults = firstDerivativeChromatogramIntegratorSupport.calculateChromatogramIntegrationResults(chromatogramSelection, chromatogramIntegrationSettings, monitor);
			processingInfo.setChromatogramIntegrationResults(chromatogramIntegrationResults);
		} catch(ValueMustNotBeNullException e) {
			logger.warn(e);
			addIntegratorExceptionInfo(processingInfo);
		}
		return processingInfo;
	}

	@Override
	public IChromatogramIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramIntegrationSettings chromatogramIntegrationSettings = PreferenceSupplier.getChromatogramIntegrationSettings();
		return integrate(chromatogramSelection, chromatogramIntegrationSettings, monitor);
	}

	private void addIntegratorExceptionInfo(IProcessingInfo processingInfo) {

		processingInfo.addErrorMessage("Chromatogram Integrator Trapezoid", "The peak(s) or settings couldn't be validated correctly.");
	}
}
