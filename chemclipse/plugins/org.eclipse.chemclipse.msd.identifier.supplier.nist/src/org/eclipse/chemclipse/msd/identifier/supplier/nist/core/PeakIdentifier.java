/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.AbstractPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support.Identifier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifier extends AbstractPeakIdentifierMSD<IPeakIdentificationResults> {

	private static final String DESCRIPTION = "NIST-DB Identifier";

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IPeakIdentificationResults> processingInfo = new ProcessingInfo<>();
		if(identifierSettings instanceof PeakIdentifierSettings) {
			try {
				PeakIdentifierSettings peakIdentifierSettings = (PeakIdentifierSettings)identifierSettings;
				Identifier identifier = new Identifier();
				IPeakIdentificationResults peakIdentificationResults = identifier.runPeakIdentification(peaks, peakIdentifierSettings, processingInfo, monitor);
				processingInfo.setProcessingResult(peakIdentificationResults);
			} catch(FileNotFoundException e) {
				processingInfo.addErrorMessage(DESCRIPTION, "An I/O error ocurred.");
			}
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The settings are not of type: " + PeakIdentifierSettings.class);
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(IPeakMSD peak, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {

		List<IPeakMSD> peaks = new ArrayList<>();
		peaks.add(peak);
		return identify(peaks, peakIdentifierSettings, monitor);
	}

	// TODO JUnit
	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakMSD> peaks, IProgressMonitor monitor) {

		PeakIdentifierSettings peakIdentifierSettings = PreferenceSupplier.getPeakIdentifierSettings();
		return identify(peaks, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(IPeakMSD peak, IProgressMonitor monitor) {

		PeakIdentifierSettings peakIdentifierSettings = PreferenceSupplier.getPeakIdentifierSettings();
		return identify(peak, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(IChromatogramSelectionMSD chromatogramSelectionMSD, IProgressMonitor monitor) {

		PeakIdentifierSettings peakIdentifierSettings = PreferenceSupplier.getPeakIdentifierSettings();
		return identify(chromatogramSelectionMSD, peakIdentifierSettings, monitor);
	}

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(IChromatogramSelectionMSD chromatogramSelectionMSD, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {

		IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogram();
		List<IPeakMSD> peaks = new ArrayList<>();
		for(IChromatogramPeakMSD chromatogramPeakMSD : chromatogramMSD.getPeaks(chromatogramSelectionMSD)) {
			peaks.add(chromatogramPeakMSD);
		}
		return identify(peaks, peakIdentifierSettings, monitor);
	}
}
