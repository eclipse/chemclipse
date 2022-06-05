/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
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
 * Matthias Mailänder - adapted for MALDI-TOF MS
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction.MALDIExtractionSupport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction.MALDIExtractionSupport.ExtractionType;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionMALDI implements IExtractionData {

	private List<IDataInputEntry> dataInputEntries;
	private int maximalNumberPeaks;
	private int massWindow;
	private boolean useDefaultProperties;
	private ExtractionType extractionType;

	public PcaExtractionMALDI(int massWindow, int maximalNumberPeaks, List<IDataInputEntry> dataInputEntries, ExtractionType extractionType, boolean useDefaultProperties) {

		this.massWindow = massWindow;
		this.dataInputEntries = dataInputEntries;
		this.extractionType = extractionType;
		this.useDefaultProperties = useDefaultProperties;
		this.maximalNumberPeaks = maximalNumberPeaks;
	}

	@Override
	public Samples process(IProgressMonitor monitor) {

		MALDIExtractionSupport spectraExtractionSupport = new MALDIExtractionSupport(massWindow, maximalNumberPeaks, extractionType, useDefaultProperties);
		Map<IDataInputEntry, Collection<IIon>> inputData = new HashMap<>();
		for(IDataInputEntry input : dataInputEntries) {
			IProcessingInfo<IMassSpectra> processingInfo = MassSpectrumConverter.convert(new File(input.getInputFile()), monitor);
			IMassSpectra massSpectra = processingInfo.getProcessingResult();
			if(massSpectra == null)
				continue;
			IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
			if(massSpectrum == null)
				continue;
			List<IIon> ions = massSpectrum.getIons();
			if(ions.size() > 0)
				inputData.put(input, ions);
		}
		return spectraExtractionSupport.process(inputData, monitor);
	}
}
