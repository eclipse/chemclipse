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
package org.eclipse.chemclipse.chromatogram.msd.identifier.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.AbstractPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifierRemoveUnidentified extends AbstractPeakIdentifierMSD {

	@Override
	public IProcessingInfo identify(List<IPeakMSD> peaks, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
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
	public IProcessingInfo identify(IPeakMSD peak, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		peaks.add(peak);
		return identify(peaks, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(IPeakMSD peak, IProgressMonitor monitor) {

		IPeakIdentifierSettingsMSD peakIdentifierSettings = new PeakIdentifierSettings();
		return identify(peak, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(List<IPeakMSD> peaks, IProgressMonitor monitor) {

		PeakIdentifierSettings peakIdentifierSettings = new PeakIdentifierSettings();
		return identify(peaks, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(IChromatogramSelectionMSD chromatogramSelectionMSD, IProgressMonitor monitor) {

		PeakIdentifierSettings peakIdentifierSettings = new PeakIdentifierSettings();
		return identify(chromatogramSelectionMSD, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(IChromatogramSelectionMSD chromatogramSelectionMSD, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {

		IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		for(IChromatogramPeakMSD chromatogramPeakMSD : chromatogramMSD.getPeaks(chromatogramSelectionMSD)) {
			peaks.add(chromatogramPeakMSD);
		}
		return identify(peaks, peakIdentifierSettings, monitor);
	}
}
