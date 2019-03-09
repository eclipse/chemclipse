/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.ui.internal.handler;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.CombinedIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ICombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.notifier.IntegrationResultUpdateNotifier;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class CombinedIntegratorRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(CombinedIntegratorRunnable.class);
	private static final String COMBINED_INTEGRATOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.combinedIntegrator";
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;

	@SuppressWarnings("rawtypes")
	public CombinedIntegratorRunnable(IChromatogramSelection chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Integrator Trapezoid", IProgressMonitor.UNKNOWN);
			/*
			 * Show the processing view if error messages occurred.
			 */
			IProcessingInfo<ICombinedIntegrationResult> processingInfo = CombinedIntegrator.integrate(chromatogramSelection, COMBINED_INTEGRATOR_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			/*
			 * Try to set the results.
			 */
			try {
				ICombinedIntegrationResult combinedIntegrationResult = processingInfo.getProcessingResult(ICombinedIntegrationResult.class);
				IntegrationResultUpdateNotifier.fireUpdateChange(combinedIntegrationResult);
				updateSelection();
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}

	/*
	 * Updates the selection using the GUI thread.
	 */
	private void updateSelection() {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
					((ChromatogramSelectionMSD)chromatogramSelection).update(false);
				} else if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
					((ChromatogramSelectionCSD)chromatogramSelection).update(false);
				}
			}
		});
	}
}
