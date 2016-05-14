/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.PathResolver;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexEntry;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.NullProgressMonitor;

public class StandardsReader {

	public List<IRetentionIndexEntry> getStandardsList() {

		List<IRetentionIndexEntry> retentionIndexEntries = new ArrayList<IRetentionIndexEntry>();
		//
		File file = new File(PathResolver.getAbsolutePath(PathResolver.ALKANES));
		IMassSpectrumImportConverterProcessingInfo processingInfo = MassSpectrumConverter.convert(file, new NullProgressMonitor());
		IMassSpectra massSpectra = processingInfo.getMassSpectra();
		for(IScanMSD massSpectrum : massSpectra.getList()) {
			if(massSpectrum instanceof ILibraryMassSpectrum) {
				ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)massSpectrum;
				String name = libraryMassSpectrum.getLibraryInformation().getName();
				int retentionTime = massSpectrum.getRetentionTime();
				float retentionIndex = massSpectrum.getRetentionIndex();
				IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
				retentionIndexEntries.add(retentionIndexEntry);
			}
		}
		//
		return retentionIndexEntries;
	}

	public String[] getAvailableStandards() {

		List<IRetentionIndexEntry> retentionIndexEntries = getStandardsList();
		int size = retentionIndexEntries.size();
		//
		String[] entries = new String[size];
		for(int i = 0; i < size; i++) {
			entries[i] = retentionIndexEntries.get(i).getName();
		}
		//
		return entries;
	}

	public Map<String, Integer> getNameIndexMap() {

		Map<String, Integer> retentionIndices = new HashMap<String, Integer>();
		//
		StandardsReader standardsReader = new StandardsReader();
		List<IRetentionIndexEntry> retentionIndexEntries = standardsReader.getStandardsList();
		for(IRetentionIndexEntry retentionIndexEntry : retentionIndexEntries) {
			retentionIndices.put(retentionIndexEntry.getName(), (int)retentionIndexEntry.getRetentionIndex());
		}
		//
		return retentionIndices;
	}
}
