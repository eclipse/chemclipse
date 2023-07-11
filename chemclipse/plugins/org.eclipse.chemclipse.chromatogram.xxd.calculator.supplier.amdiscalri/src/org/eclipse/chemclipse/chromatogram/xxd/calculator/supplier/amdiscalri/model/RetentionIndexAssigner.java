/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.validators.RetentionIndexAssignerValidator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.core.runtime.IStatus;

public class RetentionIndexAssigner extends HashSet<IndexNameMarker> {

	private static final Logger logger = Logger.getLogger(RetentionIndexAssigner.class);
	//
	public static final String DESCRIPTION = "Retention Index Assigner";
	public static final String FILE_EXTENSION = ".ria";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
	private static final long serialVersionUID = -6838825644374711900L;
	//
	private RetentionIndexAssignerValidator validator = new RetentionIndexAssignerValidator();

	public Set<String> keySet() {

		Set<String> keys = new HashSet<>();
		for(IndexNameMarker setting : this) {
			keys.add(setting.getName());
		}
		return keys;
	}

	@Override
	public boolean add(IndexNameMarker setting) {

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

	public String extractSetting(IndexNameMarker setting) {

		List<IndexNameMarker> settings = new ArrayList<>();
		settings.add(setting);
		return extractSettings(settings);
	}

	public String extractSettings(Collection<IndexNameMarker> settings) {

		StringBuilder builder = new StringBuilder();
		List<IndexNameMarker> indices = new ArrayList<>(settings);
		Collections.sort(indices, (i1, i2) -> Integer.compare(i1.getRetentionIndex(), i2.getRetentionIndex()));
		//
		Iterator<IndexNameMarker> iterator = indices.iterator();
		while(iterator.hasNext()) {
			IndexNameMarker setting = iterator.next();
			extractSetting(setting, builder);
			if(iterator.hasNext()) {
				builder.append(RetentionIndexAssignerValidator.SEPARATOR_TOKEN);
			}
		}
		return builder.toString().trim();
	}

	public IndexNameMarker extractSettingInstance(String item) {

		return extract(item);
	}

	public void importItems(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = bufferedReader.readLine()) != null) {
				IndexNameMarker setting = extract(line);
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
			List<IndexNameMarker> settings = new ArrayList<>(this);
			Collections.sort(settings, (r1, r2) -> Integer.compare(r1.getRetentionIndex(), r2.getRetentionIndex()));
			//
			for(IndexNameMarker setting : settings) {
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

	private IndexNameMarker extract(String text) {

		IndexNameMarker setting = null;
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
					IndexNameMarker setting = extractSettingInstance(item);
					if(setting != null) {
						add(setting);
					}
				}
			}
		}
	}

	private void extractSetting(IndexNameMarker setting, StringBuilder builder) {

		List<String> entries = new ArrayList<>();
		//
		entries.add(Integer.toString(setting.getRetentionIndex()));
		entries.add(getFormattedString(setting.getName()));
		//
		compile(builder, entries);
	}

	private String getFormattedString(String value) {

		return value.replace(RetentionIndexAssignerValidator.SEPARATOR_TOKEN, RetentionIndexAssignerValidator.WHITE_SPACE).replace(RetentionIndexAssignerValidator.SEPARATOR_ENTRY, RetentionIndexAssignerValidator.WHITE_SPACE);
	}

	private String[] parseString(String list) {

		String osDelimiterSpecific = OperatingSystemUtils.getLineDelimiter();
		String osDelimiterGeneric = "\n";
		//
		String[] array;
		if(list.contains(RetentionIndexAssignerValidator.SEPARATOR_TOKEN)) {
			array = list.split(RetentionIndexAssignerValidator.SEPARATOR_TOKEN);
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
				builder.append(RetentionIndexAssignerValidator.WHITE_SPACE);
				builder.append(RetentionIndexAssignerValidator.SEPARATOR_ENTRY);
				builder.append(RetentionIndexAssignerValidator.WHITE_SPACE);
			}
		}
	}
}