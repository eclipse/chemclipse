/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.IPeakDetectorSettingsWSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class AbstractPeakDetectorWSDSignal<P extends IPeak, C extends IChromatogram<P>, R> extends AbstractPeakDetectorWSD<P, C, R> {

	@Override
	public IProcessingInfo<R> detect(IChromatogramSelectionWSD chromatogramSelection, IPeakDetectorSettingsWSD peakDetectorSettings, IProgressMonitor monitor) {

		return new ProcessingInfo<>();
	}

	@Override
	public IProcessingInfo<R> detect(IChromatogramSelectionWSD chromatogramSelection, IProgressMonitor monitor) {

		return new ProcessingInfo<>();
	}
}
