/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.core;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.AbstractPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.PeakDetectorELUSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.support.PeakProcessorSupport;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetectorELU extends AbstractPeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorELU.class);

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorSettingsMSD peakDetectorSettings, IProgressMonitor monitor) {

		/*
		 * Validate
		 */
		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, peakDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(peakDetectorSettings instanceof PeakDetectorELUSettings) {
				PeakDetectorELUSettings amdisSettings = (PeakDetectorELUSettings)peakDetectorSettings;
				PeakProcessorSupport peakProcessorSupport = new PeakProcessorSupport();
				File file = new File(amdisSettings.getPathFileELU());
				if(file.exists()) {
					peakProcessorSupport.extractEluFileAndSetPeaks(chromatogramSelection, file, amdisSettings, monitor);
				} else {
					logger.warn("The file doesn't exist: " + file.getAbsolutePath());
				}
			} else {
				logger.warn("The settings is not of type: " + PeakDetectorELUSettings.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		PeakDetectorELUSettings peakDetectorSettings = PreferenceSupplier.getPeakDetectorELUSettings();
		return detect(chromatogramSelection, peakDetectorSettings, monitor);
	}
}
