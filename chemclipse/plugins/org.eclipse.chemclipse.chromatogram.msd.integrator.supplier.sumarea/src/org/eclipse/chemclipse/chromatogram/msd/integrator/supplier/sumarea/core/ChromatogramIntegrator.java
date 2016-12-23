/*******************************************************************************
 * Copyright (c) 2011, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.support.ISumareaChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.support.SumareaChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.AbstractChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.ChromatogramIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.IChromatogramIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class ChromatogramIntegrator extends AbstractChromatogramIntegrator {

	private static final Logger logger = Logger.getLogger(ChromatogramIntegrator.class);

	@Override
	public IChromatogramIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IChromatogramIntegrationSettings chromatogramIntegrationSettings, IProgressMonitor monitor) {

		IChromatogramIntegratorProcessingInfo processingInfo = new ChromatogramIntegratorProcessingInfo();
		try {
			super.validate(chromatogramSelection, chromatogramIntegrationSettings);
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				ISumareaChromatogramIntegratorSupport sumareaChromatogramIntegratorSupport = new SumareaChromatogramIntegratorSupport();
				IChromatogramIntegrationResults chromatogramIntegrationResults = sumareaChromatogramIntegratorSupport.calculateChromatogramIntegrationResults(chromatogramSelectionMSD, chromatogramIntegrationSettings, monitor);
				processingInfo.setChromatogramIntegrationResults(chromatogramIntegrationResults);
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
	public IChromatogramIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramIntegrationSettings chromatogramIntegrationSettings = PreferenceSupplier.getIntegrationSettings();
		return integrate(chromatogramSelection, chromatogramIntegrationSettings, monitor);
	}

	private void addIntegratorExceptionInfo(IProcessingInfo processingInfo) {

		processingInfo.addErrorMessage("Sumarea Integrator", "The peak(s) or settings couldn't be validated correctly.");
	}
}
