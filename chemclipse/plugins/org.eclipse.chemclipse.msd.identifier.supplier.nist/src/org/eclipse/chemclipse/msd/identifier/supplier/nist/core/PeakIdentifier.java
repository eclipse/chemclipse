/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - remove default methods
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.core;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.AbstractPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support.Identifier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.INistSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifier extends AbstractPeakIdentifierMSD<IPeakIdentificationResults> {

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IPeakIdentificationResults> processingInfo = new ProcessingInfo<>();
		//
		if(identifierSettings == null) {
			identifierSettings = PreferenceSupplier.getPeakIdentifierSettings();
		}
		//
		if(identifierSettings instanceof PeakIdentifierSettings) {
			try {
				PeakIdentifierSettings peakIdentifierSettings = (PeakIdentifierSettings)identifierSettings;
				Identifier identifier = new Identifier();
				IPeakIdentificationResults peakIdentificationResults = identifier.runPeakIdentification(peaks, peakIdentifierSettings, processingInfo, monitor);
				processingInfo.setProcessingResult(peakIdentificationResults);
			} catch(FileNotFoundException e) {
				processingInfo.addErrorMessage(INistSupport.NIST_DESCRIPTION, "An I/O error ocurred.");
			}
		} else {
			processingInfo.addErrorMessage(INistSupport.NIST_DESCRIPTION, "The settings are not of type: " + PeakIdentifierSettings.class);
		}
		//
		return processingInfo;
	}
}
