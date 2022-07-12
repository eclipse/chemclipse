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

public class VirtualChannels extends ArrayList<VirtualChannel> {

	public static final String WHITE_SPACE = " ";
	public static final String SEPARATOR_TOKEN = ";";
	public static final String SEPARATOR_ENTRY = "|";
	//
	private static final Logger logger = Logger.getLogger(VirtualChannels.class);
	private static final long serialVersionUID = 8201753784134562323L;

	@Override
	public boolean add(VirtualChannel virtualChannel) {

		if(virtualChannel != null) {
			return super.add(virtualChannel);
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

		return extractChannels(this);
	}

	public String extractSetting(VirtualChannel setting) {

		List<VirtualChannel> mapping = new ArrayList<>();
		mapping.add(setting);
		return extractChannels(mapping);
	}

	public String extractChannels(Collection<VirtualChannel> channels) {

		StringBuilder builder = new StringBuilder();
		Iterator<VirtualChannel> iterator = channels.iterator();
		while(iterator.hasNext()) {
			VirtualChannel setting = iterator.next();
			extractSetting(setting, builder);
			if(iterator.hasNext()) {
				builder.append(SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public VirtualChannel extractVirtualChannel(String item) {

		return extract(item);
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				VirtualChannel virtualChannel = extract(line);
				if(virtualChannel != null) {
					add(virtualChannel);
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
			List<VirtualChannel> channels = new ArrayList<>(this);
			for(VirtualChannel channel : channels) {
				StringBuilder builder = new StringBuilder();
				extractSetting(channel, builder);
				printWriter.println(builder.toString());
			}
			printWriter.flush();
			return true;
		} catch(FileNotFoundException e) {
			logger.warn(e);
			return false;
		}
	}

	private VirtualChannel extract(String text) {

		String[] values = StringUtils.split(text, SEPARATOR_ENTRY);
		if(values.length == 4) {
			String subset = values[0].trim();
			String sample = values[1].trim();
			String channels = values[2].trim();
			int[] source = Utils.parseLogicalChannels(channels);
			LogicalOperator operator = LogicalOperator.parse(channels);
			int target = Integer.parseInt(values[3].trim());
			return new VirtualChannel(subset, sample, source, operator, target);
		} else {
			logger.warn(String.format("Virtual channel with %s parameters", values.length));
		}
		return null;
	}

	private void loadSettings(String items) {

		if(!"".equals(items)) {
			String[] parsedItems = parseString(items);
			if(parsedItems.length > 0) {
				for(String item : parsedItems) {
					VirtualChannel setting = extractVirtualChannel(item);
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

	private void extractSetting(VirtualChannel channel, StringBuilder builder) {

		List<String> entries = new ArrayList<>();
		//
		entries.add(channel.getSubset());
		entries.add(channel.getSample());
		entries.add(channel.getSourceChannelString());
		entries.add(String.valueOf(channel.getTargetChannel()));
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