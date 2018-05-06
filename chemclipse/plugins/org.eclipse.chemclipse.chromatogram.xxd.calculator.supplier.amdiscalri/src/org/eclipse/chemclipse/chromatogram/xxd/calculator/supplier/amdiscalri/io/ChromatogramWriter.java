/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexExtractor;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramMSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		if(chromatogram == null || file == null) {
			throw new IOException("The chromatogram and the file must be not null.");
		}
		/*
		 * Write the cal specifiation.
		 */
		RetentionIndexExtractor retentionIndexExtractor = new RetentionIndexExtractor();
		ISeparationColumnIndices separationColumnIndices = retentionIndexExtractor.extract(chromatogram);
		CalibrationFileWriter calibrationFileWriter = new CalibrationFileWriter();
		calibrationFileWriter.write(file, separationColumnIndices);
	}
}
