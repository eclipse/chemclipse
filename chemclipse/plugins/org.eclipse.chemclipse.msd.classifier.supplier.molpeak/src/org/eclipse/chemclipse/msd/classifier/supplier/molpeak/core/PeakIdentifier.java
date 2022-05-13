/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - remove default implemented methods
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.AbstractPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PeakIdentificationResults;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier.BasePeakIdentifier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifier extends AbstractPeakIdentifierMSD<IPeakIdentificationResults> {

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD identifierSettings, IProgressMonitor monitor) {

		if(identifierSettings == null) {
			identifierSettings = PreferenceSupplier.getPeakIdentifierSettings();
		}
		IProcessingInfo<IPeakIdentificationResults> processingInfo = new ProcessingInfo<>(new PeakIdentificationResults());
		//
		if(identifierSettings instanceof PeakIdentifierSettings) {
			PeakIdentifierSettings peakIdentifierSettings = (PeakIdentifierSettings)identifierSettings;
			BasePeakIdentifier basePeakIdentifier = new BasePeakIdentifier();
			basePeakIdentifier.identifyPeaks(peaks, peakIdentifierSettings, monitor);
			processingInfo.addInfoMessage("BasePeakIdentifier", "Peak(s) have been identified.");
		}
		//
		return processingInfo;
	}
}