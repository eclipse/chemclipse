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
package org.eclipse.chemclipse.chromatogram.csd.identifier.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.AbstractPeakIdentifierCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.IPeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.PeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifierRemoveUnidentified extends AbstractPeakIdentifierCSD {

	@Override
	public IProcessingInfo identify(List<IPeakCSD> peaks, IPeakIdentifierSettingsCSD peakIdentifierSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		/*
		 * Remove all unidentified peaks.
		 */
		try {
			for(IPeakCSD peak : peaks) {
				if(peak instanceof IChromatogramPeakCSD) {
					IChromatogramPeakCSD chromatogramPeakCSD = (IChromatogramPeakCSD)peak;
					IChromatogramCSD chromatogramCSD = chromatogramPeakCSD.getChromatogram();
					if(chromatogramCSD != null) {
						if(chromatogramPeakCSD.getTargets().size() == 0) {
							chromatogramCSD.removePeak(chromatogramPeakCSD);
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
	public IProcessingInfo identify(IPeakCSD peak, IPeakIdentifierSettingsCSD peakIdentifierSettings, IProgressMonitor monitor) {

		List<IPeakCSD> peaks = new ArrayList<IPeakCSD>();
		peaks.add(peak);
		return identify(peaks, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(IPeakCSD peak, IProgressMonitor monitor) {

		IPeakIdentifierSettingsCSD peakIdentifierSettings = new PeakIdentifierSettingsCSD();
		return identify(peak, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(List<IPeakCSD> peaks, IProgressMonitor monitor) {

		IPeakIdentifierSettingsCSD peakIdentifierSettings = new PeakIdentifierSettingsCSD();
		return identify(peaks, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(IChromatogramSelectionCSD chromatogramSelectionCSD, IProgressMonitor monitor) {

		IChromatogramCSD chromatogramCSD = chromatogramSelectionCSD.getChromatogramCSD();
		List<IPeakCSD> peaks = new ArrayList<IPeakCSD>();
		for(IChromatogramPeakCSD chromatogramPeakCSD : chromatogramCSD.getPeaks(chromatogramSelectionCSD)) {
			peaks.add(chromatogramPeakCSD);
		}
		return identify(peaks, monitor);
	}
}
