/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.peak.detector.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.processing.IPeakDetectorCSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorCSDSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetector;

public interface IPeakDetectorCSD extends IPeakDetector {

	/**
	 * All peak detector plugins must implement this class.
	 * 
	 * @param chromatogramSelection
	 * @param peakDetectorSettings
	 * @param monitor
	 * @throws ValueMustNotBeNullException
	 */
	IPeakDetectorCSDProcessingInfo detect(IChromatogramSelectionCSD chromatogramSelection, IPeakDetectorCSDSettings peakDetectorSettings, IProgressMonitor monitor);

	/**
	 * The same as the other method but without settings.
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @throws ValueMustNotBeNullException
	 */
	IPeakDetectorCSDProcessingInfo detect(IChromatogramSelectionCSD chromatogramSelection, IProgressMonitor monitor);
}
