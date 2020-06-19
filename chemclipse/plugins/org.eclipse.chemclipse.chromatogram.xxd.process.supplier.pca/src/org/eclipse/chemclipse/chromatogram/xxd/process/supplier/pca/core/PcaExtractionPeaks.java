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
 * Alexander Kerner - Generics
 * Philip Wenig - improvements
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction.ExtractionSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction.PeakExtractionSupport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionPeaks implements IExtractionData {

	private static final Logger logger = Logger.getLogger(PcaExtractionPeaks.class);
	//
	private final List<IDataInputEntry> dataInputEntries;
	private final ExtractionSettings extractionSettings;

	public PcaExtractionPeaks(List<IDataInputEntry> dataInputEntries, ExtractionSettings extractionSettings) {

		this.dataInputEntries = dataInputEntries;
		this.extractionSettings = extractionSettings;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private Map<IDataInputEntry, IPeaks<?>> extractPeaks(List<IDataInputEntry> peakInputFiles, IProgressMonitor monitor) {

		Map<IDataInputEntry, IPeaks<?>> peakMap = new LinkedHashMap<>();
		for(IDataInputEntry peakFile : peakInputFiles) {
			try {
				/*
				 * MSD
				 */
				IProcessingInfo<IPeaks> processingInfo = PeakConverterMSD.convert(new File(peakFile.getInputFile()), monitor);
				IPeaks<?> peaks = processingInfo.getProcessingResult();
				if(peaks != null && peaks.isEmpty()) {
					/*
					 * CSD
					 */
					IChromatogramCSD chromatogram = ChromatogramConverterCSD.getInstance().convert(new File(peakFile.getInputFile()), monitor).getProcessingResult();
					for(IPeak peak : chromatogram.getPeaks()) {
						peaks.addPeak(peak);
					}
				}
				//
				if(!peaks.isEmpty()) {
					peakMap.put(peakFile, peaks);
				} else {
					logger.warn("No peaks contained in file: " + peakFile);
				}
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		return peakMap;
	}

	@Override
	public Samples process(IProgressMonitor monitor) {

		PeakExtractionSupport peakExtractionSupport = new PeakExtractionSupport();
		Map<IDataInputEntry, IPeaks<?>> peakMap = extractPeaks(dataInputEntries, monitor);
		return peakExtractionSupport.extractPeakData(peakMap, extractionSettings, monitor);
	}
}
