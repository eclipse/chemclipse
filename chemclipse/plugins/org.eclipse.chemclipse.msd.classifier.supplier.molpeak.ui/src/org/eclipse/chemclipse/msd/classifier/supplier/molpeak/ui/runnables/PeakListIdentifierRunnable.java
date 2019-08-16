/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.ui.runnables;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class PeakListIdentifierRunnable implements IRunnableWithProgress {

	private static final String DESCRIPTION = "MolPeak List Identifier";
	private static final String IDENTIFIER_ID = "org.eclipse.chemclipse.msd.identifier.supplier.basepeak.peak";
	private IChromatogramSelectionMSD chromatogramSelection;

	public PeakListIdentifierRunnable(IChromatogramSelectionMSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			if(chromatogramSelection != null && chromatogramSelection.getChromatogramMSD() != null) {
				IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
				List<IChromatogramPeakMSD> peaks = chromatogram.getPeaks(chromatogramSelection);
				/*
				 * TODO make generic
				 * May use a better generic supertype, e.g <? extends IPeak>???
				 */
				List<IPeakMSD> peakList = new ArrayList<IPeakMSD>();
				for(IChromatogramPeakMSD chromatogramPeak : peaks) {
					peakList.add(chromatogramPeak);
				}
				IProcessingInfo processingInfo = PeakIdentifierMSD.identify(peakList, IDENTIFIER_ID, monitor);
				/*
				 * Update the chromatogram selection.
				 */
				ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
				if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
					((ChromatogramSelectionMSD)chromatogramSelection).update(false);
				}
			}
		} finally {
			monitor.done();
		}
	}
}
