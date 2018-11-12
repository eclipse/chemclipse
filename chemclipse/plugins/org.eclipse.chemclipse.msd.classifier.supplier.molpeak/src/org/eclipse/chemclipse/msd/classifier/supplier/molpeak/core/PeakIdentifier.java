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
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.AbstractPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier.BasePeakIdentifier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifier extends AbstractPeakIdentifierMSD {

	@Override
	public IProcessingInfo identify(List<IPeakMSD> peaks, IPeakIdentifierSettingsMSD identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		//
		if(identifierSettings instanceof PeakIdentifierSettings) {
			PeakIdentifierSettings peakIdentifierSettings = (PeakIdentifierSettings)identifierSettings;
			BasePeakIdentifier basePeakIdentifier = new BasePeakIdentifier();
			basePeakIdentifier.identifyPeaks(peaks, peakIdentifierSettings, monitor);
			processingInfo.addInfoMessage("BasePeakIdentifier", "Everything is supi.");
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo identify(IPeakMSD peak, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		peaks.add(peak);
		return identify(peaks, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(List<IPeakMSD> peaks, IProgressMonitor monitor) {

		PeakIdentifierSettings peakIdentifierSettings = PreferenceSupplier.getPeakIdentifierSettings();
		return identify(peaks, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(IPeakMSD peak, IProgressMonitor monitor) {

		PeakIdentifierSettings peakIdentifierSettings = PreferenceSupplier.getPeakIdentifierSettings();
		return identify(peak, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo identify(IChromatogramSelectionMSD chromatogramSelectionMSD, IProgressMonitor monitor) {

		IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		for(IChromatogramPeakMSD chromatogramPeakMSD : chromatogramMSD.getPeaks(chromatogramSelectionMSD)) {
			peaks.add(chromatogramPeakMSD);
		}
		return identify(peaks, monitor);
	}
}
