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
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.supplier.pca.extraction.MassSpectrumExtractionSupport;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Samples;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionMassSpectra implements IExtractionData {

	private List<IDataInputEntry> dataInputEntries;

	public PcaExtractionMassSpectra(List<IDataInputEntry> dataInputEntries) {

		this.dataInputEntries = dataInputEntries;
	}

	@Override
	public Samples process(IProgressMonitor monitor) {

		MassSpectrumExtractionSupport massSpectraExtractionSupport = new MassSpectrumExtractionSupport();
		Map<IDataInputEntry, IMassSpectra> inputData = new HashMap<>();
		for(IDataInputEntry input : dataInputEntries) {
			IProcessingInfo<IMassSpectra> processingInfo = DatabaseConverter.convert(new File(input.getInputFile()), monitor);
			IMassSpectra massSpectra = processingInfo.getProcessingResult();
			if(massSpectra != null) {
				inputData.put(input, massSpectra);
			}
		}
		return massSpectraExtractionSupport.process(inputData, monitor);
	}
}