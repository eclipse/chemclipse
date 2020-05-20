/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.instruments;

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
import org.eclipse.chemclipse.support.util.InstrumentListUtil;

public class Instruments {

	private static final Logger logger = Logger.getLogger(Instruments.class);
	//
	private InstrumentListUtil instrumentListUtil = new InstrumentListUtil();
	private final Map<String, Instrument> instrumentMap = new HashMap<>();

	public Instruments() {

	}

	public Instruments(String instruments) {

		load(instruments);
	}

	public void addAll(Collection<Instrument> instruments) {

		for(Instrument instrument : instruments) {
			add(instrument);
		}
	}

	public void add(Instrument instrument) {

		instrumentMap.put(instrument.getIdentifier(), instrument);
	}

	public void remove(String identifier) {

		instrumentMap.remove(identifier);
	}

	public void remove(List<Instrument> instruments) {

		for(Instrument instrument : instruments) {
			remove(instrument);
		}
	}

	public void remove(Instrument instrument) {

		if(instrument != null) {
			instrumentMap.remove(instrument.getIdentifier());
		}
	}

	public Instrument get(String identifier) {

		return instrumentMap.get(identifier);
	}

	public Set<String> keySet() {

		return instrumentMap.keySet();
	}

	public Collection<Instrument> values() {

		return instrumentMap.values();
	}

	public void clear() {

		instrumentMap.clear();
	}

	public String extractInstrument(Instrument instrument) {

		StringBuilder builder = new StringBuilder();
		extractInstrument(instrument, builder);
		return builder.toString();
	}

	public Instrument extractInstrument(String item) {

		Instrument instrument = null;
		//
		if(!"".equals(item)) {
			String[] values = item.split("\\" + InstrumentListUtil.SEPARATOR_ENTRY);
			String identifier = ((values.length > 0) ? values[0].trim() : "");
			String name = ((values.length > 1) ? values[1].trim() : "");
			String description = ((values.length > 2) ? values[2].trim() : "");
			instrument = new Instrument(identifier, name, description);
		}
		//
		return instrument;
	}

	public void load(String instruments) {

		loadSettings(instruments);
	}

	public void loadDefault(String instruments) {

		loadSettings(instruments);
	}

	public String save() {

		StringBuilder builder = new StringBuilder();
		Iterator<Instrument> iterator = values().iterator();
		while(iterator.hasNext()) {
			Instrument instrument = iterator.next();
			extractInstrument(instrument, builder);
			if(iterator.hasNext()) {
				builder.append(InstrumentListUtil.SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				Instrument template = extractInstrument(line);
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
			List<Instrument> instruments = new ArrayList<>(values());
			Collections.sort(instruments, (r1, r2) -> r1.getIdentifier().compareTo(r2.getIdentifier()));
			//
			Iterator<Instrument> iterator = instruments.iterator();
			while(iterator.hasNext()) {
				StringBuilder builder = new StringBuilder();
				Instrument template = iterator.next();
				extractInstrument(template, builder);
				printWriter.println(builder.toString());
			}
			printWriter.flush();
			return true;
		} catch(FileNotFoundException e) {
			logger.warn(e);
			return false;
		}
	}

	private void loadSettings(String instruments) {

		if(!"".equals(instruments)) {
			String[] items = instrumentListUtil.parseString(instruments);
			if(items.length > 0) {
				for(String item : items) {
					Instrument instrument = extractInstrument(item);
					if(instrument != null) {
						add(instrument);
					}
				}
			}
		}
	}

	private void extractInstrument(Instrument instrument, StringBuilder builder) {

		builder.append(instrument.getIdentifier());
		builder.append(" ");
		builder.append(InstrumentListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(instrument.getName());
		builder.append(" ");
		builder.append(InstrumentListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(instrument.getDescription());
	}
}
