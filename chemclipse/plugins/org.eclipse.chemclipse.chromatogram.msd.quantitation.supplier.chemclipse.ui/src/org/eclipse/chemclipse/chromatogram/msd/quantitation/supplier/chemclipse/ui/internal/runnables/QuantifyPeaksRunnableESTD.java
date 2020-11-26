/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.PeakQuantifier;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class QuantifyPeaksRunnableESTD implements IRunnableWithProgress {

	private IChromatogramSelectionMSD chromatogramSelection;
	private static final String PEAK_QUANTIFIER_ID = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.peak";

	public QuantifyPeaksRunnableESTD(IChromatogramSelectionMSD chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Peak List Quantifier (ESTD)", IProgressMonitor.UNKNOWN);
			//
			List<IPeak> peaks = new ArrayList<IPeak>();
			for(IChromatogramPeakMSD peak : chromatogramSelection.getChromatogram().getPeaks()) {
				peaks.add(peak);
			}
			IProcessingInfo<?> processingInfo = PeakQuantifier.quantify(peaks, PEAK_QUANTIFIER_ID, monitor);
			ProcessingInfoPartSupport.getInstance().update(processingInfo, true);
			if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
				((ChromatogramSelectionMSD)chromatogramSelection).update(true);
			}
		} finally {
			monitor.done();
		}
	}
}
