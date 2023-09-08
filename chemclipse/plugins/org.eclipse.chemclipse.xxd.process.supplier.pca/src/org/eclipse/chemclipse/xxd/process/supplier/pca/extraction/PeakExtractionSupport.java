/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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
package org.eclipse.chemclipse.xxd.process.supplier.pca.extraction;

import java.util.Map;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.ValueOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.DescriptionOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Samples;

public class PeakExtractionSupport {

	public Samples extractPeakData(Map<IDataInputEntry, IPeaks<?>> peaks, ExtractionSettings extractionSettings) {

		Samples samples;
		DescriptionOption descriptionOption = extractionSettings.getDescriptionOption();
		ValueOption valueOption = extractionSettings.getValueOption();
		//
		switch(extractionSettings.getExtractionOption()) {
			case RETENTION_INDEX:
				PeakRetentionIndexExtractor peakRetentionIndexExtractor = new PeakRetentionIndexExtractor();
				int retentionIndexWindow = extractionSettings.getGroupValueWindow();
				samples = peakRetentionIndexExtractor.extractPeakData(peaks, retentionIndexWindow, descriptionOption, valueOption);
				break;
			case PEAK_TARGETS:
				PeakTargetExtractor peakTargetExtractor = new PeakTargetExtractor();
				samples = peakTargetExtractor.extractPeakData(peaks, descriptionOption, valueOption);
				break;
			default:
				PeakRetentionTimeExtractor peakRetentionTimeExtractor = new PeakRetentionTimeExtractor();
				int retentionTimeWindow = extractionSettings.getGroupValueWindow();
				samples = peakRetentionTimeExtractor.extractPeakData(peaks, retentionTimeWindow, descriptionOption, valueOption);
				break;
		}
		//
		return samples;
	}
}