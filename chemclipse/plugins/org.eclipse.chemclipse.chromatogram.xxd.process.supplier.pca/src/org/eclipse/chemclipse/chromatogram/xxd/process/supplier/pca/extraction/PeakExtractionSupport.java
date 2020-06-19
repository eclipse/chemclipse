/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements classifier
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction;

import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakExtractionSupport {

	public Samples extractPeakData(Map<IDataInputEntry, IPeaks<?>> peaks, ExtractionSettings extractionSettings, IProgressMonitor monitor) {

		if(extractionSettings.isUseTargets()) {
			PeakTargetExtractor samplesExtractor = new PeakTargetExtractor();
			return samplesExtractor.extractPeakData(peaks, monitor);
		} else {
			PeakRetentionTimeExtractor samplesExtractor = new PeakRetentionTimeExtractor();
			int retentionTimeWindow = extractionSettings.getRetentionTimeWindow();
			return samplesExtractor.extractPeakData(peaks, retentionTimeWindow, monitor);
		}
	}
}
