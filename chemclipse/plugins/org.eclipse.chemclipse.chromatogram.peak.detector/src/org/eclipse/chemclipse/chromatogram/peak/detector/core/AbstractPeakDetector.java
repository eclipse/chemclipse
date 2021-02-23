/*******************************************************************************
 * Copyright (c) 2014, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractPeakDetector<P extends IPeak, C extends IChromatogram<P>, R> implements IPeakDetector<P, C, R> {

	@Override
	public IProcessingInfo<R> validate(IChromatogramSelection<?, ?> chromatogramSelection, IPeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo<R> processingInfo = new ProcessingInfo<>();
		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage("Peak Detector", "The chromatogram selection must not be null.");
		}
		//
		if(peakDetectorSettings == null) {
			processingInfo.addErrorMessage("Peak Detector", "The peak detector settings must not be null.");
		}
		//
		return processingInfo;
	}
}
