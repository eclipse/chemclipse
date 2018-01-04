/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.peak.detector.processing.IPeakDetectorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.peak.detector.processing.PeakDetectorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;

public abstract class AbstractPeakDetector implements IPeakDetector {

	@Override
	public IPeakDetectorProcessingInfo validate(IChromatogramSelection chromatogramSelection, IPeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		IPeakDetectorProcessingInfo processingInfo = new PeakDetectorProcessingInfo();
		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage("Peak Detector", "The chromatogram selection must not be null.");
		}
		if(peakDetectorSettings == null) {
			processingInfo.addErrorMessage("Peak Detector", "The peak detector settings must not be null.");
		}
		return processingInfo;
	}
}
