/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - remove default methods, fix generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.core;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.AbstractPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.AlkaneIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.PeakIdentifierAlkaneSettings;
import org.eclipse.chemclipse.model.identifier.IIdentificationResults;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifier extends AbstractPeakIdentifierMSD<IIdentificationResults> {

	@Override
	public IProcessingInfo<IIdentificationResults> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {

		ProcessingInfo<IIdentificationResults> processingInfo = new ProcessingInfo<>();
		try {
			AlkaneIdentifier alkaneIdentifier = new AlkaneIdentifier();
			PeakIdentifierAlkaneSettings alkaneSettings;
			if(peakIdentifierSettings instanceof PeakIdentifierAlkaneSettings) {
				alkaneSettings = (PeakIdentifierAlkaneSettings)peakIdentifierSettings;
			} else {
				alkaneSettings = PreferenceSupplier.getPeakIdentifierAlkaneSettings();
			}
			/*
			 * Run the identifier.
			 */
			processingInfo.addMessages(alkaneIdentifier.runPeakIdentification(peaks, alkaneSettings, monitor));
			//
		} catch(FileNotFoundException e) {
			processingInfo.addErrorMessage("Alkane Identifier", "Some has gone wrong.", e);
		}
		//
		return processingInfo;
	}
}
