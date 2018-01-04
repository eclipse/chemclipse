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
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class CalibrationFileWriter {

	private static final Logger logger = Logger.getLogger(CalibrationFileWriter.class);
	private DecimalFormat decimalFormat;

	public CalibrationFileWriter() {
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
	}

	public void write(File file, List<IRetentionIndexEntry> retentionIndexEntries) {

		try {
			PrintWriter printWriter = new PrintWriter(file);
			for(IRetentionIndexEntry retentionIndexEntry : retentionIndexEntries) {
				/*
				 * e.g.
				 * 11.336 1700.0 100 937 Heptadecane
				 */
				printWriter.print(decimalFormat.format(retentionIndexEntry.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
				printWriter.print(" ");
				printWriter.print(retentionIndexEntry.getRetentionIndex());
				printWriter.print(" ");
				printWriter.print(100);
				printWriter.print(" ");
				printWriter.print(999);
				printWriter.print(" ");
				printWriter.println(retentionIndexEntry.getName());
			}
			printWriter.flush();
			printWriter.close();
		} catch(FileNotFoundException e) {
			logger.warn(e);
		}
	}
}
