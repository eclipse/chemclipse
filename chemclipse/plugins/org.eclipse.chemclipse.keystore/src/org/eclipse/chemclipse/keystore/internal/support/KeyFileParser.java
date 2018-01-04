/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.keystore.internal.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;

public class KeyFileParser {

	private static final Logger logger = Logger.getLogger(KeyFileParser.class);

	/*
	 * Use only static methods.
	 */
	private KeyFileParser() {
	}

	public static Map<String, String> readKeysFromFile(File file) {

		Map<String, String> keyMap = new HashMap<String, String>();
		/*
		 * The file must be not null.
		 */
		if(file != null) {
			FileReader reader = null;
			BufferedReader bufferedReader = null;
			try {
				reader = new FileReader(file);
				bufferedReader = new BufferedReader(reader);
				String line;
				String[] values;
				String id;
				String serial;
				while((line = bufferedReader.readLine()) != null) {
					if(!line.startsWith("#") || !line.startsWith("")) {
						/*
						 * All values are separated by a tab.
						 */
						values = line.split("\t");
						if(values.length == 2) {
							id = values[0].trim();
							serial = values[1].trim();
							keyMap.put(id, serial);
						}
					}
				}
			} catch(FileNotFoundException e) {
				logger.warn(e);
			} catch(IOException e) {
				logger.warn(e);
			} finally {
				/*
				 * Close the buffered reader.
				 */
				if(bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch(IOException e) {
						logger.warn(e);
					}
				}
				/*
				 * Close the file reader.
				 */
				if(reader != null) {
					try {
						reader.close();
					} catch(IOException e) {
						logger.warn(e);
					}
				}
			}
		}
		return keyMap;
	}
}
