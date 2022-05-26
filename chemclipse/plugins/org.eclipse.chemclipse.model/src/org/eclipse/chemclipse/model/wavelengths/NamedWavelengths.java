/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for DAD
 *******************************************************************************/
package org.eclipse.chemclipse.model.wavelengths;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.util.NamedWavelengthListUtil;

public class NamedWavelengths {

	private static final Logger logger = Logger.getLogger(NamedWavelengths.class);
	//
	private NamedWavelengthListUtil namedWavelengthListUtil = new NamedWavelengthListUtil();
	private final Map<String, NamedWavelength> namedWavelengthMap = new HashMap<>();

	public NamedWavelengths() {

	}

	/**
	 * Initializes this named Wavelengths from the given settings.
	 * 
	 * @param namedWavelengths
	 */
	public NamedWavelengths(String namedWavelengths) {

		load(namedWavelengths);
	}

	public void addAll(Collection<NamedWavelength> namedWavelengths) {

		for(NamedWavelength namedWavelength : namedWavelengths) {
			add(namedWavelength);
		}
	}

	public void add(NamedWavelength namedWavelength) {

		namedWavelengthMap.put(namedWavelength.getIdentifier(), namedWavelength);
	}

	public void remove(String identifier) {

		namedWavelengthMap.remove(identifier);
	}

	public void remove(List<NamedWavelength> namedWavelengths) {

		for(NamedWavelength namedWavelength : namedWavelengths) {
			remove(namedWavelength);
		}
	}

	public void remove(NamedWavelength namedWavelength) {

		if(namedWavelength != null) {
			namedWavelengthMap.remove(namedWavelength.getIdentifier());
		}
	}

	public NamedWavelength get(String identifier) {

		return namedWavelengthMap.get(identifier);
	}

	public Set<String> keySet() {

		return namedWavelengthMap.keySet();
	}

	public Collection<NamedWavelength> values() {

		return namedWavelengthMap.values();
	}

	public void clear() {

		namedWavelengthMap.clear();
	}

	public String extractNamedWavelength(NamedWavelength namedWavelength) {

		StringBuilder builder = new StringBuilder();
		extractNamedWavelength(namedWavelength, builder);
		return builder.toString();
	}

	public NamedWavelength extractNamedWavelength(String item) {

		NamedWavelength namedWavelength = null;
		//
		if(!"".equals(item)) {
			String[] values = item.split("\\" + NamedWavelengthListUtil.SEPARATOR_ENTRY);
			String identifier = ((values.length > 0) ? values[0].trim() : "");
			String Wavelengths = ((values.length > 1) ? values[1].trim() : "");
			namedWavelength = new NamedWavelength(identifier, Wavelengths);
		}
		//
		return namedWavelength;
	}

	public void load(String timeRanges) {

		loadSettings(timeRanges);
	}

	public void loadDefault(String timeRanges) {

		loadSettings(timeRanges);
	}

	public String save() {

		StringBuilder builder = new StringBuilder();
		Iterator<NamedWavelength> iterator = values().iterator();
		while(iterator.hasNext()) {
			NamedWavelength namedWavelength = iterator.next();
			extractNamedWavelength(namedWavelength, builder);
			if(iterator.hasNext()) {
				builder.append(NamedWavelengthListUtil.SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				NamedWavelength template = extractNamedWavelength(line);
				if(template != null) {
					add(template);
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
			/*
			 * Sort the items.
			 */
			List<NamedWavelength> namedWavelengths = new ArrayList<>(values());
			Collections.sort(namedWavelengths, (r1, r2) -> r1.getIdentifier().compareTo(r2.getIdentifier()));
			//
			Iterator<NamedWavelength> iterator = namedWavelengths.iterator();
			while(iterator.hasNext()) {
				StringBuilder builder = new StringBuilder();
				NamedWavelength template = iterator.next();
				extractNamedWavelength(template, builder);
				printWriter.println(builder.toString());
			}
			printWriter.flush();
			return true;
		} catch(FileNotFoundException e) {
			logger.warn(e);
			return false;
		}
	}

	private void loadSettings(String timeRanges) {

		if(!"".equals(timeRanges)) {
			String[] items = namedWavelengthListUtil.parseString(timeRanges);
			if(items.length > 0) {
				for(String item : items) {
					NamedWavelength namedWavelength = extractNamedWavelength(item);
					if(namedWavelength != null) {
						add(namedWavelength);
					}
				}
			}
		}
	}

	private void extractNamedWavelength(NamedWavelength namedWavelength, StringBuilder builder) {

		builder.append(namedWavelength.getIdentifier());
		builder.append(" ");
		builder.append(NamedWavelengthListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(namedWavelength.getWavelengths());
	}
}
