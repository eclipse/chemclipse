/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.validators.RetentionIndexEntryValidator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IStatus;

public class RetentionIndexMarker extends HashSet<IRetentionIndexEntry> {

	private static final Logger logger = Logger.getLogger(RetentionIndexMarker.class);
	//
	public static final String DESCRIPTION = "Retention Index Marker";
	public static final String FILE_EXTENSION = ".rim";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
	private static final long serialVersionUID = 7506362009667971730L;
	//
	private RetentionIndexEntryValidator validator = new RetentionIndexEntryValidator();

	public Set<String> keySet() {

		Set<String> keys = new HashSet<>();
		for(IRetentionIndexEntry setting : this) {
			keys.add(setting.getName());
		}
		return keys;
	}

	@Override
	public boolean add(IRetentionIndexEntry setting) {

		if(setting != null) {
			return super.add(setting);
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

		return extractSettings(this);
	}

	public String extractSetting(IRetentionIndexEntry setting) {

		List<IRetentionIndexEntry> settings = new ArrayList<>();
		settings.add(setting);
		return extractSettings(settings);
	}

	public String extractSettings(Collection<IRetentionIndexEntry> settings) {

		StringBuilder builder = new StringBuilder();
		Iterator<IRetentionIndexEntry> iterator = settings.iterator();
		while(iterator.hasNext()) {
			IRetentionIndexEntry setting = iterator.next();
			extractSetting(setting, builder);
			if(iterator.hasNext()) {
				builder.append(RetentionIndexEntryValidator.SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public IRetentionIndexEntry extractSettingInstance(String item) {

		return extract(item);
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				IRetentionIndexEntry setting = extract(line);
				if(setting != null) {
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
			List<IRetentionIndexEntry> settings = new ArrayList<>(this);
			Collections.sort(settings, (r1, r2) -> Integer.compare(r1.getRetentionTime(), r2.getRetentionTime()));
			//
			for(IRetentionIndexEntry setting : settings) {
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

	private IRetentionIndexEntry extract(String text) {

		IRetentionIndexEntry setting = null;
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
					IRetentionIndexEntry setting = extractSettingInstance(item);
					if(setting != null) {
						add(setting);
					}
				}
			}
		}
	}

	private void extractSetting(IRetentionIndexEntry setting, StringBuilder builder) {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0000");
		List<String> entries = new ArrayList<>();
		//
		entries.add(decimalFormat.format(setting.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR)); // Minutes
		entries.add(decimalFormat.format(setting.getRetentionIndex()));
		entries.add(getFormattedString(setting.getName()));
		//
		compile(builder, entries);
	}

	private String getFormattedString(String value) {

		return value.replace(RetentionIndexEntryValidator.SEPARATOR_TOKEN, RetentionIndexEntryValidator.WHITE_SPACE).replace(RetentionIndexEntryValidator.SEPARATOR_ENTRY, RetentionIndexEntryValidator.WHITE_SPACE);
	}

	private String[] parseString(String list) {

		String osDelimiterSpecific = OperatingSystemUtils.getLineDelimiter();
		String osDelimiterGeneric = "\n";
		//
		String[] array;
		if(list.contains(RetentionIndexEntryValidator.SEPARATOR_TOKEN)) {
			array = list.split(RetentionIndexEntryValidator.SEPARATOR_TOKEN);
		} else if(list.contains(osDelimiterSpecific)) {
			array = list.split(osDelimiterSpecific);
		} else if(list.contains(osDelimiterGeneric)) {
			array = list.split(osDelimiterGeneric);
		} else {
			array = new String[1];
			array[0] = list;
		}
		//
		return array;
	}

	private void compile(StringBuilder builder, List<String> entries) {

		Iterator<String> iterator = entries.iterator();
		while(iterator.hasNext()) {
			builder.append(iterator.next());
			if(iterator.hasNext()) {
				builder.append(RetentionIndexEntryValidator.WHITE_SPACE);
				builder.append(RetentionIndexEntryValidator.SEPARATOR_ENTRY);
				builder.append(RetentionIndexEntryValidator.WHITE_SPACE);
			}
		}
	}
}