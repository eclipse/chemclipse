/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class IdentifyAlkanesRunnable implements IRunnableWithProgress {

	private static final String DESCRIPTION = "Alkane Identifier";
	private static final String IDENTIFIER_ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.peak";
	private IChromatogramSelectionMSD chromatogramSelection;

	public IdentifyAlkanesRunnable(IChromatogramSelectionMSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			//
			List<IChromatogramPeakMSD> peaks = chromatogramSelection.getChromatogram().getPeaks(chromatogramSelection);
			List<IPeakMSD> peakList = new ArrayList<IPeakMSD>();
			for(IChromatogramPeakMSD chromatogramPeak : peaks) {
				peakList.add(chromatogramPeak);
			}
			IProcessingInfo<?> processingInfo = PeakIdentifierMSD.identify(peakList, IDENTIFIER_ID, monitor);
			/*
			 * Update the chromatogram selection.
			 */
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
				((ChromatogramSelectionMSD)chromatogramSelection).update(false);
			}
		} finally {
			monitor.done();
		}
	}
}
