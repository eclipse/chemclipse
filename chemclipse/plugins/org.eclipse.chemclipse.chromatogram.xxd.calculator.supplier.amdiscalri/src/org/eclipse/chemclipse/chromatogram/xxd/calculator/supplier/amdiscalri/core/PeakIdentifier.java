/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.AbstractPeakIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.PeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.AlkaneIdentifier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifier extends AbstractPeakIdentifier {

	private static final Logger logger = Logger.getLogger(PeakIdentifier.class);

	@Override
	public IPeakIdentifierProcessingInfo identify(List<IPeakMSD> peaks, IPeakIdentifierSettings peakIdentifierSettings, IProgressMonitor monitor) {

		IPeakIdentifierProcessingInfo processingInfo;
		AlkaneIdentifier alkaneIdentifier = new AlkaneIdentifier();
		try {
			processingInfo = alkaneIdentifier.runPeakIdentification(peaks, monitor);
		} catch(FileNotFoundException e) {
			logger.warn(e);
			processingInfo = new PeakIdentifierProcessingInfo();
			processingInfo.addErrorMessage("Alkane Identifier", "Some has gone wrong.");
		}
		return processingInfo;
	}

	@Override
	public IPeakIdentifierProcessingInfo identify(IPeakMSD peak, IPeakIdentifierSettings peakIdentifierSettings, IProgressMonitor monitor) {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		peaks.add(peak);
		return identify(peaks, peakIdentifierSettings, monitor);
	}

	@Override
	public IPeakIdentifierProcessingInfo identify(IPeakMSD peak, IProgressMonitor monitor) {

		return identify(peak, null, monitor);
	}

	@Override
	public IPeakIdentifierProcessingInfo identify(List<IPeakMSD> peaks, IProgressMonitor monitor) {

		return identify(peaks, null, monitor);
	}

	@Override
	public IPeakIdentifierProcessingInfo identify(IChromatogramSelectionMSD chromatogramSelectionMSD, IProgressMonitor monitor) {

		IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		for(IChromatogramPeakMSD chromatogramPeakMSD : chromatogramMSD.getPeaks(chromatogramSelectionMSD)) {
			peaks.add(chromatogramPeakMSD);
		}
		return identify(peaks, monitor);
	}
}
