/*******************************************************************************
 * Copyright (c) 2011, 2018, 2019 Lablicate GmbH.
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
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.ui.internal.handler;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.notifier.IntegrationResultUpdateNotifier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramIntegratorRunnable implements IRunnableWithProgress {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ChromatogramIntegratorRunnable.class);
	private static final String CHROMATOGRAM_INTEGRATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.chromatogramIntegrator";
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;

	@SuppressWarnings("rawtypes")
	public ChromatogramIntegratorRunnable(IChromatogramSelection chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Sumarea Integrator", IProgressMonitor.UNKNOWN);
			//
			IProcessingInfo<IChromatogramIntegrationResults> processingInfo = ChromatogramIntegrator.integrate(chromatogramSelection, CHROMATOGRAM_INTEGRATOR_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			IChromatogramIntegrationResults chromatogramIntegrationResults = processingInfo.getProcessingResult();
			IntegrationResultUpdateNotifier.fireUpdateChange(chromatogramIntegrationResults);

		} finally {
			monitor.done();
		}
	}
}
