/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class CombinedIntegratorRunnable implements IRunnableWithProgress {

	private static final String COMBINED_INTEGRATOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.combinedIntegrator";
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;

	@SuppressWarnings("rawtypes")
	public CombinedIntegratorRunnable(IChromatogramSelection chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			IProcessingInfo<ICombinedIntegrationResult> processingInfo = CombinedIntegrator.integrate(chromatogramSelection, COMBINED_INTEGRATOR_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			chromatogramSelection.update(false);
		} finally {
			monitor.done();
		}
	}
}
