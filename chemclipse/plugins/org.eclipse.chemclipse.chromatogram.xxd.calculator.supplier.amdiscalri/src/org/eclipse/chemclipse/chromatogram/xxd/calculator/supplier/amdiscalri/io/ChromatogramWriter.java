/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexEntry;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramMSDWriter {

	private Map<String, Integer> retentionIndices;

	public ChromatogramWriter() {
		initializeReferenceMap();
	}

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		if(chromatogram == null || file == null) {
			throw new IOException("The chromatogram and the file must be not null.");
		}
		/*
		 * Write the cal specifiation.
		 */
		List<IRetentionIndexEntry> retentionIndexEntries = new ArrayList<IRetentionIndexEntry>();
		for(IChromatogramPeakMSD peak : chromatogram.getPeaks()) {
			List<IPeakTarget> peakTargets = peak.getTargets();
			if(peakTargets.size() > 0) {
				String name = peakTargets.get(0).getLibraryInformation().getName().trim();
				if(retentionIndices.containsKey(name)) {
					int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
					float retentionIndex = retentionIndices.get(name);
					IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
					retentionIndexEntries.add(retentionIndexEntry);
				}
			}
		}
		//
		CalibrationFileWriter calibrationFileWriter = new CalibrationFileWriter();
		calibrationFileWriter.write(file, retentionIndexEntries);
	}

	private void initializeReferenceMap() {

		/*
		 * See standards/alkanes.msl
		 */
		retentionIndices = new HashMap<String, Integer>();
		retentionIndices.put("C1 (Methane)", 100);
		retentionIndices.put("C2 (Ethane)", 200);
		retentionIndices.put("C3 (Propane)", 300);
		retentionIndices.put("C4 (Butane)", 400);
		retentionIndices.put("C5 (Pentane)", 500);
		retentionIndices.put("C6 (Hexane)", 600);
		retentionIndices.put("C7 (Heptane)", 700);
		retentionIndices.put("C8 (Octane)", 800);
		retentionIndices.put("C9 (Nonane)", 900);
		retentionIndices.put("C10 (Decane)", 1000);
		retentionIndices.put("C11 (Undecane)", 1100);
		retentionIndices.put("C12 (Dodecane)", 1200);
		retentionIndices.put("C13 (Tridecane)", 1300);
		retentionIndices.put("C14 (Tetradecane)", 1400);
		retentionIndices.put("C15 (Pentadecane)", 1500);
		retentionIndices.put("C16 (Hexadecane)", 1600);
		retentionIndices.put("C17 (Heptadecane)", 1700);
		retentionIndices.put("C18 (Octadecane)", 1800);
		retentionIndices.put("C19 (Nonadecane)", 1900);
		retentionIndices.put("C20 (Eicosane)", 2000);
		retentionIndices.put("C21 (Heneicosane)", 2100);
		retentionIndices.put("C22 (Docosane)", 2200);
		retentionIndices.put("C23 (Tricosane)", 2300);
		retentionIndices.put("C24 (Tetracosane)", 2400);
		retentionIndices.put("C25 (Pentacosane)", 2500);
		retentionIndices.put("C26 (Hexacosane)", 2600);
		retentionIndices.put("C27 (Heptacosane)", 2700);
		retentionIndices.put("C28 (Octacosane)", 2800);
		retentionIndices.put("C29 (Nonacosane)", 2900);
		retentionIndices.put("C30 (Triacontane)", 3000);
		retentionIndices.put("C31 (Hentriacontane)", 3100);
		retentionIndices.put("C32 (Dotriacontane)", 3200);
		retentionIndices.put("C33 (Tritriacontane)", 3300);
		retentionIndices.put("C34 (Tetratriacontane)", 3400);
		retentionIndices.put("C35 (Pentatriacontane)", 3500);
		retentionIndices.put("C36 (Hexatriacontane)", 3600);
	}
}
