/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.PeakQuantifier;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class QuantifyPeakRunnableESTD implements IRunnableWithProgress {

	private IChromatogramSelectionMSD chromatogramSelection;
	private static final String PEAK_QUANTIFIER_ID = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.peak";

	public QuantifyPeakRunnableESTD(IChromatogramSelectionMSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Peak Quantifier (ESTD)", IProgressMonitor.UNKNOWN);
			//
			IPeakMSD peak = chromatogramSelection.getSelectedPeak();
			IProcessingInfo processingInfo = PeakQuantifier.quantify(peak, PEAK_QUANTIFIER_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, true);
			if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
				((ChromatogramSelectionMSD)chromatogramSelection).update(true);
			}
		} finally {
			monitor.done();
		}
	}
}
