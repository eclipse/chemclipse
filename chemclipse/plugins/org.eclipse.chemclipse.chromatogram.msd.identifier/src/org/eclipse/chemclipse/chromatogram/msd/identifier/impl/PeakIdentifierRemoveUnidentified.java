/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.AbstractPeakIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.PeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifierRemoveUnidentified extends AbstractPeakIdentifier {

	@Override
	public IPeakIdentifierProcessingInfo identify(List<IPeakMSD> peaks, IPeakIdentifierSettings peakIdentifierSettings, IProgressMonitor monitor) {

		IPeakIdentifierProcessingInfo processingInfo = new PeakIdentifierProcessingInfo();
		/*
		 * Remove all unidentified peaks.
		 */
		try {
			for(IPeakMSD peak : peaks) {
				if(peak instanceof IChromatogramPeakMSD) {
					IChromatogramPeakMSD chromatogramPeakMSD = (IChromatogramPeakMSD)peak;
					IChromatogramMSD chromatogramMSD = chromatogramPeakMSD.getChromatogram();
					if(chromatogramMSD != null) {
						if(chromatogramPeakMSD.getTargets().size() == 0) {
							chromatogramMSD.removePeak(chromatogramPeakMSD);
						}
					}
				}
			}
			processingInfo.addInfoMessage("Identifier", "Done - unidentified peaks have been removed.");
		} catch(Exception e) {
			processingInfo.addErrorMessage("Identifier", "Something has gone wrong.");
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

		IPeakIdentifierSettings peakIdentifierSettings = new PeakIdentifierSettings();
		return identify(peak, peakIdentifierSettings, monitor);
	}

	@Override
	public IPeakIdentifierProcessingInfo identify(List<IPeakMSD> peaks, IProgressMonitor monitor) {

		IPeakIdentifierSettings peakIdentifierSettings = new PeakIdentifierSettings();
		return identify(peaks, peakIdentifierSettings, monitor);
	}
}
