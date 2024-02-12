/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.core;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.IPeakIdentifierCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.IPeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.wsd.identifier.peak.IPeakIdentifierWSD;
import org.eclipse.chemclipse.chromatogram.wsd.identifier.settings.IPeakIdentifierSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.internal.identifier.UnknownIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.model.UnknownSettingsSupport;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.IUnknownSettings;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PeakIdentificationResults;
import org.eclipse.chemclipse.model.targets.TargetUnknownSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifierUnknown implements IPeakIdentifierMSD<IPeakIdentificationResults>, IPeakIdentifierCSD<IPeakIdentificationResults>, IPeakIdentifierWSD<IPeakIdentificationResults> {

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD identifierSettings, IProgressMonitor monitor) {

		if(identifierSettings == null) {
			identifierSettings = PreferenceSupplier.getPeakUnknownSettingsMSD();
		}
		//
		IUnknownSettings unknownSettings = identifierSettings instanceof IUnknownSettings settings ? settings : null;
		return runIdentification(peaks, unknownSettings);
	}

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakCSD> peaks, IPeakIdentifierSettingsCSD identifierSettings, IProgressMonitor monitor) {

		if(identifierSettings == null) {
			identifierSettings = PreferenceSupplier.getPeakUnknownSettingsCSD();
		}
		//
		IUnknownSettings unknownSettings = identifierSettings instanceof IUnknownSettings settings ? settings : null;
		return runIdentification(peaks, unknownSettings);
	}

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakWSD> peaks, IPeakIdentifierSettingsWSD identifierSettings, IProgressMonitor monitor) {

		if(identifierSettings == null) {
			identifierSettings = PreferenceSupplier.getPeakUnknownSettingsWSD();
		}
		//
		IUnknownSettings unknownSettings = identifierSettings instanceof IUnknownSettings settings ? settings : null;
		return runIdentification(peaks, unknownSettings);
	}

	private IProcessingInfo<IPeakIdentificationResults> runIdentification(List<? extends IPeak> peaks, IUnknownSettings unknownSettings) {

		IProcessingInfo<IPeakIdentificationResults> processingInfo = new ProcessingInfo<>();
		//
		if(unknownSettings != null) {
			TargetUnknownSettings targetUnknownSettings = UnknownSettingsSupport.getTargetUnknownSettings(unknownSettings);
			float limitMatchFactor = unknownSettings.getLimitMatchFactor();
			UnknownIdentifier unknownIdentifier = new UnknownIdentifier();
			unknownIdentifier.runIdentificationPeak(peaks, limitMatchFactor, targetUnknownSettings);
			IPeakIdentificationResults peakIdentificationResults = new PeakIdentificationResults();
			processingInfo.setProcessingResult(peakIdentificationResults);
			int results = peakIdentificationResults.getIdentificationResults().size();
			processingInfo.addInfoMessage(UnknownIdentifier.IDENTIFIER, MessageFormat.format("{0} peaks have been marked as unknown.", results));
		} else {
			processingInfo.addErrorMessage(UnknownIdentifier.IDENTIFIER, "The settings are not of valid.");
		}
		//
		return processingInfo;
	}
}
