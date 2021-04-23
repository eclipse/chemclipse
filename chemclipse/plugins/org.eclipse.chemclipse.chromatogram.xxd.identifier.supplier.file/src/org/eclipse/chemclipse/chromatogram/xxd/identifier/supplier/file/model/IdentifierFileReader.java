/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.io.IColumnFormat;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;

public class IdentifierFileReader {

	private static final Logger logger = Logger.getLogger(IdentifierFileReader.class);

	public ISeparationColumn parse(File file) {

		/*
		 * Extract the separation column if the library contains the info, e.g. in a *.msl or *.msp file.
		 * Otherwise, create a default column.
		 */
		ISeparationColumn separationColumn = SeparationColumnFactory.getSeparationColumn(SeparationColumnFactory.TYPE_DEFAULT);
		if(isSeparationColumnFile(file)) {
			separationColumn = extractSeparationColumn(file);
		}
		//
		return separationColumn;
	}

	private ISeparationColumn extractSeparationColumn(File file) {

		/*
		 * Restrict to *.msl and *.msp files at the moment.
		 * Otherwise use default.
		 */
		ISeparationColumn separationColumn = SeparationColumnFactory.getSeparationColumn(SeparationColumnFactory.TYPE_DEFAULT);
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
				}
			}
			/*
			 * Create the column.
			 */
			separationColumn = new SeparationColumn(name, length, diameter, phase);
			bufferedReader.close();
		} catch(IOException e) {
			logger.error(e);
		}
		//
		return separationColumn;
	}

	private boolean isSeparationColumnFile(File file) {

		boolean isSeparationColumnFile = false;
		if(file != null && file.exists()) {
			String name = file.getName().toLowerCase();
			if(name.endsWith(".msl") || name.endsWith(".msp")) {
				isSeparationColumnFile = true;
			}
		}
		//
		return isSeparationColumnFile;
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
