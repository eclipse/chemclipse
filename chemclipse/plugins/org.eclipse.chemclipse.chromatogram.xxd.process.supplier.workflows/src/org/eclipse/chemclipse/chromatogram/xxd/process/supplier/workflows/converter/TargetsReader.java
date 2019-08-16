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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;

public class TargetsReader {

	private static final Logger logger = Logger.getLogger(TargetsReader.class);

	public Map<String, String> getCompoundCasMap(File file) {

		Map<String, String> compoundCasMap = new HashMap<String, String>();
		//
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line;
			while((line = bufferedReader.readLine()) != null) {
				if(line.startsWith("#")) {
					continue;
				}
				//
				String[] values = line.split("\t");
				if(values.length >= 2) {
					compoundCasMap.put(values[0].trim(), values[1].trim());
				}
			}
		} catch(Exception e) {
			logger.warn(e);
		} finally {
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch(IOException e) {
					logger.warn(e);
				}
			}
		}
		//
		return compoundCasMap;
	}
}
