/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.core;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.AbstractPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.internal.identifier.AmdisIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetector extends AbstractPeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetector.class);

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorSettingsMSD peakDetectorSettings, IProgressMonitor monitor) {

		/*
		 * Validate
		 */
		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, peakDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(peakDetectorSettings instanceof PeakDetectorSettings) {
				PeakDetectorSettings amdisSettings = (PeakDetectorSettings)peakDetectorSettings;
				AmdisIdentifier identifier = new AmdisIdentifier();
				try {
					IProcessingResult<Void> result = identifier.calulateAndSetDeconvolutedPeaks(chromatogramSelection, amdisSettings, monitor);
					if(!result.hasErrorMessages()) {
						processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "AMDIS Peak Detector", "Peaks have been detected successfully."));
					}
					for(IProcessingMessage message : result.getMessages()) {
						processingInfo.addMessage(message);
					}
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				}
			} else {
				logger.warn("The settings is not of type: " + PeakDetectorSettings.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		PeakDetectorSettings peakDetectorSettings = PreferenceSupplier.getPeakDetectorSettings();
		return detect(chromatogramSelection, peakDetectorSettings, monitor);
	}
}
