/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add default implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetector;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.IPeakDetectorSettingsWSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakDetectorWSD<P extends IPeak, C extends IChromatogram<P>, R> extends IPeakDetector<P, C, R> {

	/**
	 * All peak detector plugins must implement this class.
	 * 
	 * @param chromatogramSelection
	 * @param peakDetectorSettings
	 * @param monitor
	 * @throws ValueMustNotBeNullException
	 */
	IProcessingInfo<R> detect(IChromatogramSelectionWSD chromatogramSelection, IPeakDetectorSettingsWSD peakDetectorSettings, IProgressMonitor monitor);

	/**
	 * The same as the other method but without settings.
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @throws ValueMustNotBeNullException
	 */
	default IProcessingInfo<R> detect(IChromatogramSelectionWSD chromatogramSelection, IProgressMonitor monitor) {

		return detect(chromatogramSelection, null, monitor);
	}
}
