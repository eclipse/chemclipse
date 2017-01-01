/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexEntry;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;

public class CalibrationFileReader {

	private static final Logger logger = Logger.getLogger(CalibrationFileReader.class);
	private static final String DELIMITER = " ";

	public List<IRetentionIndexEntry> parse(File file) {

		List<IRetentionIndexEntry> retentionIndexEntries = new ArrayList<IRetentionIndexEntry>();
		//
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String line;
			while((line = bufferedReader.readLine()) != null) {
				/*
				 * 10.214 1600.0 100 981 Hexadecane
				 */
				try {
					String[] values = line.split(DELIMITER);
					if(values.length >= 5) {
						int retentionTime = (int)(Double.parseDouble(values[0]) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
						float retentionIndex = Float.parseFloat(values[1]);
						String peakName = values[4].trim();
						if(values.length >= 6) {
							peakName += " " + values[5].trim();
						}
						IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, peakName);
						retentionIndexEntries.add(retentionIndexEntry);
					}
				} catch(NumberFormatException e) {
					logger.warn(e);
				}
			}
			bufferedReader.close();
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
		//
		return retentionIndexEntries;
	}
}
