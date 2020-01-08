/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.ranges;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.util.TimeRangeListUtil;

public class TimeRanges {

	private static final Logger logger = Logger.getLogger(TimeRanges.class);
	//
	private TimeRangeListUtil timeRangeListUtil = new TimeRangeListUtil();
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
	/*
	 * The map is used to have an easier access to the stored time ranges.
	 * To keep the map and time range instances in sync, this put method
	 * is used.
	 */
	private final Map<String, TimeRange> timeRangeMap = new HashMap<>();

	public TimeRanges() {
	}

	/**
	 * Initializes this ranges from the given settings.
	 * 
	 * @param timeRanges
	 */
	public TimeRanges(String timeRanges) {
		load(timeRanges);
	}

	public void addAll(Collection<TimeRange> timeRanges) {

		for(TimeRange timeRange : timeRanges) {
			add(timeRange);
		}
	}

	public void add(TimeRange timeRange) {

		timeRangeMap.put(timeRange.getIdentifier(), timeRange);
	}

	public void remove(String identifier) {

		timeRangeMap.remove(identifier);
	}

	public void remove(TimeRange timeRange) {

		if(timeRange != null) {
			timeRangeMap.remove(timeRange.getIdentifier());
		}
	}

	public TimeRange get(String identifier) {

		return timeRangeMap.get(identifier);
	}

	public Set<String> keySet() {

		return timeRangeMap.keySet();
	}

	public Collection<TimeRange> values() {

		return timeRangeMap.values();
	}

	public void clear() {

		timeRangeMap.clear();
	}

	public String extractTimeRange(TimeRange timeRange) {

		StringBuilder builder = new StringBuilder();
		extractTimeRange(timeRange, builder);
		return builder.toString();
	}

	public TimeRange extractTimeRange(String item) {

		TimeRange timeRange = null;
		//
		if(!"".equals(item)) {
			String[] values = item.split("\\" + TimeRangeListUtil.SEPARATOR_ENTRY);
			String identifier = ((values.length > 0) ? values[0].trim() : "");
			int start = ((values.length > 1) ? calculateRetentionTime(values[1]) : 0);
			int center = ((values.length > 2) ? calculateRetentionTime(values[2]) : 0);
			int stop = ((values.length > 3) ? calculateRetentionTime(values[3]) : 0);
			timeRange = new TimeRange(identifier, start, center, stop);
		}
		//
		return timeRange;
	}

	public void load(String timeRanges) {

		loadSettings(timeRanges);
	}

	public void loadDefault(String timeRanges) {

		loadSettings(timeRanges);
	}

	public String save() {

		StringBuilder builder = new StringBuilder();
		Iterator<TimeRange> iterator = values().iterator();
		while(iterator.hasNext()) {
			TimeRange timeRange = iterator.next();
			extractTimeRange(timeRange, builder);
			if(iterator.hasNext()) {
				builder.append(TimeRangeListUtil.SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				TimeRange template = extractTimeRange(line);
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

		try {
			PrintWriter printWriter = new PrintWriter(file);
			Iterator<TimeRange> iterator = values().iterator();
			while(iterator.hasNext()) {
				StringBuilder builder = new StringBuilder();
				TimeRange template = iterator.next();
				extractTimeRange(template, builder);
				printWriter.println(builder.toString());
			}
			printWriter.flush();
			printWriter.close();
			return true;
		} catch(FileNotFoundException e) {
			logger.warn(e);
			return false;
		}
	}

	private void loadSettings(String timeRanges) {

		if(!"".equals(timeRanges)) {
			String[] items = timeRangeListUtil.parseString(timeRanges);
			if(items.length > 0) {
				for(String item : items) {
					TimeRange timeRange = extractTimeRange(item);
					if(timeRange != null) {
						add(timeRange);
					}
				}
			}
		}
	}

	private int calculateRetentionTime(String minutes) {

		try {
			return (int)(Double.parseDouble(minutes.trim()) * TimeRange.MINUTE_FACTOR);
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	private String calculateRetentionTimeMinutes(int retentionTime) {

		return decimalFormat.format(retentionTime / TimeRange.MINUTE_FACTOR);
	}

	private void extractTimeRange(TimeRange timeRange, StringBuilder builder) {

		builder.append(timeRange.getIdentifier());
		builder.append(" ");
		builder.append(TimeRangeListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(calculateRetentionTimeMinutes(timeRange.getStart()));
		builder.append(" ");
		builder.append(TimeRangeListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(calculateRetentionTimeMinutes(timeRange.getCenter()));
		builder.append(" ");
		builder.append(TimeRangeListUtil.SEPARATOR_ENTRY);
		builder.append(" ");
		builder.append(calculateRetentionTimeMinutes(timeRange.getStop()));
	}
}
