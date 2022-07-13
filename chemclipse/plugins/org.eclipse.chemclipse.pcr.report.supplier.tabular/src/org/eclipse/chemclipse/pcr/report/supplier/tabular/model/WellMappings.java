/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.Utils;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

public class WellMappings extends ArrayList<WellMapping> {

	public static final String WHITE_SPACE = " ";
	public static final String SEPARATOR_TOKEN = ";";
	public static final String SEPARATOR_ENTRY = "|";
	//
	private static final Logger logger = Logger.getLogger(WellMappings.class);
	private static final long serialVersionUID = 8201753784134562324L;

	@Override
	public boolean add(WellMapping mapping) {

		if(mapping != null) {
			return super.add(mapping);
		}
		return false;
	}

	public void load(String items) {

		loadSettings(items);
	}

	public void loadDefault(String items) {

		loadSettings(items);
	}

	public String save() {

		return extractMappings(this);
	}

	public String extractSetting(WellMapping setting) {

		List<WellMapping> mapping = new ArrayList<>();
		mapping.add(setting);
		return extractMappings(mapping);
	}

	public String extractMappings(Collection<WellMapping> mappings) {

		StringBuilder builder = new StringBuilder();
		Iterator<WellMapping> iterator = mappings.iterator();
		while(iterator.hasNext()) {
			WellMapping setting = iterator.next();
			extractSetting(setting, builder);
			if(iterator.hasNext()) {
				builder.append(SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public WellMapping extractWellMapping(String item) {

		return extract(item);
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				WellMapping mapping = extract(line);
				if(mapping != null) {
					add(mapping);
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
			List<WellMapping> mappings = new ArrayList<>(this);
			for(WellMapping mapping : mappings) {
				StringBuilder builder = new StringBuilder();
				extractSetting(mapping, builder);
				printWriter.println(builder.toString());
			}
			printWriter.flush();
			return true;
		} catch(FileNotFoundException e) {
			logger.warn(e);
			return false;
		}
	}

	private WellMapping extract(String text) {

		String[] values = StringUtils.split(text, SEPARATOR_ENTRY);
		if(values.length == 6) {
			String subset = values[0].trim();
			String sample = values[1].trim();
			int[] channels = Utils.parseChannels(values[2]);
			int cutoff = Integer.parseInt(values[3].trim());
			String positive = values[4].trim();
			String negative = values[5].trim();
			return new WellMapping(subset, sample, channels, cutoff, positive, negative);
		} else {
			logger.warn(String.format("Well mapping with %s parameters", values.length));
		}
		return null;
	}

	private void loadSettings(String items) {

		if(!"".equals(items)) {
			String[] parsedItems = parseString(items);
			if(parsedItems.length > 0) {
				for(String item : parsedItems) {
					WellMapping setting = extractWellMapping(item);
					if(setting != null) {
						add(setting);
					}
				}
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
		return decodedArray;
	}

	private void extractSetting(WellMapping mapping, StringBuilder builder) {

		List<String> entries = new ArrayList<>();
		//
		entries.add(mapping.getSubset());
		entries.add(mapping.getSample());
		entries.add(mapping.getChannelString());
		entries.add(String.valueOf(mapping.getCutoff()));
		entries.add(mapping.getPositive());
		entries.add(mapping.getNegative());
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
}