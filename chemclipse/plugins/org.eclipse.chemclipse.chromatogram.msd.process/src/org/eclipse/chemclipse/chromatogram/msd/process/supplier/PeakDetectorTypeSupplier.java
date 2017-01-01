/*******************************************************************************
 * Copyright (c) 2012, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetectorSupplier;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetectorTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Peak Detector";

	@Override
	public String getCategory() {

		return CATEGORY;
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		IPeakDetectorSupplier peakDetectorSupplier = PeakDetectorMSD.getPeakDetectorSupport().getPeakDetectorSupplier(processorId);
		return peakDetectorSupplier.getPeakDetectorName();
	}

	@Override
	public List<String> getPluginIds() throws Exception {

		return PeakDetectorMSD.getPeakDetectorSupport().getAvailablePeakDetectorIds();
	}

	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelectionMSD chromatogramSelection, String processorId, IProgressMonitor monitor) {

		return PeakDetectorMSD.detect(chromatogramSelection, processorId, monitor);
	}
}
