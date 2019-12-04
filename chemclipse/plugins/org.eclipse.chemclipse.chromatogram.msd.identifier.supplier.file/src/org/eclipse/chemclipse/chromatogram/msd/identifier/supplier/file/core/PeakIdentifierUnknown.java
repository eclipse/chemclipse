/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.internal.identifier.UnknownIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.PeakUnknownSettings;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PeakIdentificationResults;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifierUnknown implements IPeakIdentifierMSD<IPeakIdentificationResults> {

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD identifierSettings, IProgressMonitor monitor) {

		if(identifierSettings == null) {
			identifierSettings = PreferenceSupplier.getPeakUnknownSettings();
		}
		//
		IProcessingInfo<IPeakIdentificationResults> processingInfo = new ProcessingInfo<>();
		if(identifierSettings instanceof PeakUnknownSettings) {
			PeakUnknownSettings unknownIdentifierSettings = (PeakUnknownSettings)identifierSettings;
			UnknownIdentifier unknownIdentifier = new UnknownIdentifier();
			unknownIdentifier.runIdentification(peaks, unknownIdentifierSettings);
			IPeakIdentificationResults peakIdentificationResults = new PeakIdentificationResults();
			processingInfo.setProcessingResult(peakIdentificationResults);
			processingInfo.addInfoMessage(UnknownIdentifier.IDENTIFIER, "Done - peaks have been identified.");
		} else {
			processingInfo.addErrorMessage(UnknownIdentifier.IDENTIFIER, "The settings are not of type: " + PeakUnknownSettings.class);
		}
		return processingInfo;
	}
}
