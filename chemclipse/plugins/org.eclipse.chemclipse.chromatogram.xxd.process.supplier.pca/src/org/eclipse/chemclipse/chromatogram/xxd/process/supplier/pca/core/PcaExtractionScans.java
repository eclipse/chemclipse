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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction.ScanExtractionSupport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction.ScanExtractionSupport.ExtractionType;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionScans implements IExtractionData {

	private List<IDataInputEntry> dataInputEntries;
	private ExtractionType extractionType;
	private int maximalNumberScans;
	private int retentionTimeWindow;
	private boolean useDefaultProperties;

	public PcaExtractionScans(int retentionTimeWindow, int maximalNumberScans, List<IDataInputEntry> dataInputEntries, ExtractionType scanAlignment, boolean useDefaultProperties) {
		this.retentionTimeWindow = retentionTimeWindow;
		this.dataInputEntries = dataInputEntries;
		this.extractionType = scanAlignment;
		this.useDefaultProperties = useDefaultProperties;
		this.maximalNumberScans = maximalNumberScans;
	}

	@Override
	public Samples process(IProgressMonitor monitor) {

		ScanExtractionSupport scansExtractionSupport = new ScanExtractionSupport(retentionTimeWindow, maximalNumberScans, extractionType, useDefaultProperties);
		Map<IDataInputEntry, Collection<IScan>> inputData = new HashMap<>();
		for(IDataInputEntry input : dataInputEntries) {
			IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(new File(input.getInputFile()), monitor);
			List<IScan> scans = processingInfo.getProcessingResult().getScans();
			inputData.put(input, scans);
		}
		return scansExtractionSupport.process(inputData, monitor);
	}
}
