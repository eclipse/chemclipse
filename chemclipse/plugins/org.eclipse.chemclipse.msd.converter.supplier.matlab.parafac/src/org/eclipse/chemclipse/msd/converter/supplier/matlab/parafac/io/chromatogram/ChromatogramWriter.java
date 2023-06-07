/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.io.chromatogram;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.converter.MatlabParafacPeakExportConverter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.PeaksMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramMSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		if(chromatogram == null || file == null) {
			throw new IOException("The chromatogram and the file must be not null.");
		}
		/*
		 * Extract the peaks
		 */
		IPeaks<? extends IPeakMSD> peaks = new PeaksMSD();
		List<IChromatogramPeakMSD> chromatogramPeaks = chromatogram.getPeaks();
		for(IChromatogramPeakMSD chromatogramPeak : chromatogramPeaks) {
			peaks.addPeak(chromatogramPeak);
		}
		/*
		 * Export the peaks
		 */
		MatlabParafacPeakExportConverter peakExportConverter = new MatlabParafacPeakExportConverter();
		peakExportConverter.convert(file, peaks, false, monitor);
	}
}
