/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.runnables;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class MassSpectraIdentifierRunnable implements IRunnableWithProgress {

	private static final String DESCRIPTION = "File Mass Spectrum Identifier";
	private static final String IDENTIFIER_ID = "org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.massSpectrum";
	private IChromatogramSelectionMSD chromatogramSelection;

	public MassSpectraIdentifierRunnable(IChromatogramSelectionMSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			//
			List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
			IChromatogramMSD chromtogramMSD = chromatogramSelection.getChromatogramMSD();
			int startScan = chromtogramMSD.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromtogramMSD.getScanNumber(chromatogramSelection.getStopRetentionTime());
			for(int i = startScan; i <= stopScan; i++) {
				IScan scan = chromtogramMSD.getScan(i);
				if(scan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)scan;
					if(scanMSD.getTargets().size() > 0) {
						massSpectra.add(scanMSD);
					}
				}
			}
			//
			IProcessingInfo processingInfo = MassSpectrumIdentifier.identify(massSpectra, IDENTIFIER_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
		} finally {
			monitor.done();
		}
	}
}
