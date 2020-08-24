/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.PathResolver;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

public class StandardsReader {

	public IMassSpectra getStandardsMassSpectra() {

		File file = new File(PathResolver.getAbsolutePath(PathResolver.ALKANES));
		IProcessingInfo<?> processingInfo = DatabaseConverter.convert(file, new NullProgressMonitor());
		IMassSpectra massSpectra = (IMassSpectra)processingInfo.getProcessingResult();
		return massSpectra;
	}

	public List<IRetentionIndexEntry> getStandardsList() {

		List<IRetentionIndexEntry> retentionIndexEntries = new ArrayList<>();
		//
		IMassSpectra massSpectra = getStandardsMassSpectra();
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
}
