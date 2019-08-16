/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionPeaks implements IDataExtraction {

	private List<IDataInputEntry> dataInputEntriesAll;
	private int retentionTimeWindow;

	public PcaExtractionPeaks(List<IDataInputEntry> dataInputEntriesAll, int retentionTimeWindow) {

		this.retentionTimeWindow = retentionTimeWindow;
		this.dataInputEntriesAll = dataInputEntriesAll;
	}

	private Map<IDataInputEntry, IPeaks> extractPeaks(List<IDataInputEntry> peakinpitFiles, IProgressMonitor monitor) {

		Map<IDataInputEntry, IPeaks> peakMap = new LinkedHashMap<>();
		for(IDataInputEntry peakFile : peakinpitFiles) {
			try {
				/*
				 * Try to catch exceptions if wrong files have been selected.
				 */
				IProcessingInfo<IPeaks> processingInfo = PeakConverterMSD.convert(new File(peakFile.getInputFile()), monitor);
				IPeaks peaks = processingInfo.getProcessingResult();
				peakMap.put(peakFile, peaks);
			} catch(Exception e) {
			}
		}
		return peakMap;
	}

	@Override
	public Samples process(IProgressMonitor monitor) {

		/*
		 * Initialize PCA Results
		 */
		PeakExtractionSupport peakExtractionSupport = new PeakExtractionSupport(retentionTimeWindow);
		Map<IDataInputEntry, IPeaks> peakMap = extractPeaks(dataInputEntriesAll, monitor);
		Samples samples = peakExtractionSupport.extractPeakData(peakMap, monitor);
		return samples;
	}
}
