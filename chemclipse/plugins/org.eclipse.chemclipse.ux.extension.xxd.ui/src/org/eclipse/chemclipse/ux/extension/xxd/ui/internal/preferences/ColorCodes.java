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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.preferences;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;

public class ColorCodes extends HashMap<String, ColorCode> {

	private static final long serialVersionUID = 5166193750159157107L;
	private static final Logger logger = Logger.getLogger(ColorCodes.class);
	public static final String ENTRY_DELIMITER = ";";
	public static final String VALUE_DELIMITER = ":";
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public void add(ColorCode colorCode) {

		if(colorCode != null) {
			put(colorCode.getName(), colorCode);
		}
	}

	public void load() {

		String codes = preferenceStore.getString(PreferenceConstants.DEF_COLOR_CODES);
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

	public boolean save() {

		StringBuilder builder = new StringBuilder();
		if(size() >= 1) {
			for(ColorCode colorCode : values()) {
				builder.append(colorCode.getName());
				builder.append(VALUE_DELIMITER);
				builder.append(Colors.getColor(colorCode.getColor()));
				builder.append(ENTRY_DELIMITER);
			}
		}
		preferenceStore.putValue(PreferenceConstants.DEF_COLOR_CODES, builder.toString());
		return true;
	}

	private static String[] parseString(String stringList) {

		String[] decodedArray;
		if(stringList.contains(ENTRY_DELIMITER)) {
			StringTokenizer stringTokenizer = new StringTokenizer(stringList, ENTRY_DELIMITER);
			int arraySize = stringTokenizer.countTokens();
			decodedArray = new String[arraySize];
			for(int i = 0; i < arraySize; i++) {
				decodedArray[i] = stringTokenizer.nextToken(ENTRY_DELIMITER);
			}
		} else {
			decodedArray = new String[1];
			decodedArray[0] = stringList;
		}
		return decodedArray;
	}
}
