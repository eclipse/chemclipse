/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.model;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.graphics.Color;

public class ColorCodes extends HashMap<String, ColorCode> {

	public static final String ENTRY_DELIMITER = ";";
	public static final String VALUE_DELIMITER = ":";
	//
	private static final long serialVersionUID = 5166193750159157107L;
	private static final Logger logger = Logger.getLogger(ColorCodes.class);

	public void add(ColorCode colorCode) {

		if(colorCode != null) {
			put(colorCode.getName(), colorCode);
		}
	}

	public void load(String codes) {

		loadSettings(codes);
	}

	public void loadDefault(String codes) {

		loadSettings(codes);
	}

	public String save() {

		StringBuilder builder = new StringBuilder();
		if(size() >= 1) {
			for(ColorCode colorCode : values()) {
				builder.append(colorCode.getName());
				builder.append(VALUE_DELIMITER);
				builder.append(Colors.getColor(colorCode.getColor()));
				builder.append(ENTRY_DELIMITER);
			}
		}
		return builder.toString().trim();
	}

	private void loadSettings(String codes) {

		if(!"".equals(codes)) {
			String[] items = parseString(codes);
			if(items.length > 0) {
				for(String item : items) {
					try {
						String[] values = item.split(VALUE_DELIMITER);
						if(values.length > 1) {
							String name = values[0];
							Color color = Colors.getColor(values[1]);
							ColorCode colorCode = new ColorCode(name, color);
							add(colorCode);
						}
					} catch(NumberFormatException e) {
						logger.warn(e);
					}
				}
			}
		}
	}

	private static String[] parseString(String string) {

		String[] decodedArray = new String[0];
		//
		if(string.contains(ENTRY_DELIMITER)) {
			StringTokenizer stringTokenizer = new StringTokenizer(string, ENTRY_DELIMITER);
			int arraySize = stringTokenizer.countTokens();
			decodedArray = new String[arraySize];
			for(int i = 0; i < arraySize; i++) {
				decodedArray[i] = stringTokenizer.nextToken(ENTRY_DELIMITER);
			}
		} else {
			if(string.contains(VALUE_DELIMITER)) {
				decodedArray = new String[1];
				decodedArray[0] = string;
			}
		}
		return decodedArray;
	}
}
