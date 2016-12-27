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
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.ui.internal.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings.ISumareaIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings.SumareaIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.IChromatogramIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.notifier.IntegrationResultUpdateNotifier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramIntegratorRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ChromatogramIntegratorRunnable.class);
	private static final String CHROMATOGRAM_INTEGRATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.chromatogramIntegrator";
	private IChromatogramSelection chromatogramSelection;

	public ChromatogramIntegratorRunnable(IChromatogramSelection chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Sumarea Integrator", IProgressMonitor.UNKNOWN);
			/*
			 * Integrate the chromatogram selection.
			 */
			ISumareaIntegrationSettings chromatogramIntegrationSettings = new SumareaIntegrationSettings();
			IMarkedIons selectedIons = chromatogramIntegrationSettings.getSelectedIons();
			Set<Integer> ions = PreferenceSupplier.getIons(PreferenceSupplier.P_SELECTED_IONS, PreferenceSupplier.DEF_SELECTED_IONS);
			PreferenceSupplier.setMarkedIons(selectedIons, ions);
			/*
			 * Result
			 */
			IChromatogramIntegratorProcessingInfo processingInfo = ChromatogramIntegrator.integrate(chromatogramSelection, chromatogramIntegrationSettings, CHROMATOGRAM_INTEGRATOR_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			/*
			 * Try to set the results.
			 */
			try {
				IChromatogramIntegrationResults chromatogramIntegrationResults = processingInfo.getChromatogramIntegrationResults();
				IntegrationResultUpdateNotifier.fireUpdateChange(chromatogramIntegrationResults);
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}
}
