/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ScansExtractionSupport.ExtractionType;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionScans implements IDataExtraction {

	private List<IDataInputEntry> dataInputEntriesAll;
	private ExtractionType extractionType;
	private int maximalNumberScans;
	private int retentionTimeWindow;
	private boolean useDefoultProperties;

	public PcaExtractionScans(int retentionTimeWindow, int maximalNumberScans, List<IDataInputEntry> dataInputEntries, ExtractionType scanAlignment, boolean useDefoultProperties) {

		this.retentionTimeWindow = retentionTimeWindow;
		this.dataInputEntriesAll = dataInputEntries;
		this.extractionType = scanAlignment;
		this.useDefoultProperties = useDefoultProperties;
		this.maximalNumberScans = maximalNumberScans;
	}

	@Override
	public Samples process(IProgressMonitor monitor) {

		ScansExtractionSupport scansExtractionSupport = new ScansExtractionSupport(retentionTimeWindow, maximalNumberScans, extractionType, useDefoultProperties);
		Map<IDataInputEntry, Collection<IScan>> inputData = new HashMap<>();
		for(IDataInputEntry input : dataInputEntriesAll) {
			IProcessingInfo processingInfo = ChromatogramConverterMSD.getInstance().convert(new File(input.getInputFile()), monitor);
			List<IScan> scans = processingInfo.getProcessingResult(IChromatogramMSD.class).getScans();
			inputData.put(input, scans);
		}
		return scansExtractionSupport.process(inputData, monitor);
	}
}
