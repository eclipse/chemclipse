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
package org.eclipse.chemclipse.model.traces;

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
import org.eclipse.chemclipse.support.util.NamedTraceListUtil;

public class NamedTraces {

	private static final Logger logger = Logger.getLogger(NamedTraces.class);
	//
	private NamedTraceListUtil namedTraceListUtil = new NamedTraceListUtil();
	private final Map<String, NamedTrace> namedTraceMap = new HashMap<>();

	public NamedTraces() {
	}

	/**
	 * Initializes this named traces from the given settings.
	 * 
	 * @param namedTraces
	 */
	public NamedTraces(String namedTraces) {
		load(namedTraces);
	}

	public void addAll(Collection<NamedTrace> NamedTraces) {

		for(NamedTrace namedTrace : NamedTraces) {
			add(namedTrace);
		}
	}

	public void add(NamedTrace namedTrace) {

		namedTraceMap.put(namedTrace.getIdentifier(), namedTrace);
	}

	public void remove(String identifier) {

		namedTraceMap.remove(identifier);
	}

	public void remove(List<NamedTrace> namedTraces) {

		for(NamedTrace namedTrace : namedTraces) {
			remove(namedTrace);
		}
	}

	public void remove(NamedTrace namedTrace) {

		if(namedTrace != null) {
			namedTraceMap.remove(namedTrace.getIdentifier());
		}
	}

	public NamedTrace get(String identifier) {

		return namedTraceMap.get(identifier);
	}

	public Set<String> keySet() {

		return namedTraceMap.keySet();
	}

	public Collection<NamedTrace> values() {

		return namedTraceMap.values();
	}

	public void clear() {

		namedTraceMap.clear();
	}

	public String extractNamedTrace(NamedTrace namedTrace) {

		StringBuilder builder = new StringBuilder();
		extractNamedTrace(namedTrace, builder);
		return builder.toString();
	}

	public NamedTrace extractNamedTrace(String item) {

		NamedTrace namedTrace = null;
		//
		if(!"".equals(item)) {
			String[] values = item.split("\\" + NamedTraceListUtil.SEPARATOR_ENTRY);
			String identifier = ((values.length > 0) ? values[0].trim() : "");
			String traces = ((values.length > 1) ? values[1].trim() : "");
			namedTrace = new NamedTrace(identifier, traces);
		}
		//
		return namedTrace;
	}

	public void load(String timeRanges) {

		loadSettings(timeRanges);
	}

	public void loadDefault(String timeRanges) {

		loadSettings(timeRanges);
	}

	public String save() {

		StringBuilder builder = new StringBuilder();
		Iterator<NamedTrace> iterator = values().iterator();
		while(iterator.hasNext()) {
			NamedTrace namedTrace = iterator.next();
			extractNamedTrace(namedTrace, builder);
			if(iterator.hasNext()) {
				builder.append(NamedTraceListUtil.SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				NamedTrace template = extractNamedTrace(line);
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
			List<NamedTrace> namedTraces = new ArrayList<>(values());
			Collections.sort(namedTraces, (r1, r2) -> r1.getIdentifier().compareTo(r2.getIdentifier()));
			//
			Iterator<NamedTrace> iterator = namedTraces.iterator();
			while(iterator.hasNext()) {
				StringBuilder builder = new StringBuilder();
				NamedTrace template = iterator.next();
				extractNamedTrace(template, builder);
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
			String[] items = namedTraceListUtil.parseString(timeRanges);
			if(items.length > 0) {
				for(String item : items) {
					NamedTrace namedTrace = extractNamedTrace(item);
					if(namedTrace != null) {
						add(namedTrace);
					}
				}
			}
		}
	}

	private void extractNamedTrace(NamedTrace namedTrace, StringBuilder builder) {

		builder.append(namedTrace.getIdentifier());
		builder.append(" ");
		builder.append(NamedTraceListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(namedTrace.getTraces());
	}
}
