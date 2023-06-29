/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.eclipse.chemclipse.converter.core.AbstractFileContentMatcher;
import org.eclipse.chemclipse.converter.core.IFileContentMatcher;
import org.eclipse.chemclipse.msd.converter.supplier.csv.io.core.CSVPeakConverter;

public class FileContentMatcherPeak extends AbstractFileContentMatcher implements IFileContentMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		try {
			try (CSVParser parser = new CSVParser(new FileReader(file), CSVFormat.EXCEL.withHeader())) {
				String[] array = parser.getHeaderMap().keySet().toArray(new String[0]);
				return Arrays.equals(array, CSVPeakConverter.HEADERS);
			}
		} catch(IOException e) {
			// ignore
		}
		return false;
	}
}
