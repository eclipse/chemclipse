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
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

public class ChannelMappings extends ArrayList<ChannelMapping> {

	public static final String WHITE_SPACE = " ";
	public static final String SEPARATOR_TOKEN = ";";
	public static final String SEPARATOR_ENTRY = "|";
	//
	private static final Logger logger = Logger.getLogger(ChannelMappings.class);
	private static final long serialVersionUID = 8201753784134562323L;

	@Override
	public boolean add(ChannelMapping mapping) {

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

	public String extractSetting(ChannelMapping setting) {

		List<ChannelMapping> mapping = new ArrayList<>();
		mapping.add(setting);
		return extractMappings(mapping);
	}

	public String extractMappings(Collection<ChannelMapping> mappings) {

		StringBuilder builder = new StringBuilder();
		Iterator<ChannelMapping> iterator = mappings.iterator();
		while(iterator.hasNext()) {
			ChannelMapping setting = iterator.next();
			extractSetting(setting, builder);
			if(iterator.hasNext()) {
				builder.append(SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public ChannelMapping extractChannelMapping(String item) {

		return extract(item);
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				ChannelMapping mapping = extract(line);
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
			List<ChannelMapping> mappings = new ArrayList<>(this);
			for(ChannelMapping mapping : mappings) {
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

	private ChannelMapping extract(String text) {

		String[] values = StringUtils.split(text, SEPARATOR_ENTRY);
		if(values.length == 3) {
			String subset = values[0].trim();
			int channel = Integer.parseInt(values[1].trim());
			String label = values[2].trim();
			return new ChannelMapping(subset, channel, label);
		} else {
			logger.warn(String.format("Channel mapping with %s parameters", values.length));
		}
		return null;
	}

	private void loadSettings(String items) {

		if(!"".equals(items)) {
			String[] parsedItems = parseString(items);
			if(parsedItems.length > 0) {
				for(String item : parsedItems) {
					ChannelMapping setting = extractChannelMapping(item);
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

	private void extractSetting(ChannelMapping mapping, StringBuilder builder) {

		List<String> entries = new ArrayList<>();
		//
		entries.add(mapping.getSubset());
		entries.add(String.valueOf(mapping.getChannel()));
		entries.add(mapping.getLabel());
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