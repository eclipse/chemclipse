/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.ui.internal.handler;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.IChromatogramIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.notifier.IntegrationResultUpdateNotifier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class ChromatogramIntegratorRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ChromatogramIntegratorRunnable.class);
	private static final String CHROMATOGRAM_INTEGRATOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.chromatogramIntegrator";
	private IChromatogramSelection chromatogramSelection;

	public ChromatogramIntegratorRunnable(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Integrator Trapezoid", IProgressMonitor.UNKNOWN);
			/*
			 * Detect Peaks in actual chromatogram selection.
			 */
			IChromatogramIntegrationSettings chromatogramIntegrationSettings = new ChromatogramIntegrationSettings();
			/*
			 * Show the processing view if error messages occurred.
			 */
			final IChromatogramIntegratorProcessingInfo processingInfo = ChromatogramIntegrator.integrate(chromatogramSelection, chromatogramIntegrationSettings, CHROMATOGRAM_INTEGRATOR_ID, monitor);
			/*
			 * asyncExec
			 */
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					/*
					 * Show the processing view if error messages occurred.
					 */
					ProcessingInfoViewSupport.showErrorInfoReminder(processingInfo);
					ProcessingInfoViewSupport.updateProcessingInfoView(processingInfo);
				}
			});
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
