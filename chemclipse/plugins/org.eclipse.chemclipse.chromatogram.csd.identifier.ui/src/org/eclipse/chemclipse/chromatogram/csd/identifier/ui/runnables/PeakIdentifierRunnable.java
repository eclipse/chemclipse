/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.identifier.ui.runnables;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.PeakIdentifierCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class PeakIdentifierRunnable implements IRunnableWithProgress {

	private static final String DESCRIPTION = "Unidentified Peak Remover";
	private static final String IDENTIFIER_ID = "org.eclipse.chemclipse.chromatogram.csd.identifier.deleteUnidentifiedPeaks";
	private IChromatogramSelectionCSD chromatogramSelection;

	public PeakIdentifierRunnable(IChromatogramSelectionCSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			//
			List<IChromatogramPeakCSD> peaks = chromatogramSelection.getChromatogramCSD().getPeaks(chromatogramSelection);
			List<IPeakCSD> peakList = new ArrayList<IPeakCSD>();
			for(IChromatogramPeakCSD chromatogramPeak : peaks) {
				peakList.add(chromatogramPeak);
			}
			IProcessingInfo processingInfo = PeakIdentifierCSD.identify(peakList, IDENTIFIER_ID, monitor);
			/*
			 * Update the chromatogram selection.
			 */
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
				((ChromatogramSelectionCSD)chromatogramSelection).update(true);
			}
		} finally {
			monitor.done();
		}
	}
}
