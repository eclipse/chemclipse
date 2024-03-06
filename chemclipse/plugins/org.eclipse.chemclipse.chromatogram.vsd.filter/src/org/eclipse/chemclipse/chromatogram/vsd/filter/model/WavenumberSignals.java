/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.vsd.filter.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.vsd.filter.validators.WavenumberSignalsValidator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.core.runtime.IStatus;

public class WavenumberSignals extends ArrayList<WavenumberSignal> {

	private static final long serialVersionUID = 5390614556584324943L;
	private static final Logger logger = Logger.getLogger(WavenumberSignals.class);
	//
	public static final String DESCRIPTION = "Wavenumber Signals";
	public static final String FILE_EXTENSION = ".wns";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
	public static final String WHITE_SPACE = " ";
	public static final String SEPARATOR_TOKEN = ";";
	public static final String SEPARATOR_ENTRY = "|";
	//
	public static final String DEMO_ENTRY = "200 " + SEPARATOR_ENTRY + " 9999";
	//
	private WavenumberSignalsValidator validator = new WavenumberSignalsValidator();

	public Set<String> keySet() {

		Set<String> keys = new HashSet<>();
		for(WavenumberSignal setting : this) {
			keys.add(Double.toString(setting.getWavenumber()));
		}
		//
		return keys;
	}

	public void load(String items) {

		loadSettings(items);
	}

	public void loadDefault(String items) {

		loadSettings(items);
	}

	public String save() {

		return extractSettings(this);
	}

	public String extractSetting(WavenumberSignal setting) {

		List<WavenumberSignal> settings = new ArrayList<>();
		settings.add(setting);
		return extractSettings(settings);
	}

	public String extractSettings(Collection<WavenumberSignal> settings) {

		StringBuilder builder = new StringBuilder();
		Iterator<WavenumberSignal> iterator = settings.iterator();
		while(iterator.hasNext()) {
			WavenumberSignal setting = iterator.next();
			extractSetting(setting, builder);
			if(iterator.hasNext()) {
				builder.append(SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public WavenumberSignal extractSettingInstance(String item) {

		return extract(item);
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				WavenumberSignal setting = extract(line);
				if(setting != null && !this.contains(setting)) {
					add(setting);
				}
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	public boolean exportItems(File file) {

		boolean success = false;
		try (PrintWriter printWriter = new PrintWriter(file)) {
			//
			List<WavenumberSignal> settings = new ArrayList<>(this);
			Collections.sort(settings, (s1, s2) -> Double.compare(s1.getWavenumber(), s2.getWavenumber()));
			//
			for(WavenumberSignal setting : settings) {
				StringBuilder builder = new StringBuilder();
				extractSetting(setting, builder);
				printWriter.println(builder.toString());
			}
			printWriter.flush();
			success = true;
		} catch(FileNotFoundException e) {
			logger.warn(e);
		}
		//
		return success;
	}

	private WavenumberSignal extract(String text) {

		WavenumberSignal setting = null;
		//
		IStatus status = validator.validate(text);
		if(status.isOK()) {
			setting = validator.getSetting();
		} else {
			logger.warn(status.getMessage());
		}
		//
		return setting;
	}

	private void loadSettings(String items) {

		if(!"".equals(items)) {
			String[] parsedItems = parseString(items);
			if(parsedItems.length > 0) {
				for(String item : parsedItems) {
					WavenumberSignal setting = extractSettingInstance(item);
					if(setting != null && !this.contains(setting)) {
						add(setting);
					}
				}
			}
		}
	}

	private void extractSetting(WavenumberSignal setting, StringBuilder builder) {

		List<String> entries = new ArrayList<>();
		//
		entries.add(Double.toString(setting.getWavenumber()));
		entries.add(Double.toString(setting.getIntensity()));
		//
		compile(builder, entries);
	}

	private void compile(StringBuilder builder, List<String> entries) {

		Iterator<String> iterator = entries.iterator();
		while(iterator.hasNext()) {
			builder.append(iterator.next());
			if(iterator.hasNext()) {
				builder.append(WHITE_SPACE);
				builder.append(SEPARATOR_ENTRY);
				builder.append(WHITE_SPACE);
			}
		}
	}

	private String[] parseString(String stringList) {

		String lineDelimiterSpecific = OperatingSystemUtils.getLineDelimiter();
		String lineDelimiterGeneric = "\n";
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