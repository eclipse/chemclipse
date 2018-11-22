/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.IPeakDetectorSettingsWSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class AbstractPeakDetectorWSDSignal extends AbstractPeakDetectorWSD {

	@Override
	public IProcessingInfo detect(IChromatogramSelectionWSD chromatogramSelection, IPeakDetectorSettingsWSD peakDetectorSettings, IProgressMonitor monitor) {

		return null;
	}

	@Override
	public IProcessingInfo detect(IChromatogramSelectionWSD chromatogramSelection, IProgressMonitor monitor) {

		return null;
	}
}
