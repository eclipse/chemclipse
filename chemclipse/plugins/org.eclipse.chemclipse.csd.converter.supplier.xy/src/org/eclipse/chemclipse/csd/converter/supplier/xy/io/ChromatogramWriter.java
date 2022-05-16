/*******************************************************************************
 * Copyright (c) 2012, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.xy.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDWriter;
import org.eclipse.chemclipse.csd.converter.supplier.xy.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramCSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		DelimiterFormat delimiterFormat = PreferenceSupplier.getDelimiterFormat();
		RetentionTimeFormat retentionTimeFormat = PreferenceSupplier.getRetentionTimeFormat();
		//
		try (PrintWriter printWriter = new PrintWriter(file)) {
			/*
			 * Write each scan to the file.
			 */
			String delimiter = delimiterFormat.value();
			for(IScan scan : chromatogram.getScans()) {
				int retentionTime = scan.getRetentionTime();
				String x;
				switch(retentionTimeFormat) {
					case MINUTES:
						x = Double.toString(retentionTime / IChromatogram.MINUTE_CORRELATION_FACTOR);
						break;
					case SECONDS:
						x = Double.toString(retentionTime / IChromatogram.SECOND_CORRELATION_FACTOR);
						break;
					default:
						x = Integer.toString(retentionTime);
						break;
				}
				//
				printWriter.println(x + delimiter + scan.getTotalSignal());
			}
			//
			printWriter.flush();
		}
	}
}