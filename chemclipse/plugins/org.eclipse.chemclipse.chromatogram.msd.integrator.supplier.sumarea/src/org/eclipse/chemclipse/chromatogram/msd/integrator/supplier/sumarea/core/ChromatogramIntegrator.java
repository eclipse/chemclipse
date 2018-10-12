/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.support.ChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.AbstractChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramIntegrator extends AbstractChromatogramIntegrator {

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IChromatogramIntegrationSettings chromatogramIntegrationSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(chromatogramSelection, chromatogramIntegrationSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramSelection instanceof IChromatogramSelectionMSD && chromatogramIntegrationSettings instanceof ChromatogramIntegrationSettings) {
				IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				ChromatogramIntegrationSettings settings = (ChromatogramIntegrationSettings)chromatogramIntegrationSettings;
				ChromatogramIntegratorSupport chromatogramIntegratorSupport = new ChromatogramIntegratorSupport();
				IChromatogramIntegrationResults chromatogramIntegrationResults = chromatogramIntegratorSupport.calculateChromatogramIntegrationResults(chromatogramSelectionMSD, settings, monitor);
				processingInfo.setProcessingResult(chromatogramIntegrationResults);
			} else {
				processingInfo.addErrorMessage("Sumarea Integrator", "The settings and/or chromatogram are not of type MSD.");
			}
		}
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramIntegrationSettings chromatogramIntegrationSettings = PreferenceSupplier.getIntegrationSettings();
		return integrate(chromatogramSelection, chromatogramIntegrationSettings, monitor);
	}
}
