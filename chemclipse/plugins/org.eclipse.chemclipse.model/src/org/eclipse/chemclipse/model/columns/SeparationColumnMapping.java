/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.columns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

public class SeparationColumnMapping extends HashMap<String, SeparationColumnType> {

	private static final Logger logger = Logger.getLogger(SeparationColumnMapping.class);
	private static final long serialVersionUID = 5357306927108529230L;
	//
	public static final String DESCRIPTION = "Separation Column Mapping";
	public static final String FILE_EXTENSION = ".scm";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
	public static final String SEPARATOR_TOKEN = ";";
	public static final String SEPARATOR_ENTRY = "|";
	public static final String LINE_DELIMITER = "\n";

	public void load(String items) {

		loadSettings(items);
	}

	public void loadDefault(String items) {

		loadSettings(items);
	}

	public String save() {

		return extractSettings(this);
	}

	public String extractSettings(Map<String, SeparationColumnType> settings) {

		StringBuilder builder = new StringBuilder();
		Iterator<Map.Entry<String, SeparationColumnType>> iterator = settings.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, SeparationColumnType> setting = iterator.next();
			builder.append(setting.getKey());
			builder.append(SEPARATOR_ENTRY);
			builder.append(setting.getValue().name());
			if(iterator.hasNext()) {
				builder.append(SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				String[] parsedItems = parseString(line.trim());
				if(parsedItems.length > 0) {
					for(String item : parsedItems) {
						addItem(item);
					}
				}
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	public boolean exportItems(File file) {

		try (PrintWriter printWriter = new PrintWriter(file)) {
			String settings = extractSettings(this);
			printWriter.println(settings.replace(SEPARATOR_TOKEN, OperatingSystemUtils.getLineDelimiter()));
			printWriter.flush();
			return true;
		} catch(FileNotFoundException e) {
			logger.warn(e);
			return false;
		}
	}

	private void loadSettings(String items) {

		if(!"".equals(items)) {
			String[] parsedItems = parseString(items);
			if(parsedItems.length > 0) {
				for(String item : parsedItems) {
					addItem(item);
				}
			}
		}
	}

	private void addItem(String item) {

		String[] values = item.split("\\" + SEPARATOR_ENTRY);
		if(values.length == 2) {
			try {
				SeparationColumnType separationColumnType = SeparationColumnType.valueOf(values[1].trim());
				put(values[0].trim(), separationColumnType);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
	}

	private String[] parseString(String stringList) {

		String lineDelimiterSpecific = OperatingSystemUtils.getLineDelimiter();
		String lineDelimiterGeneric = LINE_DELIMITER;
		//
		String[] decodedArray;
		if(stringList.contains(SEPARATOR_TOKEN)) {
			decodedArray = stringList.split(SEPARATOR_TOKEN);
		} else if(stringList.contains(lineDelimiterSpecific)) {
			decodedArray = stringList.split(lineDelimiterSpecific);
		} else if(stringList.contains(lineDelimiterGeneric)) {
			decodedArray = stringList.split(lineDelimiterGeneric);
		} else {
			decodedArray = new String[1];
			decodedArray[0] = stringList;
		}
		//
		return decodedArray;
	}
}