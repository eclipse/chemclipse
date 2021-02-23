/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetector;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakDetectorMSD<P extends IPeak, C extends IChromatogram<P>, R> extends IPeakDetector<P, C, R> {

	/**
	 * All peak detector plugins must implement this class.
	 * 
	 * @param chromatogramSelection
	 * @param peakDetectorSettings
	 * @param monitor
	 * @throws ValueMustNotBeNullException
	 */
	IProcessingInfo<R> detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorSettingsMSD peakDetectorSettings, IProgressMonitor monitor);

	/**
	 * The same as the other method but without settings.
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @throws ValueMustNotBeNullException
	 */
	IProcessingInfo<R> detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor);
}
