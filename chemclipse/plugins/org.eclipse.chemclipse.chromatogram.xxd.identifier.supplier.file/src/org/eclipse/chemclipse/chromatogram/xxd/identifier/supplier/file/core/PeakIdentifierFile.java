/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich, remove default implemented methods, adjust generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.core;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.internal.identifier.FileIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifierFile implements IPeakIdentifierMSD<IPeakIdentificationResults> {

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD identifierSettings, IProgressMonitor monitor) {

		if(identifierSettings == null) {
			identifierSettings = PreferenceSupplier.getPeakIdentifierSettings();
		}
		IProcessingInfo<IPeakIdentificationResults> processingInfo = new ProcessingInfo<>();
		if(identifierSettings instanceof PeakIdentifierSettings peakIdentifierSettings) {
			try {
				FileIdentifier fileIdentifier = new FileIdentifier();
				IPeakIdentificationResults peakIdentificationResults = fileIdentifier.runPeakIdentification(peaks, peakIdentifierSettings, processingInfo, monitor);
				processingInfo.setProcessingResult(peakIdentificationResults);
				int results = peakIdentificationResults.getIdentificationResults().size();
				processingInfo.addInfoMessage(FileIdentifier.IDENTIFIER, MessageFormat.format("{0} peaks have been identified.", results));
			} catch(FileNotFoundException e) {
				processingInfo.addErrorMessage(FileIdentifier.IDENTIFIER, "An I/O error ocurred.");
			}
		} else {
			processingInfo.addErrorMessage(FileIdentifier.IDENTIFIER, "The settings are not of type: " + PeakIdentifierSettings.class);
		}
		return processingInfo;
	}
}
