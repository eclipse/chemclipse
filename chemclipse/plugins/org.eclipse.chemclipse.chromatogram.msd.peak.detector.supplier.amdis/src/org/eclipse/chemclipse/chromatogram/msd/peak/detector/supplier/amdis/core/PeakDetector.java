/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.processing.IPeakDetectorMSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.processing.PeakDetectorMSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.internal.identifier.AmdisIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.IAmdisSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetector extends AbstractPeakDetectorMSD {

	@Override
	public IPeakDetectorMSDProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorMSDSettings peakDetectorSettings, IProgressMonitor monitor) {

		/*
		 * Validate
		 */
		IPeakDetectorMSDProcessingInfo processingInfo = new PeakDetectorMSDProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, peakDetectorSettings, monitor));
		//
		if(!processingInfo.hasErrorMessages()) {
			/*
			 * Detect the peaks using the chromatogram selection.
			 * Filter the peak results to match only the selection.
			 */
			AmdisIdentifier identifier = new AmdisIdentifier();
			IAmdisSettings amdisSettings;
			if(peakDetectorSettings instanceof IAmdisSettings) {
				amdisSettings = (IAmdisSettings)peakDetectorSettings;
			} else {
				amdisSettings = PreferenceSupplier.getAmdisSettings();
			}
			identifier.calulateAndSetDeconvolutedPeaks(chromatogramSelection, amdisSettings, monitor);
			/*
			 * Success - add a message.
			 */
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "AMDIS Peak Detector", "Peaks have been detected successfully."));
		}
		return processingInfo;
	}

	@Override
	public IPeakDetectorMSDProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IPeakDetectorMSDSettings peakDetectorSettings = PreferenceSupplier.getAmdisSettings();
		return detect(chromatogramSelection, peakDetectorSettings, monitor);
	}
}
