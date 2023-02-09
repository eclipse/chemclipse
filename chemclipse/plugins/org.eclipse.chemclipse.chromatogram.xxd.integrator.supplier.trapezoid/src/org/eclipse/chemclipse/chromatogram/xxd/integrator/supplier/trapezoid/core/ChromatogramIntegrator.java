/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.core;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.AbstractChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support.ChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramIntegrator extends AbstractChromatogramIntegrator<IChromatogramIntegrationResults> {

	@Override
	public IProcessingInfo<IChromatogramIntegrationResults> integrate(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramIntegrationSettings chromatogramIntegrationSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramIntegrationResults> processingInfo = super.validate(chromatogramSelection, chromatogramIntegrationSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramIntegrationSettings instanceof ChromatogramIntegrationSettings settings) {
				ChromatogramIntegratorSupport chromatogramIntegratorSupport = new ChromatogramIntegratorSupport();
				IChromatogramIntegrationResults chromatogramIntegrationResults = chromatogramIntegratorSupport.calculateChromatogramIntegrationResults(chromatogramSelection, settings, monitor);
				processingInfo.setProcessingResult(chromatogramIntegrationResults);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramIntegrationResults> integrate(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		ChromatogramIntegrationSettings chromatogramIntegrationSettings = PreferenceSupplier.getChromatogramIntegrationSettings();
		return integrate(chromatogramSelection, chromatogramIntegrationSettings, monitor);
	}
}
