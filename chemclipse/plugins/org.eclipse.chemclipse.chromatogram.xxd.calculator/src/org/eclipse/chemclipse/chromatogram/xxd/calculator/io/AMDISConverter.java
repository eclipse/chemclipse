/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.SeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnIndices;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public class AMDISConverter {

	private static final Logger logger = Logger.getLogger(AMDISConverter.class);

	/**
	 * Return ISeparationColumnIndices via IProcessingInfo.
	 * 
	 * @param file
	 * @return {@link IProcessingInfo}
	 */
	public IProcessingInfo<ISeparationColumnIndices> parseRetentionIndices(File file) {

		IProcessingInfo<ISeparationColumnIndices> processingInfo = new ProcessingInfo<ISeparationColumnIndices>();
		processingInfo.setProcessingResult(parse(file));
		return processingInfo;
	}

	public ISeparationColumnIndices parse(File file) {

		ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
		//
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			/*
			 * Column Specification
			 */
			String name = SeparationColumnFactory.TYPE_DEFAULT;
			String length = "";
			String diameter = "";
			String phase = "";
			//
			String line;
			while((line = bufferedReader.readLine()) != null) {
				/*
				 * 10.214 1600.0 100 981 Hexadecane
				 * see: AMDIS - User Guide
				 * https://www.nist.gov/sites/default/files/documents/srd/AMDISMan.pdf
				 */
				if(line.startsWith(IColumnFormat.COLUMN_MARKER)) {
					/*
					 * Column data
					 * #COLUMN_NAME=DB5
					 * ...
					 */
					if(line.startsWith(IColumnFormat.COLUMN_NAME)) {
						name = getValue(line);
					} else if(line.startsWith(IColumnFormat.COLUMN_LENGTH)) {
						length = getValue(line);
					} else if(line.startsWith(IColumnFormat.COLUMN_DIAMETER)) {
						diameter = getValue(line);
					} else if(line.startsWith(IColumnFormat.COLUMN_PHASE)) {
						phase = getValue(line);
					}
				} else {
					/*
					 * RI data
					 */
					try {
						String[] values = line.split(IColumnFormat.RI_VALUE_DELIMITER);
						if(values.length >= 5) {
							int retentionTime = (int)(Double.parseDouble(values[0]) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR); // Retention Time: 1.908
							float retentionIndex = Float.parseFloat(values[1]); // Retention Index: 600.0
							/*
							 * The following values are used by AMDIS but are not needed here.
							 */
							// values[2] ... Net - the computed Net value
							// values[3] ... S/N - the computed "S/N (total)" value
							/*
							 * It's assumed, that all other values belong to the peak name.
							 */
							String peakName = values[4].trim(); // C6
							for(int i = 5; i < values.length; i++) {
								peakName += " " + values[i];
							}
							//
							IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, peakName);
							separationColumnIndices.put(retentionIndexEntry);
						} else {
							throw new IOException("Unexpected column count: " + Arrays.asList(values).toString());
						}
					} catch(NumberFormatException e) {
						logger.warn(e);
					}
				}
			}
			/*
			 * Create and set the column.
			 */
			ISeparationColumn separationColumn = new SeparationColumn(name, length, diameter, phase);
			separationColumnIndices.setSeparationColumn(separationColumn);
			//
			bufferedReader.close();
		} catch(IOException e) {
			logger.error(e);
		}
		//
		return separationColumnIndices;
	}

	private String getValue(String line) {

		String value = "";
		String[] values = line.split(IColumnFormat.HEADER_VALUE_DELIMITER);
		if(values.length == 2) {
			value = values[1].trim();
		}
		return value;
	}
}
